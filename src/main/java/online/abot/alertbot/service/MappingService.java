package online.abot.alertbot.service;

import online.abot.alertbot.domian.Binding;

import java.util.Set;

public interface MappingService {

    void addNewMapping(Binding binding);
    Set<String> getSubscribers(String accountId);
    Set<String> getAccounts();
}
