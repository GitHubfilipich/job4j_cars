package ru.job4j.cars.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum PostingPeriod {
    DAY("За сутки"),
    WEEK("За неделю"),
    MONTH("За месяц");

    private final String displayName;
}
