package ru.sber.sberlunch.util.events;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;
import org.telegram.telegrambots.meta.api.objects.Update;

@Getter
public class GetTeamEvent extends ApplicationEvent {
    private final Long chatId;

    public GetTeamEvent(Object source, Long chatId) {
        super(source);
        this.chatId = chatId;
    }
}
