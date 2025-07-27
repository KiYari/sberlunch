package ru.sber.sberlunch.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.sber.sberlunch.util.enums.Role;
import ru.sber.sberlunch.util.enums.UserStatus;

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
    private UserStatus status = UserStatus.PENDING;

    public static UserEntity getDefaultUserEntity() {
        return new UserEntity(null, "", "", LocalDateTime.now(), RoleEntity.of(Role.USER), UserStatus.PENDING);
    }
}
