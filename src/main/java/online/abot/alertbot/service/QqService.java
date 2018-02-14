package online.abot.alertbot.service;


//To interact with the qq side
public interface QqService {
    boolean subscribe(String qqId, String accountId);
    boolean alert(String qqId, String message);
}
