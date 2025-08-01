package ru.sber.sberlunch.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.sber.sberlunch.model.entity.Room;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
}
