package online.abot.alertbot.imp;

import online.abot.alertbot.service.ImService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.telegram.telegrambots.exceptions.TelegramApiRequestException;

import javax.annotation.PostConstruct;

@Service("TelegramService")
public class TelegramServiceImpl implements ImService {

    private static final Logger LOGGER = Logger.getLogger(TelegramServiceImpl.class);

    @Value("${telegram.token}")
    private String token;

    private static final String BOT_USERNAME = "hengbot";

    @PostConstruct
    public void init() throws TelegramApiRequestException {
        ApiContextInitializer.init();
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
        TelegramLongPollingBot bot = new TelegramLongPollingBot() {
            @Override
            public String getBotToken() {
                return token;
            }

            @Override
            public void onUpdateReceived(Update update) {

//check if the update has a message
                if(update.hasMessage()){
                    Message message = update.getMessage();

                    //check if the message has text. it could also  contain for example a location ( message.hasLocation() )
                    if(message.hasText()){

                        //create a object that contains the information to send back the message
                        SendMessage sendMessageRequest = new SendMessage();
                        sendMessageRequest.setChatId(message.getChatId().toString()); //who should get the message? the sender from which we got the message...
                        sendMessageRequest.setText("you said: " + message.getText());
                        try {
                            sendMessage(sendMessageRequest); //at the end, so some magic and send the message ;)
                        } catch (TelegramApiException e) {
                            //do some error handling
                        }//end catch()
                    }//end if()
                }//end  if()


            }

            @Override
            public String getBotUsername() {
                return BOT_USERNAME;
            }
        };
        telegramBotsApi.registerBot(bot);
    }


    @Override
    public boolean subscribe(String chatId, String accountId) {
        return false;
    }

    @Override
    public boolean alert(String chatId, String message) {
        return false;
    }
}
