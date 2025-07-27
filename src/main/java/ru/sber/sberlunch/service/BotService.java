package ru.sber.sberlunch.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import ru.sber.sberlunch.config.TextImporter;
import ru.sber.sberlunch.config.bot.TelegramBot;
import ru.sber.sberlunch.model.entity.UserEntity;
import ru.sber.sberlunch.util.events.StartMessageEvent;

import java.util.Random;

@Service
@RequiredArgsConstructor
public class BotService {
    private final TextImporter textImporter;
    private final Random random = new Random();
    private final UserService userService;
    private final TelegramBot telegramBot;

    @EventListener
    public void startCommandReceived(StartMessageEvent event) {
        UserEntity entity = UserEntity.getDefaultUserEntity();
        entity.setUsername(event.getUsername());
        entity.setRealName("");
        entity.setID(event.getChatId());
        userService.createIfNotExists(entity);

        telegramBot.sendMessage(event.getChatId(), "Привет!" + "\n" +
                "Напиши своё настоящее имя + фамилию в следующем предложении, чтобы я знал кто ты" + "\n" +
                "Имей в виду, что имя будет проверяться!" + "\n"
                + textImporter.getLines().get(random.nextInt(0, 100)));
    }
}
