package online.abot.alertbot.imp;

import online.abot.alertbot.constant.Constants;
import online.abot.alertbot.domian.Binding;
import online.abot.alertbot.mapper.BindingMapper;
import online.abot.alertbot.service.ImService;
import online.abot.alertbot.service.MappingService;
import online.abot.alertbot.service.StellarService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
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

    private static final String BOT_USERNAME = "stellar_alert_bot";

    @Autowired
    StellarService stellarService;

    @Autowired
    MappingService mappingService;

    @Autowired
    BindingMapper bindingMapper;

    private TelegramLongPollingBot bot;

    @PostConstruct
    public void init() throws TelegramApiRequestException {
        ApiContextInitializer.init();
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
        bot = new TelegramLongPollingBot() {
            @Override
            public String getBotToken() {
                return token;
            }

            @Override
            public void onUpdateReceived(Update update) {

                //check if the update has a message
                if(update.hasMessage()){
                    Message message = update.getMessage();

                    // check if the message has text. it could also  contain for example a location ( message.hasLocation() )
                    if(message.hasText()){

                        //create a object that contains the information to send back the message
                        SendMessage sendMessageRequest = new SendMessage();
                        String chatId = message.getChatId().toString();
                        String imId = Constants.TL_PREFIX + chatId;
                                String accountId = message.getText();
                        LOGGER.info("Telegram Message record: " + chatId + ": " + accountId);
                                if (accountId.contains("start")) return;
                        boolean result = subscribe(imId, accountId);
                        String response = "";
                        if (result){
                            response = "Bind successfully! \n绑定成功！";

                        }else {
                            response = "Please make sure you have input your stellar account! \n请确认您输入的是恒星账号！";
                        }

                        //response
                        sendMessageRequest.setChatId(chatId); //who should get the message? the sender from which we got the message...
                        sendMessageRequest.setText(response);
                        try {
                            execute(sendMessageRequest); //at the end, so some magic and send the message ;)
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
        accountId = accountId.replace(" ", "");
        Binding binding = new Binding(chatId, accountId);
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
    public boolean alert(String chatId, String message) {
        SendMessage sendMessageRequest = new SendMessage();
        sendMessageRequest.setChatId(chatId);
        sendMessageRequest.setText(message);
        try {
            bot.execute(sendMessageRequest);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
        return false;
    }
}
