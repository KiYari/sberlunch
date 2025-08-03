package ru.sber.sberlunch.util.events.user;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class ChangeReadyEvent extends ApplicationEvent {
    private final Long chatId;

    public ChangeReadyEvent(Object source, Long chatId) {
        super(source);
        this.chatId = chatId;
    }
}
