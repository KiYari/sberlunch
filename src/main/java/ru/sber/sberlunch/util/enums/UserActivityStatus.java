package ru.sber.sberlunch.util.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserActivityStatus {
    STABLE("STABLE"),
    PROPOSING("PROPOSING"),
    ACCEPTING("ACCEPTING");

    private final String status;
}
