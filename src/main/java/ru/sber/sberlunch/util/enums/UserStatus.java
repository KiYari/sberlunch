package ru.sber.sberlunch.util.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserStatus {
    PENDING("PENDING"),
    REVIEWING("REVIEWING"),
    ACTIVE("ACTIVE");

    private final String status;
}
