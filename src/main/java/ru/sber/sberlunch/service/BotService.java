package ru.sber.sberlunch.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.sber.sberlunch.config.TextImporter;
import ru.sber.sberlunch.config.bot.TelegramBot;
import ru.sber.sberlunch.model.entity.UserEntity;
import ru.sber.sberlunch.repository.UserRepository;
import ru.sber.sberlunch.util.enums.UserActivityStatus;
import ru.sber.sberlunch.util.enums.UserRegistrationStatus;
import ru.sber.sberlunch.util.events.ProposePlaceEvent;
import ru.sber.sberlunch.util.events.StartMessageEvent;
import ru.sber.sberlunch.util.events.TelegramMessageEvent;

import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class BotService {
    private final TextImporter textImporter;
    private final Random random = new Random();
    //    private final UserService userService; - под вопросом нужно ли
    private final UserRepository userRepository;
    private final TelegramBot telegramBot;

    @EventListener
    public void defaultMessageReceived(TelegramMessageEvent event) {
        Optional<UserEntity> optional = userRepository.findById(event.getChatId());
        Message message = event.getUpdate().getMessage();

        if (optional.isPresent()) {
            UserEntity user = optional.get();
            switch (user.getRegistrationStatus()) {
                case PENDING -> {
                    String realName = message.getText().substring(0, Math.min(message.getText().length(), 64));
                    user.setRealName(realName);
                    user.setRegistrationStatus(UserRegistrationStatus.REVIEWING);

                    telegramBot.sendMessage(event.getChatId(), "Отлично!\n" +
                            "Пока я не проверю твоё имя тебя будут звать: " + realName);
                }

                default -> {
                    if (user.getActivityStatus().equals(UserActivityStatus.STABLE)) {
                        telegramBot.sendMessage(event.getChatId(), "ну потом поговорим короче, сейчас некогда, жена рожает, давай гуляй");
                    }
                }

            }

            if (user.getRegistrationStatus().equals(UserRegistrationStatus.ACTIVE)) {
                switch (user.getActivityStatus()){
                    case PROPOSING -> {
                        user.setPlaceProposed(message.getText());
                        user.setActivityStatus(UserActivityStatus.STABLE);
                        //TODO: на этом моменте нужно вводить сущность группы и приписывать чувака к группе, нужно подумать как ограничить количество выбора для человека места до 1
                        telegramBot.sendMessage(event.getChatId(), "Пока похуй думаю как сделать");
                    }
                }
            }

            if (user.getUsername().equals(message.getChat().getUserName())) {
                user.setUsername(message.getChat().getUserName());
            }
            userRepository.save(user);

        } else {
            startCommandReceived(new StartMessageEvent(this, message.getChatId(), message.getChat().getUserName()));
        }
    }

    @EventListener
    public void startCommandReceived(StartMessageEvent event) {
        UserEntity entity = UserEntity.getDefaultUserEntity();
        entity.setUsername(event.getUsername());
        entity.setRealName("");
        entity.setID(event.getChatId());
        userRepository.save(entity);

        telegramBot.sendMessage(event.getChatId(), "Привет!" + "\n" +
                "Напиши своё настоящее имя(чтобы я мог ориентироваться ты кто) + фамилию в следующем предложении, чтобы я знал кто ты" + "\n" +
                "Имей в виду, что имя будет проверяться! Так что писать гей вонючка фимозник можно, но потом я поменяю" + "\n"
                + textImporter.getLines().get(random.nextInt(0, 100)));
    }

    @EventListener
    public void userPlaseProposingCommandReceived(ProposePlaceEvent event) {
        Optional<UserEntity> optional = userRepository.findById(event.getChatId());

        if (optional.isPresent()) {
            UserEntity user = optional.get();

            if (user.getRegistrationStatus() == UserRegistrationStatus.ACTIVE) {
                user.setActivityStatus(UserActivityStatus.PROPOSING);
                userRepository.save(user);
                telegramBot.sendMessage(event.getChatId(), "В следующем сообщении напиши куда хочешь сходить");
            } else {
                telegramBot.sendMessage(event.getChatId(), "Молодой еще, жди пока тебя согласуют ");
            }
        }
    }
}
