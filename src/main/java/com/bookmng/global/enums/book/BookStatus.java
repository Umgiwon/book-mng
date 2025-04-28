package com.bookmng.global.enums.book;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum BookStatus {
    AVAILABLE("대여가능"),
    DAMAGED("훼손됨"),
    LOST("분실됨"),
    ;

    private final String description;
}
