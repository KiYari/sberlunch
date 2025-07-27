package ru.sber.sberlunch.util.events;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEvent;

@Getter
public class StartMessageEvent extends ApplicationEvent {
    private final String username;
    private final Long chatId;

    public StartMessageEvent(Object source, Long chatId, String username) {
        super(source);
        this.username = username;
        this.chatId = chatId;
    }
}
