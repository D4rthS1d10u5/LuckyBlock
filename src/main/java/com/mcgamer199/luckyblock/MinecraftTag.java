package com.mcgamer199.luckyblock;

import lombok.Getter;

public enum MinecraftTag {

    CAN_PLACE_ON("CanPlaceOn"),
    CAN_DESTROY("CanDestroy");

    @Getter
    private final String tagName;

    MinecraftTag(String tagName) {
        this.tagName = tagName;
    }
}