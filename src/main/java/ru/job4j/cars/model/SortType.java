package ru.job4j.cars.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum SortType {
    DATE_DESC("По дате размещения (новые вначале)"),
    PRICE_ASC("По цене (сначала дешёвые)"),
    PRICE_DESC("По цене (сначала дорогие)");

    private final String displayName;
}
