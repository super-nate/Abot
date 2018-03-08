package online.abot.alertbot.imp;

import com.alibaba.fastjson.JSON;
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
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import java.util.Set;

@Service
public class StellarServiceImpl implements StellarService {

    private static final Logger LOGGER = Logger.getLogger(StellarServiceImpl.class);

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

            String template = "时间:%s\n" +
                    "类型：%s\n" +
                    "资金账户：%s\n" +
                    "接受账户：%s\n" +
                    "金额：%s %s（%s）\n"+
                    "交易哈希：%s \n"+
                    "---------------- \n"+
                    "time: %s\n" +
                    "type: %s\n" +
                    "from：%s\n" +
                    "to：%s\n" +
                    "amount: %s %s（%s）\n"+
                    "txhash: %s";

            //String notify = "账号"+from+ "发送"+num+assetCode+"("+assetIssuer+")"+"到账号"+to;

            String notify = String.format(template, time, type, from, to, num, assetCode, assetIssuer, txHash, time, type, from, to, num, assetCode, assetIssuer, txHash);

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

            String template = "时间：%s\n" +
                    "类型：%s\n" +
                    "资金账户：%s\n" +
                    "卖出：%s %s（%s）\n" +
                    "买入：%s %s（%s）\n" +
                    "价格：1 %s=%s %s \n"+
                    "交易哈希：%s \n"+
                    "---------------- \n"+
                    "time: %s\n" +
                    "type: %s\n" +
                    "source: %s\n" +
                    "sell: %s %s（%s）\n" +
                    "buy: %s %s（%s）\n" +
                    "price: 1 %s=%s %s \n"+
                    "txhash: %s";

            //String notify = "账号"+sourceAccount+ "以价格"+price+buyingAssetCode+"("+buyingAssetIssuer+")"+"卖出"+num+sellingAssetCode+"("+sellingAssetIssuer+")";
            String notify = String.format(template, time, type, sourceAccount, num, sellingAssetCode,sellingAssetIssuer, buyingNum, buyingAssetCode, buyingAssetIssuer, buyingAssetCode, 1/sellingPrice, sellingAssetCode, txHash, time, type, sourceAccount, num, sellingAssetCode,sellingAssetIssuer, buyingNum, buyingAssetCode, buyingAssetIssuer, buyingAssetCode, 1/sellingPrice, sellingAssetCode, txHash);

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

}
