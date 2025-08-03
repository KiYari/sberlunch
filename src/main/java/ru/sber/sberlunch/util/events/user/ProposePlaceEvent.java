package ru.sber.sberlunch.util.events.user;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;
import org.telegram.telegrambots.meta.api.objects.Message;

@Getter
public class ProposePlaceEvent extends ApplicationEvent {
    private final Long chatId;
    private final Message message;

    public ProposePlaceEvent(Object source, Long chatId, Message message) {
        super(source);
        this.chatId = chatId;
        this.message = message;
    }
}
