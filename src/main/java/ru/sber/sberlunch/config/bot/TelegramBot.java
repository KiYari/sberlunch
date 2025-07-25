package ru.sber.sberlunch.config.bot;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.sber.sberlunch.config.TextImporter;

import java.util.Random;

@Component
@RequiredArgsConstructor
public class TelegramBot extends TelegramLongPollingBot {
    private final BotConfig botConfig;
    private final TextImporter textImporter;
    private final  Random random = new Random();

    @Override
    public String getBotUsername() {
        return botConfig.getBotName();
    }

    @Override
    public String getBotToken() {
        return botConfig.getToken();
    }

    @Override
    public void onUpdateReceived(Update update) {
        if(update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();

            switch (messageText){
                case "/start":
                    startCommandReceived(chatId, update.getMessage().getChat().getFirstName());
                    break;

                default:
                    String message = "здарова";
                    sendMessage(chatId, message);

            }
        }
    }

    private void startCommandReceived(Long chatId, String name) {
        String answer = "Привет, " + name + "!" + "\n" +
                "Напиши своё настоящее имя + фамилию, чтобы я знал кто ты" + "\n" +
                "Имей в виду, что имя будет проверяться!" + "\n"
                + textImporter.getLines().get(random.nextInt(0, 100));
        sendMessage(chatId, answer);
    }

    private void sendMessage(Long chatId, String textToSend){
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText(textToSend);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {

        }
    }

}
