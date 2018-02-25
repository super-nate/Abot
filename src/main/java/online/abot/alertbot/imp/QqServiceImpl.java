package online.abot.alertbot.imp;

import com.scienjus.smartqq.callback.MessageCallback;
import com.scienjus.smartqq.client.SmartQQClient;
import com.scienjus.smartqq.model.DiscussMessage;
import com.scienjus.smartqq.model.GroupMessage;
import com.scienjus.smartqq.model.Message;
import online.abot.alertbot.constant.Constants;
import online.abot.alertbot.domian.Binding;
import online.abot.alertbot.mapper.BindingMapper;
import online.abot.alertbot.service.MappingService;
import online.abot.alertbot.service.ImService;
import online.abot.alertbot.service.StellarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class QqServiceImpl implements ImService {



    private SmartQQClient client = null;

    @Autowired
    StellarService stellarService;

    @Autowired
    MappingService mappingService;

    @Autowired
    BindingMapper bindingMapper;

    @PostConstruct
    private void init() {

        client = new SmartQQClient(new MessageCallback() {
            @Override
            public void onMessage(Message message, SmartQQClient client) {
                String accountId = message.getContent();
                String qqId = String.valueOf(message.getUserId());//TODO return
                if ("3269075003".equals(qqId)){
                    return;
                }
                qqId = Constants.QQ_PREFIX + qqId;
                boolean result = subscribe(qqId, accountId);
                if (result) {
                    client.sendMessageToFriend(message.getUserId(), "绑定成功！");
                } else {
                    client.sendMessageToFriend(message.getUserId(), "请确认您输入的是恒星账号！");
                }
            }

            @Override
            public void onGroupMessage(GroupMessage message, SmartQQClient client) {
                System.out.println(message.getContent());
            }

            @Override
            public void onDiscussMessage(DiscussMessage message, SmartQQClient client) {
                System.out.println(message.getContent());
            }
        });

    }


    @Override
    public boolean subscribe(String qqId, String accountId) {
        accountId = accountId.replace(" ", "");
        Binding binding = new Binding(qqId, accountId);
        try {
            bindingMapper.insertQQBinding(binding);
            mappingService.addNewMapping(binding);
            stellarService.subscribe(binding);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public boolean alert(String qqId, String message) {
        try{
            client.sendMessageToFriend(Long.valueOf(qqId),  message);
        }catch (Exception e){
            e.printStackTrace();
        }

        return false;
    }
}
