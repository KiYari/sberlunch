package ru.sber.sberlunch.util.events.user;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class GetTeamEvent extends ApplicationEvent {
    private final Long chatId;

    public GetTeamEvent(Object source, Long chatId) {
        super(source);
        this.chatId = chatId;
    }
}
