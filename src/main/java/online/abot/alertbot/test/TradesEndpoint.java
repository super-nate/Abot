package online.abot.alertbot.test;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.web.client.RestTemplate;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

public class TradesEndpoint {
    public static void main(String[] args) throws InterruptedException {
        RestTemplateBuilder restTemplateBuilder =new RestTemplateBuilder();
        RestTemplate restTemplate =  restTemplateBuilder.build();
        String requestUrl="https://horizon.stellar.org/trades?limit=1&order=desc";
      /*  String requestUrl="https://horizon.stellar.org/trades?cursor=71998576892317697-3";*/
        String result = restTemplate.getForObject(requestUrl, String.class);
        System.out.println(result);
        JSONObject jsonObject = JSONObject.parseObject(result);
        JSONObject embedded = jsonObject.getJSONObject("_embedded");
        JSONArray records = embedded.getJSONArray("records");
        JSONObject trade = (JSONObject)records.get(0);
        String cursor = "72058543225708545-0";//trade.getString("paging_token");
        System.out.println(cursor);

        String TRADES_URL = "https://horizon.stellar.org/trades?cursor=72067240534491137-0&limit=200";
        requestUrl = String.format(TRADES_URL, cursor);
        result = restTemplate.getForObject(requestUrl, String.class);
        System.out.println(result);
        jsonObject = JSONObject.parseObject(result);
        embedded = jsonObject.getJSONObject("_embedded");
        records = embedded.getJSONArray("records");
        System.out.println("size: " + records.size());
        Flux.fromIterable(records).subscribeOn(Schedulers.elastic()).subscribe();
        Thread.sleep(50000);

    }

}
