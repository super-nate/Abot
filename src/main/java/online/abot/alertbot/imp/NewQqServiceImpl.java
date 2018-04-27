package online.abot.alertbot.imp;

import com.alibaba.fastjson.JSONObject;
import com.scienjus.smartqq.callback.MessageCallback;
import com.scienjus.smartqq.client.SmartQQClient;
import com.scienjus.smartqq.model.DiscussMessage;
import com.scienjus.smartqq.model.Friend;
import com.scienjus.smartqq.model.GroupMessage;
import com.scienjus.smartqq.model.Message;
import online.abot.alertbot.constant.Constants;
import online.abot.alertbot.domian.Binding;
import online.abot.alertbot.mapper.BindingMapper;
import online.abot.alertbot.service.ImService;
import online.abot.alertbot.service.MappingService;
import online.abot.alertbot.service.StellarService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("NewQqService")
public class NewQqServiceImpl implements ImService {

    private static final Logger LOGGER = Logger.getLogger(NewQqServiceImpl.class);

    private static final String HTTP_API_URL = "http://localhost:5700/send_private_msg_async";

    @Value("${access.token}")
    private String ACCESS_TOKEN;

    @Autowired
    StellarService stellarService;

    @Autowired
    MappingService mappingService;

    @Autowired
    BindingMapper bindingMapper;

    private final RestTemplate restTemplate;

    public NewQqServiceImpl(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    @Override
    public boolean subscribe(String qqName, String accountId) {
        accountId = accountId.replace(" ", "");
        Binding binding = new Binding(qqName, accountId);
        try {
            bindingMapper.insertBinding(binding);
            mappingService.addNewMapping(binding);
            //stellarService.subscribe(binding);
        } catch (Exception e) {
            LOGGER.error("Database exception", e);
            return false;
        }
        return true;
    }

    @Override
    public boolean alert(String qqId, String message) {
        try{
            JSONObject alert = new JSONObject();
            alert.put("user_id", qqId);
            alert.put("message",message);


            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", ACCESS_TOKEN);
            headers.set("Content-Type", "application/json");

            HttpEntity<String> entity = new HttpEntity<String>(alert.toJSONString(), headers);


            ResponseEntity<String> respEntity = restTemplate.exchange(HTTP_API_URL, HttpMethod.POST, entity, String.class);
            //String result = restTemplate.postForObject( HTTP_API_URL, alert, String.class);
            LOGGER.debug(respEntity.getBody());
        }catch (Exception e){
            e.printStackTrace();
        }

        return false;
    }
}
