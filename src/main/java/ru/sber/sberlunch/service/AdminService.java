package ru.sber.sberlunch.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.sber.sberlunch.config.bot.TelegramBot;
import ru.sber.sberlunch.model.entity.RoleEntity;
import ru.sber.sberlunch.model.entity.UserEntity;
import ru.sber.sberlunch.repository.UserRepository;
import ru.sber.sberlunch.util.enums.Role;
import ru.sber.sberlunch.util.events.admin.AdminEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AdminService {
    private final UserRepository userRepository;
    private final TelegramBot bot;

    @EventListener
    public void handleAdminEvent(AdminEvent adminEvent) {
        Optional<UserEntity> opt = userRepository.findById(adminEvent.getChatId());

        if (opt.isPresent()) {
            UserEntity user = opt.get();

            if (user.getRole().equals(RoleEntity.of(Role.ADMIN))) {
                bot.sendMessage(adminEvent.getChatId(), "Панель админа", adminMarkup());
            }
        }
    }

    private InlineKeyboardMarkup adminMarkup() {
        InlineKeyboardMarkup inlineKeyboard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();

        List<InlineKeyboardButton> rowInline1 = new ArrayList<>();
        InlineKeyboardButton button1 = new InlineKeyboardButton();
        button1.setText("Пока думаю че нужно");
        button1.setCallbackData("admin_clicked");
        rowInline1.add(button1);

        rowsInline.add(rowInline1);
        inlineKeyboard.setKeyboard(rowsInline);

        return inlineKeyboard;
    }
}
