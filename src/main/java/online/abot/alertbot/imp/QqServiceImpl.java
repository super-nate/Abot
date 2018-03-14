package online.abot.alertbot.imp;

import com.scienjus.smartqq.callback.MessageCallback;
import com.scienjus.smartqq.client.SmartQQClient;
import com.scienjus.smartqq.model.DiscussMessage;
import com.scienjus.smartqq.model.Friend;
import com.scienjus.smartqq.model.GroupMessage;
import com.scienjus.smartqq.model.Message;
import online.abot.alertbot.constant.Constants;
import online.abot.alertbot.domian.Binding;
import online.abot.alertbot.mapper.BindingMapper;
import online.abot.alertbot.service.MappingService;
import online.abot.alertbot.service.ImService;
import online.abot.alertbot.service.StellarService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("QqService")
public class QqServiceImpl implements ImService {

    private static final Logger LOGGER = Logger.getLogger(QqServiceImpl.class);

    private SmartQQClient client = null;

    private Map<String, Long> qqNameToUidMap = new HashMap();

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
                long uid = message.getUserId();//TODO return
                if (3269075003L == uid){
                    return;
                }

                String qqName = client.getFriendInfo(uid).getNick();
                qqNameToUidMap.put(qqName,uid);
                qqName = Constants.QQ_PREFIX + qqName;
                LOGGER.info("QQ Message record: " + qqName + ": " + accountId);
                boolean result = subscribe(qqName, accountId);
                if (result) {
                    client.sendMessageToFriend(message.getUserId(), "Bind successfully! \n绑定成功！");
                } else {
                    client.sendMessageToFriend(message.getUserId(), "Please make sure you have input your stellar account! \n请确认您输入的是恒星账号！");
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

        List<Friend> friendList = client.getFriendList();
        for (Friend friend:friendList){
            String qqName = friend.getNickname();
            qqNameToUidMap.put(qqName,friend.getUserId());
        }

    }

    @Override
    public boolean subscribe(String qqName, String accountId) {
        accountId = accountId.replace(" ", "");
        Binding binding = new Binding(qqName, accountId);
        try {
            bindingMapper.insertBinding(binding);
            mappingService.addNewMapping(binding);
            stellarService.subscribe(binding);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public boolean alert(String qqName, String message) {
        try{
            long uid = qqNameToUidMap.get(qqName);
            client.sendMessageToFriend(uid,  message);
        }catch (Exception e){
            e.printStackTrace();
        }

        return false;
    }
}
