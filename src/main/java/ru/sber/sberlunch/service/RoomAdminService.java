package ru.sber.sberlunch.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.sber.sberlunch.config.bot.TelegramBot;
import ru.sber.sberlunch.model.entity.RoleEntity;
import ru.sber.sberlunch.model.entity.Room;
import ru.sber.sberlunch.model.entity.UserEntity;
import ru.sber.sberlunch.repository.UserRepository;
import ru.sber.sberlunch.util.enums.Role;
import ru.sber.sberlunch.util.enums.UserActivityStatus;
import ru.sber.sberlunch.util.events.admin.AddUserToRoomEvent;
import ru.sber.sberlunch.util.events.admin.RoomAdminEvent;
import ru.sber.sberlunch.util.events.admin.ShuffleRoomEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class RoomAdminService {
    private final UserRepository userRepository;
    private final TelegramBot bot;

    @EventListener
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
    public void handleShuffleRoomEvent(ShuffleRoomEvent shuffleRoomEvent) {
        Optional<UserEntity> opt = userRepository.findById(shuffleRoomEvent.getChatId());

        if (opt.isPresent()) {
            UserEntity user = opt.get();

            if (user.getRole().equals(RoleEntity.of(Role.ADMIN)) || user.getRoom().getAdminId().equals(shuffleRoomEvent.getChatId())) {
                bot.sendMessage(shuffleRoomEvent.getChatId(), "Комната перемешана!");//TODO: реализовать шафл комнаты
            }
        }
    }

    @EventListener
    public void handleAddUserToRoomEvent(AddUserToRoomEvent addUserToRoomEvent) {
        Optional<UserEntity> opt = userRepository.findById(addUserToRoomEvent.getChatId());

        if (opt.isPresent()) {
            UserEntity user = opt.get();
            user.setActivityStatus(UserActivityStatus.ACCEPTING);

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

    private void shuffleRoom(Room room) {

    }
}
