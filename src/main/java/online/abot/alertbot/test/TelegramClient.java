package online.abot.alertbot.test;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.web.client.RestTemplate;

public class TelegramClient {

    public static void main(String[] args) {
        RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder();
        RestTemplate restTemplate = restTemplateBuilder.build();
        String result = restTemplate.getForObject("https://api.telegram.org/bot489910690:AAGehxnbrzg9rYd_dHWbSnNWOni1MKtj3BE/getMe", String.class);
        System.out.println(result);
    }
}
