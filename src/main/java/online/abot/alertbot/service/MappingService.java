package online.abot.alertbot.service;

import online.abot.alertbot.domian.QqBinding;

import java.util.Set;

public interface MappingService {

    void addNewMapping(QqBinding qqBinding);
    Set<String> getSubscribers(String accountId);
    Set<String> getAccounts();
}
