package ru.sber.sberlunch.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.sber.sberlunch.util.enums.Role;

@Entity
@Table(name = "roles")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoleEntity {
    @Id
    private Long ID;

    @Column(length = 16)
    private String roleName;

    public static RoleEntity of(Role role) {
        return RoleEntity.builder()
                .ID(Long.valueOf(role.getId()))
                .roleName(role.getRole())
                .build();
    }
}
