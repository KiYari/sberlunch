package ru.sber.sberlunch.util.events;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class TimeProposeEvent extends ApplicationEvent {
    private final String username;
    private final Long chatId;

    public TimeProposeEvent(Object source, Long chatId, String username) {
        super(source);
        this.username = username;
        this.chatId = chatId;
    }
}
