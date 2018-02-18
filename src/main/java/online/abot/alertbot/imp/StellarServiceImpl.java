package online.abot.alertbot.imp;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import online.abot.alertbot.domian.QqBinding;
import online.abot.alertbot.mapper.QqBindingMapper;
import online.abot.alertbot.service.MappingService;
import online.abot.alertbot.service.QqService;
import online.abot.alertbot.service.StellarService;
import org.glassfish.jersey.media.sse.EventListener;
import org.glassfish.jersey.media.sse.EventSource;
import org.glassfish.jersey.media.sse.InboundEvent;
import org.glassfish.jersey.media.sse.SseFeature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.sse.InboundSseEvent;
import javax.ws.rs.sse.SseEventSource;
import java.util.List;
import java.util.Set;

@Service
public class StellarServiceImpl implements StellarService {

    @Autowired
    QqService qqService;

    @Autowired
    MappingService mappingService;

    @Autowired
    QqBindingMapper qqBindingMapper;


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
    public boolean subscribe(QqBinding qqBinding) {
       /* try {
            String accountId = qqBinding.getAccountId();
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
            String assetType = jsonObj.getString("asset_type");
            String assetCode = "XLM";
            String assetIssuer = "stellar.org";
            String num = jsonObj.getString("amount");
            if (!"native".equals(assetType)){
                assetCode = jsonObj.getString("asset_code");
                assetIssuer=jsonObj.getString("asset_issuer");
            }
            String notify = "账号"+from+ "发送"+num+assetCode+"("+assetIssuer+")"+"到账号"+to;

            Set<String> subscribers = mappingService.getSubscribers(sourceAccount);
            Set<String> subscribers1 = mappingService.getSubscribers(from);
            Set<String> subscribers2 = mappingService.getSubscribers(to);

            subscribers.addAll(subscribers1);
            subscribers.addAll(subscribers2);

            for (String qqId: subscribers){
                qqService.alert(qqId, notify);
            }


            System.out.println("notify: " + notify);

        }

        if ("manage_offer".equals(type)){
            String sourceAccount = jsonObj.getString("source_account");
            if(!accounts.contains(sourceAccount)){
                return;
            }

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

            String notify = "账号"+sourceAccount+ "以价格"+price+buyingAssetCode+"("+buyingAssetIssuer+")"+"卖出"+num+sellingAssetCode+"("+sellingAssetIssuer+")";

            Set<String> subscribers = mappingService.getSubscribers(sourceAccount);

            for (String qqId: subscribers){
                qqService.alert(qqId, notify);
            }

            System.out.println("notify: " + notify);

        }


    }

}
