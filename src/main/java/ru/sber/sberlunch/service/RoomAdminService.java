package ru.sber.sberlunch.service;

import lombok.RequiredArgsConstructor;
import org.glassfish.jersey.internal.guava.Lists;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.sber.sberlunch.config.bot.TelegramBot;
import ru.sber.sberlunch.model.entity.RoleEntity;
import ru.sber.sberlunch.model.entity.Room;
import ru.sber.sberlunch.model.entity.UserEntity;
import ru.sber.sberlunch.repository.RoomRepository;
import ru.sber.sberlunch.repository.UserRepository;
import ru.sber.sberlunch.util.enums.Role;
import ru.sber.sberlunch.util.enums.UserActivityStatus;
import ru.sber.sberlunch.util.events.admin.room.AddUserToRoomEvent;
import ru.sber.sberlunch.util.events.admin.room.RoomAdminEvent;
import ru.sber.sberlunch.util.events.admin.room.ShuffleRoomEvent;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class RoomAdminService {
    private final UserRepository userRepository;
    private final RoomRepository roomRepository;
    private final TelegramBot bot;
    private final SecureRandom secureRandom = new SecureRandom();

    @EventListener
    @Transactional
    public void handleAdminEvent(RoomAdminEvent adminEvent) {
        Optional<UserEntity> opt = userRepository.findById(adminEvent.getChatId());

        if (opt.isPresent()) {
            UserEntity user = opt.get();

            if (user.getRole().equals(RoleEntity.of(Role.ADMIN)) || user.getRoom().getAdminId().equals(adminEvent.getChatId())) {
                bot.sendMessage(adminEvent.getChatId(), "Панель админа комнаты", adminMarkup());
            } else if (user.getRoom() == null) {
                bot.sendMessage(adminEvent.getChatId(), "Обратитесь к любому администратору, чтобы добавил вас в комнату");
            } else {
                bot.sendMessage(adminEvent.getChatId(), "Ваш администратор: " + userRepository.findById(user.getRoom().getAdminId()));
            }
        }
    }

    @EventListener
    @Transactional
    public void handleShuffleRoomEvent(ShuffleRoomEvent shuffleRoomEvent) {
        Optional<UserEntity> opt = userRepository.findById(shuffleRoomEvent.getChatId());

        if (opt.isPresent()) {
            UserEntity user = opt.get();

            if (user.getRole().equals(Role.ADMIN) || user.getRoom().getAdminId().equals(shuffleRoomEvent.getChatId())) {
                shuffleRoom(user.getRoom(), 6); //TODO: сделать через настройку
                bot.sendMessage(shuffleRoomEvent.getChatId(), "Комната перемешана!");
            }
        }
    }

    @EventListener
    @Transactional
    public void handleAddUserToRoomEvent(AddUserToRoomEvent addUserToRoomEvent) {
        Optional<UserEntity> opt = userRepository.findById(addUserToRoomEvent.getChatId());

        if (opt.isPresent()) {
            UserEntity user = opt.get();
            user.setActivityStatus(UserActivityStatus.ACCEPTING_TO_ROOM);

            if (user.getRole().equals(RoleEntity.of(Role.ADMIN)) || user.getRoom().getAdminId().equals(addUserToRoomEvent.getChatId())) {
                bot.sendMessage(addUserToRoomEvent.getChatId(), "В следующем сообщении напиши ТГ юзернейм пользователя, которого хочешь добавить в комнату.\n" +
                        "Имей в виду, что он должен быть зарегистрирован в боте!");
            }

            userRepository.save(user);
        }


    }

    private InlineKeyboardMarkup adminMarkup() {
        InlineKeyboardMarkup inlineKeyboard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();

        List<InlineKeyboardButton> rowInline1 = new ArrayList<>();
        InlineKeyboardButton button1 = new InlineKeyboardButton();
        button1.setText("Добавить юзера");
        button1.setCallbackData("add_user_to_room");
        rowInline1.add(button1);

        InlineKeyboardButton button2 = new InlineKeyboardButton();
        button2.setText("Перемешать команды");
        button2.setCallbackData("shuffle_room");
        rowInline1.add(button2);

        rowsInline.add(rowInline1);
        inlineKeyboard.setKeyboard(rowsInline);

        return inlineKeyboard;
    }

    @Transactional
    protected void shuffleRoom(Room room, Integer teamSize) {
        List<UserEntity> users = room.getUsers().stream().filter(UserEntity::getIsReady).collect(Collectors.toList());

        if (users.isEmpty()) {
            return;
        }

        users = secureShuffle(users); //TODO: шафл с весами

        int teamCount = (int) Math.ceil((double) users.size() / teamSize);

        for (int i = 0; i < users.size(); i++) {
            int teamNumber = (i % teamCount) + 1;
            users.get(i).setTeamId(teamNumber);
        }

        room.setTeamAmount(teamCount);

        userRepository.saveAll(users);
        roomRepository.save(room);
    }

    private <T> List<T> secureShuffle(List<T> list) {
        List<T> shuffledList = Lists.newArrayList(list);
        int n = shuffledList.size();

        for (int i = n - 1; i > 0; i--) {
            int j = secureRandom.nextInt(i + 1);

            T temp = shuffledList.get(i);
            shuffledList.set(i, shuffledList.get(j));
            shuffledList.set(j, temp);
        }

        return shuffledList;
    }
}
