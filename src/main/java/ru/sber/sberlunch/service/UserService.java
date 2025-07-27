package ru.sber.sberlunch.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.sber.sberlunch.model.entity.UserEntity;
import ru.sber.sberlunch.repository.UserRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;

    public void createIfNotExists(UserEntity userEntity) {
        if (!userRepository.existsById(userEntity.getID())) {
            userRepository.save(userEntity);
        }
        log.warn(">> Пользователь {} уже зарегистрирован", userEntity.getID());
    }
}
