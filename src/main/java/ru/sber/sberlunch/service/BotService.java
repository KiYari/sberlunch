package ru.sber.sberlunch.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.sber.sberlunch.config.TextImporter;
import ru.sber.sberlunch.config.bot.TelegramBot;
import ru.sber.sberlunch.model.entity.Room;
import ru.sber.sberlunch.model.entity.UserEntity;
import ru.sber.sberlunch.repository.RoomRepository;
import ru.sber.sberlunch.repository.UserRepository;
import ru.sber.sberlunch.util.enums.UserActivityStatus;
import ru.sber.sberlunch.util.enums.UserRegistrationStatus;
import ru.sber.sberlunch.util.events.*;
import ru.sber.sberlunch.util.events.user.*;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BotService { //TODO: Говно полное, надо подумать как разделить нормально потом все это. пока вайбкод. Еще надо подумать как разделение на комнаты сделать, но это далеко
    private final TextImporter textImporter;
    private final Random random = new Random();
    //    private final UserService userService; - под вопросом нужно ли
    private final UserRepository userRepository;
    private final TelegramBot telegramBot;
    private final RoomRepository roomRepository;

    @EventListener
    @Transactional
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
                        if (user.getTeamId() < 1) {
                            user.setTeamId(joinLeastMembersTeam(user.getRoom()));
                        }
                        user.setPlaceProposed(message.getText());
                        telegramBot.sendMessage(event.getChatId(), "Теперь твое выбранное место: " + message.getText(), placeProposedMarkup());
                    }

                    case ACCEPTING_TO_ROOM -> {
                        Optional<UserEntity> opt = userRepository.findByUsername(message.getText());
                        if (opt.isPresent()) {
                            UserEntity userToAdd = opt.get();
                            userToAdd.setRoom(user.getRoom());

                            userRepository.save(userToAdd);
                            telegramBot.sendMessage(event.getChatId(), "Отлично! Вы добавили " + userToAdd.getRealName() + " в комнату!");
                        } else {
                            telegramBot.sendMessage(event.getChatId(), "Нет такого чела в боте. Либо попроси его написать сообщение, чтобы обновился ТГ юзернейм");
                        }
                    }

                    case ADMIN_ACCEPTING_TO_SYSTEM -> {
                        List<UserEntity> usersToUpdate = Arrays.stream(message.getText().split(","))
                                .map(String::trim)
                                .map(userRepository::findByUsername)
                                .filter(Optional::isPresent)
                                .map(Optional::get)
                                .peek(u -> u.setRegistrationStatus(UserRegistrationStatus.ACTIVE))
                                .collect(Collectors.toList());

                        if (usersToUpdate.isEmpty()) {
                            telegramBot.sendMessage(event.getChatId(), "Никого не приняли");
                            return;
                        }
                        telegramBot.sendMessage(event.getChatId(), "Приняты: " + usersToUpdate.stream().map(UserEntity::getRealName).collect(Collectors.joining(", ")));

                        user.setActivityStatus(UserActivityStatus.STABLE);
                        usersToUpdate.add(user);


                        userRepository.saveAll(usersToUpdate);

                    }

                    default -> {

                    }
                }
            }

            if (user.getUsername().equals(message.getChat().getUserName())) {
                user.setUsername(message.getChat().getUserName());
            }
            user.setActivityStatus(UserActivityStatus.STABLE);
            userRepository.save(user);

        } else {
            startCommandReceived(new StartMessageEvent(this, message.getChatId(), message.getChat().getUserName()));
        }
    }

    @EventListener
    @Transactional
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
    @Transactional
    public void userPlaseProposingCommandReceived(ProposePlaceEvent event) {
        Optional<UserEntity> optional = userRepository.findById(event.getChatId());

        if (optional.isPresent()) {
            UserEntity user = optional.get();

            if (user.getRegistrationStatus() == UserRegistrationStatus.ACTIVE) {
                if (user.getRoom() == (null)) {
                    telegramBot.sendMessage(event.getChatId(), "У тебя нет комнаты, к которой ты приписан! Попроси кого-нибудь добавить тебя.");
                    return;
                }

                user.setActivityStatus(UserActivityStatus.PROPOSING);
                userRepository.save(user);
                telegramBot.sendMessage(event.getChatId(), "В следующем сообщении напиши куда хочешь сходить");
            } else {
                telegramBot.sendMessage(event.getChatId(), "Молодой еще, жди пока тебя согласуют ");
            }
        }
    }

    @EventListener
    @Transactional
    public void timeProposingCommandReceived(TimeProposeEvent event) {
        Optional<UserEntity> optional = userRepository.findById(event.getChatId());

        if (optional.isPresent()) {
            UserEntity user = optional.get();

            if (user.getRegistrationStatus() == UserRegistrationStatus.ACTIVE) {
                if (user.getRoom() == (null)) {
                    //TODO: Добавить предложение времени. Надо будет в БД добавить, как с местом короче
                }
            }
        }

        telegramBot.sendMessage(event.getChatId(), "Эта хуйня еще не работает", ladnoKeymap());
    }

    @EventListener
    @Transactional
    public void getTeamCommandReceived(GetTeamEvent event) {
        Optional<UserEntity> optional = userRepository.findById(event.getChatId());
        if (optional.isPresent()) {
            UserEntity user = optional.get();

            if (user.getRegistrationStatus() == UserRegistrationStatus.ACTIVE) {
                if (user.getRoom() == (null)) {
                    telegramBot.sendMessage(event.getChatId(), "Не приписан ни к какой комнате");
                }

                if (user.getTeamId() < 1) {
                    user.setTeamId(joinLeastMembersTeam(user.getRoom()));
                }

                telegramBot.sendMessage(event.getChatId(), "Твоя команда: " + userRepository.findByRoomAndTeamId(user.getRoom(),
                        user.getTeamId())
                        .stream()
                        .filter(UserEntity::getIsReady)
                        .map(UserEntity::getRealName)
                        .collect(Collectors.joining(", ")));

            } else {
                telegramBot.sendMessage(event.getChatId(), "Не согласован, проси админа добавить тебя");
            }
        }
    }

    @EventListener
    @Transactional
    public void handleChangeReadinessEvent(ChangeReadyEvent event) {
        Optional<UserEntity> optional = userRepository.findById(event.getChatId());
        if (optional.isPresent()) {
            UserEntity user = optional.get();

            if (user.getRegistrationStatus() == UserRegistrationStatus.ACTIVE) {
                user.setIsReady(!user.getIsReady());
                userRepository.save(user);
                telegramBot.sendMessage(event.getChatId(), "Готовность идти на обед выставлена на: " + user.getIsReady());
            } else {
                telegramBot.sendMessage(event.getChatId(), "Не согласовано");
            }
        }
    }

    private Integer joinLeastMembersTeam(Room room) {
        return Math.max(userRepository.findByRoom(room)
                .stream()
                .collect(Collectors.groupingBy(
                        UserEntity::getTeamId,
                        Collectors.counting()
                )).entrySet()
                .stream()
                .min(Map.Entry.comparingByValue()).get().getKey(), 1);

    }

    private InlineKeyboardMarkup placeProposedMarkup() {
        InlineKeyboardMarkup inlineKeyboard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();

        List<InlineKeyboardButton> rowInline1 = new ArrayList<>();
        InlineKeyboardButton button1 = new InlineKeyboardButton();
        button1.setText("Предложить время");
        button1.setCallbackData("time_clicked");
        rowInline1.add(button1);

        rowsInline.add(rowInline1);
        inlineKeyboard.setKeyboard(rowsInline);

        return inlineKeyboard;
    }

    private InlineKeyboardMarkup ladnoKeymap() {
        InlineKeyboardMarkup inlineKeyboard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();

        List<InlineKeyboardButton> rowInline1 = new ArrayList<>();
        InlineKeyboardButton button1 = new InlineKeyboardButton();
        button1.setText("ладно");
        button1.setCallbackData("okokok");
        rowInline1.add(button1);

        InlineKeyboardButton button2 = new InlineKeyboardButton();
        button2.setText("ладно");
        button2.setCallbackData("okokok2");
        rowInline1.add(button1);

        InlineKeyboardButton button3 = new InlineKeyboardButton();
        button3.setText("ладно");
        button3.setCallbackData("okokok3");
        rowInline1.add(button1);

        rowsInline.add(rowInline1);
        inlineKeyboard.setKeyboard(rowsInline);

        return inlineKeyboard;
    }
}
