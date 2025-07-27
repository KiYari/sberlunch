package ru.sber.sberlunch.util.events;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.time.Clock;

@Getter
public class TelegramMessageEvent extends ApplicationEvent {
    private final Long chatId;
    private final Update update;

    public TelegramMessageEvent(Object source, Long chatId, Update update) {
        super(source);
        this.chatId = chatId;
        this.update = update;
    }
}
