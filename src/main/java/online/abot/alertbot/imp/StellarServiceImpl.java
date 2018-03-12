package online.abot.alertbot.imp;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import online.abot.alertbot.constant.Constants;
import online.abot.alertbot.domian.Binding;
import online.abot.alertbot.mapper.BindingMapper;
import online.abot.alertbot.service.MappingService;
import online.abot.alertbot.service.ImService;
import online.abot.alertbot.service.StellarService;
import org.apache.log4j.Logger;
import org.glassfish.jersey.media.sse.EventListener;
import org.glassfish.jersey.media.sse.EventSource;
import org.glassfish.jersey.media.sse.InboundEvent;
import org.glassfish.jersey.media.sse.SseFeature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import javax.annotation.PostConstruct;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Service
public class StellarServiceImpl implements StellarService {

    private static final Logger LOGGER = Logger.getLogger(StellarServiceImpl.class);
    private static final String TRADES_URL = "https://horizon.stellar.org/trades?cursor=%s&limit=200";

    @Autowired
    @Qualifier("QqService")
    ImService qqService;

    @Autowired
    @Qualifier("TelegramService")
    ImService telegramService;

    @Autowired
    MappingService mappingService;

    @Autowired
    BindingMapper bindingMapper;

    private final RestTemplate restTemplate;

    private String cursor;

    public StellarServiceImpl(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();

    }


    @PostConstruct
    private void init(){
        Client client = ClientBuilder.newBuilder()
                .register(SseFeature.class).build();
        WebTarget target = client.target("https://horizon.stellar.org/operations?cursor=now");
        EventSource eventSource = EventSource.target(target).build();
        EventListener listener = new EventListener() {
            @Override
            public void onEvent(InboundEvent inboundEvent) {
                dataHandling(inboundEvent.readData(String.class));
            }
        };
        eventSource.register(listener);
        eventSource.open();


        if(cursor==null){
            String cursorUrl="https://horizon.stellar.org/trades?limit=1&order=desc";
            String result = restTemplate.getForObject(cursorUrl, String.class);
            //LOGGER.info("First trade: " + result);
            JSONObject jsonObject = JSONObject.parseObject(result);
            JSONObject embedded = jsonObject.getJSONObject("_embedded");
            JSONArray records = embedded.getJSONArray("records");
            JSONObject trade = (JSONObject)records.get(0);
            cursor = trade.getString("paging_token");
            //LOGGER.info("Cursor: " + cursor);
        }
    }

    @Scheduled(fixedRate = 3000)
    public void getTrades() {
        String requestUrl = String.format(TRADES_URL, cursor);
        //LOGGER.info(requestUrl);
        String result = restTemplate.getForObject(requestUrl, String.class);
        //System.out.println("result: " + result);
        JSONObject jsonObject = JSONObject.parseObject(result);
        JSONObject embedded = jsonObject.getJSONObject("_embedded");
        JSONArray records = embedded.getJSONArray("records");
        if (records!=null&&records.size()!=0) {
            JSONObject trade = (JSONObject) records.get(0);
            cursor = trade.getString("paging_token");//get the newest cursor
            LOGGER.info("Cursor: " + cursor);
        }
        Flux.fromIterable(records).subscribeOn(Schedulers.elastic()).subscribe(this::handleTradesData);
    }


    @Override
    public boolean subscribe(Binding binding) {
       /* try {
            String accountId = binding.getAccountId();
            Client client = ClientBuilder.newBuilder().build();
            String url = String.format("https://horizon.stellar.org/accounts/%s/payments?cursor=now", accountId);
            WebTarget target = client.target(url);
            SseEventSource sseEventSource = SseEventSource.target(target).build();
            sseEventSource.register((event) -> messageHandling(event, accountId));
            sseEventSource.open();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }*/
        return true;
    }

    public void dataHandling(String data){
        Set<String> accounts = mappingService.getAccounts();

        if (data.contains("hello")) { //TODO contains hello
            return;
        }
        JSONObject jsonObj = JSON.parseObject(data);

        String type= jsonObj.getString("type");

        if ("payment".equals(type)){
            String sourceAccount = jsonObj.getString("source_account");
            String from = jsonObj.getString("from");
            String to = jsonObj.getString("to");
            if(!(accounts.contains(sourceAccount)||accounts.contains(from)||accounts.contains(to))){
                return;
            }
            String time = jsonObj.getString("created_at");
            String txHash  = jsonObj.getString("transaction_hash");
            String assetType = jsonObj.getString("asset_type");
            String assetCode = "XLM";
            String assetIssuer = "stellar.org";
            String num = jsonObj.getString("amount");
            if (!"native".equals(assetType)){
                assetCode = jsonObj.getString("asset_code");
                assetIssuer=jsonObj.getString("asset_issuer");
            }

            String template = "时间(伦敦):%s\n" +
                    "类型：%s\n" +
                    "资金账户：%s\n" +
                    "接受账户：%s\n" +
                    "金额：%s %s（%s）\n"+
                    "交易哈希：%s \n"+
                    "---------------- \n"+
                    "time(London): %s\n" +
                    "type: %s\n" +
                    "from：%s\n" +
                    "to：%s\n" +
                    "amount: %s %s（%s）\n"+
                    "txhash: %s";

            //String notify = "账号"+from+ "发送"+num+assetCode+"("+assetIssuer+")"+"到账号"+to;

            String notify = String.format(template, time, "转账", from, to, num, assetCode, assetIssuer, txHash, time, type, from, to, num, assetCode, assetIssuer, txHash);

            Set<String> subscribers = mappingService.getSubscribers(sourceAccount);
            Set<String> subscribers1 = mappingService.getSubscribers(from);
            Set<String> subscribers2 = mappingService.getSubscribers(to);

            if (subscribers1!=null){
                subscribers.addAll(subscribers1);
            }
            if (subscribers2!=null){
                subscribers.addAll(subscribers2);
            }

            for (String imId: subscribers){

                if (imId.startsWith(Constants.QQ_PREFIX)) {
                    imId=imId.substring(3);
                    qqService.alert(imId, notify);
                }

                if (imId.startsWith(Constants.TL_PREFIX)) {
                    imId=imId.substring(3);
                    telegramService.alert(imId, notify);
                }

            }

            LOGGER.info(notify);
            //System.out.println("notify: " + notify);

        }

        if ("manage_offer".equals(type)){
            String sourceAccount = jsonObj.getString("source_account");
            if(!accounts.contains(sourceAccount)){
                return;
            }
            String time = jsonObj.getString("created_at");
            String txHash  = jsonObj.getString("transaction_hash");
            String buyingAssetType = jsonObj.getString("buying_asset_type");
            String buyingAssetCode = "XLM";
            String buyingAssetIssuer = "stellar.org";
            if (!"native".equals(buyingAssetType)){
                buyingAssetCode = jsonObj.getString("buying_asset_code");
                buyingAssetIssuer = jsonObj.getString("buying_asset_issuer");
            }

            String sellingAssetType = jsonObj.getString("selling_asset_type");
            String sellingAssetCode = "XLM";
            String sellingAssetIssuer = "stellar.org";
            if (!"native".equals(sellingAssetType)){
                sellingAssetCode = jsonObj.getString("selling_asset_code");
                sellingAssetIssuer = jsonObj.getString("selling_asset_issuer");
            }

            String num = jsonObj.getString("amount");
            String price = jsonObj.getString("price");

            double sellingNum = Double.valueOf(num);
            double sellingPrice = Double.valueOf(price);
            String buyingNum = String.valueOf(sellingNum*sellingPrice);

            String template = "时间（伦敦）：%s\n" +
                    "类型：%s\n" +
                    "资金账户：%s\n" +
                    "卖出：%s %s（%s）\n" +
                    "买入：%s %s（%s）\n" +
                    "价格：1 %s=%s %s \n"+
                    "交易哈希：%s \n"+
                    "---------------- \n"+
                    "time(London): %s\n" +
                    "type: %s\n" +
                    "source: %s\n" +
                    "sell: %s %s（%s）\n" +
                    "buy: %s %s（%s）\n" +
                    "price: 1 %s=%s %s \n"+
                    "txhash: %s";

            //String notify = "账号"+sourceAccount+ "以价格"+price+buyingAssetCode+"("+buyingAssetIssuer+")"+"卖出"+num+sellingAssetCode+"("+sellingAssetIssuer+")";
            String notify = String.format(template, time, "挂单", sourceAccount, num, sellingAssetCode,sellingAssetIssuer, buyingNum, buyingAssetCode, buyingAssetIssuer, buyingAssetCode, 1/sellingPrice, sellingAssetCode, txHash, time, type, sourceAccount, num, sellingAssetCode,sellingAssetIssuer, buyingNum, buyingAssetCode, buyingAssetIssuer, buyingAssetCode, 1/sellingPrice, sellingAssetCode, txHash);

            Set<String> subscribers = mappingService.getSubscribers(sourceAccount);

            for (String imId: subscribers){
                if (imId.startsWith(Constants.QQ_PREFIX)) {
                    imId=imId.substring(3);
                    qqService.alert(imId, notify);
                }
                if (imId.startsWith(Constants.TL_PREFIX)) {
                    imId=imId.substring(3);
                    telegramService.alert(imId, notify);
                }
            }

            LOGGER.info(notify);
            //System.out.println("notify: " + notify);

        }


    }

    public void handleTradesData(Object object){
        Set<String> accounts = mappingService.getAccounts();

        //System.out.println(Thread.currentThread().getName() + ": inside: " + object);
        JSONObject jsonObj = (JSONObject)object;
        String baseAccount = jsonObj.getString("base_account");
        String counterAccount = jsonObj.getString("counter_account");

        if (!accounts.contains(baseAccount)&&!accounts.contains(counterAccount)){
            return;
        }

        LOGGER.info("trade: " + object);

        String time = jsonObj.getString("ledger_close_time");
        String id  = jsonObj.getString("id");
        String offerId  = jsonObj.getString("offer_id");


        String baseAssetType = jsonObj.getString("base_asset_type");
        String baseAssetCode = "XLM";
        String baseAssetIssuer = "stellar.org";
        if (!"native".equals(baseAssetType)){
            baseAssetCode = jsonObj.getString("base_asset_code");
            baseAssetIssuer = jsonObj.getString("base_asset_issuer");
        }
        String baseAmount = jsonObj.getString("base_amount");

        String counterAssetType = jsonObj.getString("counter_asset_type");
        String counterAssetCode = "XLM";
        String counterAssetIssuer = "stellar.org";
        if (!"native".equals(counterAssetType)){
            counterAssetCode = jsonObj.getString("counter_asset_code");
            counterAssetIssuer = jsonObj.getString("counter_asset_issuer");
        }
        String counterAmount = jsonObj.getString("counter_amount");



        double cAmount = Double.valueOf(counterAmount);
        double bAmount = Double.valueOf(baseAmount);
        double price = cAmount/bAmount;

        String template = "时间（伦敦）：%s\n" +
                "类型：%s\n" +
                "详情：\n" +
                "交易账户 %s 卖出 %s %s（%s）买入 %s %s（%s），价格为1 %s=%s %s \n" + // 账户xxx卖出5xlm（stellar.org）买入10CNY（ripplefox），价格为1xml=2CNY
                "交易账户 %s 卖出 %s %s（%s）买入 %s %s（%s），价格为1 %s=%s %s \n" +
                "---------------- \n"+
                "time(London): %s\n" +
                "type: %s\n" +
                "detail: \n" +
                "account %s sell %s %s（%s）buy %s %s（%s） at price 1 %s=%s %s \n" +
                "account %s sell %s %s（%s）buy %s %s（%s） at price 1 %s=%s %s \n";

        String notify = String.format(template, time, "挂单成交",
                baseAccount, baseAmount, baseAssetCode, baseAssetIssuer, counterAmount, counterAssetCode, counterAssetIssuer, baseAssetCode, price, counterAssetCode,
                counterAccount, counterAmount, counterAssetCode, counterAssetIssuer, baseAmount, baseAssetCode, baseAssetIssuer, counterAssetCode, 1/price, baseAssetCode);

        Set<String> subscribers = new HashSet<>();
        Set<String> subscribers1 =mappingService.getSubscribers(baseAccount);
        Set<String> subscribers2 = mappingService.getSubscribers(counterAccount);

        if (subscribers1!=null){
            subscribers.addAll(subscribers1);
        }
        if (subscribers2!=null){
            subscribers.addAll(subscribers1);
        }

        for (String imId: subscribers){
            if (imId.startsWith(Constants.QQ_PREFIX)) {
                imId=imId.substring(3);
                qqService.alert(imId, notify);
            }
            if (imId.startsWith(Constants.TL_PREFIX)) {
                imId=imId.substring(3);
                telegramService.alert(imId, notify);
            }
        }

        LOGGER.info(notify);

    }

}
