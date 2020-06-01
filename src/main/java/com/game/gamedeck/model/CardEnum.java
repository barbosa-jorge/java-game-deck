package com.game.gamedeck.model;

import java.util.Arrays;
import java.util.List;

public enum CardEnum {

    AS_HEARTS(1, "AS", "HEARTS"),
    AS_SPADES(1, "AS", "SPADES"),
    AS_DIAMONDS(1, "AS", "DIAMONDS"),
    AS_CLUBS(1, "AS", "CLUBS"),

    TWO_HEARTS(2, "TWO", "HEARTS"),
    TWO_SPADES(2, "TWO", "SPADES"),
    TWO_DIAMONDS(2, "TWO", "DIAMONDS"),
    TWO_CLUBS(2, "TWO", "CLUBS");

    private int value;
    private String suit;
    private String faceValue;

    public int getValue() {
        return value;
    }

    public String getSuit() {
        return suit;
    }

    public String getFaceValue() {
        return faceValue;
    }

    CardEnum(int value, String faceValue, String suit) {
        this.value = value;
        this.suit = suit;
        this.faceValue = faceValue;
    }

    public static List<CardEnum> createDeck() {
        return Arrays.asList(CardEnum.values());
    }
}
