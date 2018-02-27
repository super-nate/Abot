package online.abot.alertbot.imp;

import com.scienjus.smartqq.client.SmartQQClient;
import online.abot.alertbot.domian.Binding;
import online.abot.alertbot.mapper.BindingMapper;
import online.abot.alertbot.service.MappingService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;
@Service
public class MappingServiceImpl implements MappingService {
    //日志
    private static final Logger LOGGER = Logger.getLogger(MappingServiceImpl.class);


    @Autowired
    BindingMapper bindingMapper;

    //accountid as key, list of subscribers like qqid, as value
    private Map<String, Set<String>> subscribersMap = new HashMap<>();

    @PostConstruct
    void init(){
        List<Binding> bindings = bindingMapper.findAll();
        for(Binding binding : bindings){
            addNewMapping(binding);
        }
    }


    @Override
    public void addNewMapping(Binding binding) {
        String accountId = binding.getAccountId();
        String imId = binding.getImId();
        Set<String> subscribersSet = subscribersMap.get(accountId);
        if (subscribersSet==null||subscribersSet.size()==0){
            subscribersSet = new HashSet<>();
            subscribersMap.put(accountId, subscribersSet);
        }
        subscribersSet.add(imId);
    }

    @Override
    public Set<String> getSubscribers(String accountId) {
        return subscribersMap.get(accountId);
    }

    @Override
    public Set<String> getAccounts(){
         return subscribersMap.keySet();
    }
}
