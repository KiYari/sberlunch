package ru.sber.sberlunch.util.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserActivityStatus {
    STABLE("STABLE"),
    PROPOSING("PROPOSING"),
    ACCEPTING_TO_ROOM("ACCEPTING_TO_ROOM"),
    ADMIN_ACCEPTING_TO_SYSTEM("ADMIN_ACCEPTING_TO_SYSTEM");

    private final String status;
}
