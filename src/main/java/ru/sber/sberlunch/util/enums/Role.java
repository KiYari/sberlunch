package ru.sber.sberlunch.util.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Role {
    USER(1, "USER"),
    ADMIN( 2, "ADMIN");


    private final Integer id;
    private final String role;
}
