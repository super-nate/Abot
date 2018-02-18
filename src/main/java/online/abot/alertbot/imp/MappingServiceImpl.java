package online.abot.alertbot.imp;

import online.abot.alertbot.domian.QqBinding;
import online.abot.alertbot.mapper.QqBindingMapper;
import online.abot.alertbot.service.MappingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;
@Service
public class MappingServiceImpl implements MappingService {
    @Autowired
    QqBindingMapper qqBindingMapper;

    //accountid as key, list of subscribers like qqid, as value
    private Map<String, Set<String>> subscribersMap = new HashMap<>();

    @PostConstruct
    void init(){
        List<QqBinding> qqBindings= qqBindingMapper.findAll();
        for(QqBinding qqBinding: qqBindings){
            addNewMapping(qqBinding);
        }
    }


    @Override
    public void addNewMapping(QqBinding qqBinding) {
        String accountId = qqBinding.getAccountId();
        String qqId = qqBinding.getQqId();
        Set<String> subscribersSet = subscribersMap.get(accountId);
        if (subscribersSet==null||subscribersSet.size()==0){
            subscribersSet = new HashSet<>();
            subscribersMap.put(accountId, subscribersSet);
        }
        subscribersSet.add(qqId);
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
