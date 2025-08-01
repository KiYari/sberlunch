package ru.sber.sberlunch.config.bot;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.sber.sberlunch.util.events.*;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class TelegramBot extends TelegramLongPollingBot {
    private final BotConfig botConfig;
    private final ApplicationEventPublisher publisher;

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
            Long chatId = update.getMessage().getChatId();
            String callbackData = null;
            if(update.hasCallbackQuery()) {
                callbackData = update.getCallbackQuery().getData();
            }

            switch (messageText){
                case "/start":
                    publisher.publishEvent(new StartMessageEvent(this, chatId, update.getMessage().getChat().getUserName()));
                    break;

                case "Предложить место для обеда":
                    publisher.publishEvent(new ProposePlaceEvent(this, chatId, update.getMessage()));
                    break;

                case "Посмотреть сопартийцев":
                    publisher.publishEvent(new GetTeamEvent(this, chatId));
                    break;


                default:
                    publisher.publishEvent(new TelegramMessageEvent(this, chatId, update));
            }

            if (callbackData != null) {
                if (callbackData.equals("time_clicked")) {
                    publisher.publishEvent(new TimeProposeEvent(this, chatId, update.getMessage().getChat().getUserName()));
                }
            }

        }
    }

    public void sendMessage(Long chatId, String textToSend){
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText(textToSend);
        sendMessage.setReplyMarkup(createMainMenuKeyboard());
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {

        }
    }

    public void sendMessage(Long chatId, String textToSend, InlineKeyboardMarkup  markup){
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText(textToSend);
        sendMessage.setReplyMarkup(createMainMenuKeyboard());
        sendMessage.setReplyMarkup(markup);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {

        }
    }

    public ReplyKeyboardMarkup createMainMenuKeyboard() {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setSelective(true);
        keyboardMarkup.setResizeKeyboard(true);
        keyboardMarkup.setOneTimeKeyboard(false);

        List<KeyboardRow> keyboard = new ArrayList<>();

        KeyboardRow row1 = new KeyboardRow();
        row1.add(new KeyboardButton("Предложить место для обеда"));
        row1.add(new KeyboardButton("Посмотреть сопартийцев"));

        keyboard.add(row1);

        keyboardMarkup.setKeyboard(keyboard);
        return keyboardMarkup;
    }

}
