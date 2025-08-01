package ru.sber.sberlunch.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.sber.sberlunch.model.entity.Room;
import ru.sber.sberlunch.model.entity.UserEntity;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    List<UserEntity> findByRoom(Room room);
    List<UserEntity> findByRoomAndTeamId(Room room, Integer teamId);
}
