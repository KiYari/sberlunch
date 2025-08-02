package ru.sber.sberlunch.util.events.admin.room;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class ShuffleRoomEvent extends ApplicationEvent {
    private final Long chatId;

    public ShuffleRoomEvent(Object source, Long chatId) {
        super(source);
        this.chatId = chatId;
    }
}
