package ru.sber.sberlunch.util.events.admin;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class AdminEvent extends ApplicationEvent {
    private final Long chatId;

    public AdminEvent(Object source, Long chatId) {
        super(source);
        this.chatId = chatId;
    }
}
