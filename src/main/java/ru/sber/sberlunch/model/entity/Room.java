package ru.sber.sberlunch.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "rooms")
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class Room {
    @Id
    private Long ID;

    private Long adminId;
    @Setter
    private Integer teamAmount;
    @OneToMany(mappedBy = "room", fetch = FetchType.EAGER)
    private List<UserEntity> users;
}
