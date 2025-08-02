package ru.sber.sberlunch.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.sber.sberlunch.config.bot.TelegramBot;
import ru.sber.sberlunch.model.entity.RoleEntity;
import ru.sber.sberlunch.model.entity.UserEntity;
import ru.sber.sberlunch.repository.UserRepository;
import ru.sber.sberlunch.util.enums.Role;
import ru.sber.sberlunch.util.enums.UserActivityStatus;
import ru.sber.sberlunch.util.enums.UserRegistrationStatus;
import ru.sber.sberlunch.util.events.admin.AcceptPeopleEvent;
import ru.sber.sberlunch.util.events.admin.AdminEvent;
import ru.sber.sberlunch.util.events.admin.CheckReviewingEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {
    private final UserRepository userRepository;
    private final TelegramBot bot;

    @EventListener
    @Transactional
    public void handleAdminEvent(AdminEvent adminEvent) {
        Optional<UserEntity> opt = userRepository.findById(adminEvent.getChatId());

        if (opt.isPresent()) {
            UserEntity user = opt.get();

            System.out.println(user.getRole().getRole());
            if (user.getRole().equals(Role.ADMIN)) {
                bot.sendMessage(adminEvent.getChatId(), "Панель админа", adminMarkup());
            }
        }
    }

    @EventListener
    @Transactional
    public void handleCheckReviewingEvent(CheckReviewingEvent checkReviewingEvent) {
        Optional<UserEntity> opt = userRepository.findById(checkReviewingEvent.getChatId());

        if (opt.isPresent()) {
            UserEntity user = opt.get();

            if (user.getRole().equals(Role.ADMIN)) {
                String users = userRepository.findAllByRoomAndRegistrationStatus(user.getRoom(), UserRegistrationStatus.REVIEWING)
                        .stream()
                        .map(UserEntity::getUsername)
                        .collect(Collectors.joining(", "))
                        .trim();

                if (users.isEmpty()) {
                    bot.sendMessage(checkReviewingEvent.getChatId(), "Никого нету");
                    return;
                }

                user.setActivityStatus(UserActivityStatus.ADMIN_ACCEPTING_TO_SYSTEM);
                bot.sendMessage(checkReviewingEvent.getChatId(), "Ожидают проверки: \n" + users);
            }
            userRepository.save(user);
        }
    }

    private InlineKeyboardMarkup adminMarkup() {
        InlineKeyboardMarkup inlineKeyboard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();

        List<InlineKeyboardButton> rowInline1 = new ArrayList<>();
        InlineKeyboardButton button1 = new InlineKeyboardButton();
        button1.setText("Посмотреть в статусе REVIEWING");
        button1.setCallbackData("reviewing_status_check");
        rowInline1.add(button1);

        rowsInline.add(rowInline1);
        inlineKeyboard.setKeyboard(rowsInline);

        return inlineKeyboard;
    }
}
