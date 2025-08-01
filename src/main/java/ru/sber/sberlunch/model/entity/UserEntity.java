package ru.sber.sberlunch.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.sber.sberlunch.util.enums.Role;
import ru.sber.sberlunch.util.enums.UserActivityStatus;
import ru.sber.sberlunch.util.enums.UserRegistrationStatus;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class UserEntity {
    @Id
    @Setter
    private Long ID;

    @Column(nullable = false, length = 64)
    @Setter
    private String username;

    @Column(nullable = false, length = 64)
    @Setter
    private String realName;

    @Column
    private LocalDateTime createdAt = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role")
    private RoleEntity role = RoleEntity.of(Role.USER);

    @Enumerated(EnumType.STRING)
    @Setter
    private UserRegistrationStatus registrationStatus = UserRegistrationStatus.PENDING;

    @Enumerated(EnumType.STRING)
    @Setter
    private UserActivityStatus activityStatus;

    @Setter
    @Column
    private Integer teamId;

    @Column(length = 255)
    @Setter
    private String placeProposed;

    @Setter
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "room_id")
    private Room room;

    public static UserEntity getDefaultUserEntity() {
        return new UserEntity(null, "", "", LocalDateTime.now(), RoleEntity.of(Role.USER), UserRegistrationStatus.PENDING, UserActivityStatus.STABLE, 0, "", null);
    }
}
