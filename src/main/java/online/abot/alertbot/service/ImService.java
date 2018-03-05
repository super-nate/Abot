package online.abot.alertbot.service;


//To interact with the qq side
public interface ImService {
    boolean subscribe(String id, String accountId);
    boolean alert(String id, String message);
}
