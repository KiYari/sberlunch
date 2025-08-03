package ru.sber.sberlunch.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.sber.sberlunch.model.entity.Room;
import ru.sber.sberlunch.model.entity.UserEntity;
import ru.sber.sberlunch.util.enums.UserRegistrationStatus;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    List<UserEntity> findByRoom(Room room);
    List<UserEntity> findByRoomAndTeamId(Room room, Integer teamId);
    Optional<UserEntity> findByUsername(String username);
    List<UserEntity> findAllByRoomAndRegistrationStatus(Room room, UserRegistrationStatus registrationStatus);
    List<UserEntity> findAllByRegistrationStatus(UserRegistrationStatus registrationStatus);
    Boolean existsByUsername(String username);
}
