package com.booking.configuration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum TokenType {
    BEARER("Bearer ");

    @Getter
    private final String tokenType;
}