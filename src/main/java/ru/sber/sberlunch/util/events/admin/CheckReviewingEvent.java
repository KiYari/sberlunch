package ru.sber.sberlunch.util.events.admin;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class CheckReviewingEvent extends ApplicationEvent {
    private final Long chatId;

    public CheckReviewingEvent(Object source, Long chatId) {
        super(source);
        this.chatId = chatId;
    }
}
