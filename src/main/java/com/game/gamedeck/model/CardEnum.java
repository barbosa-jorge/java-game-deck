package com.game.gamedeck.model;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum CardEnum {

    ACE_HEARTS(1, "A", "HEARTS"),
    ACE_SPADES(1, "A", "SPADES"),
    ACE_DIAMONDS(1, "A", "DIAMONDS"),
    ACE_CLUBS(1, "A", "CLUBS"),

    TWO_HEARTS(2, "2", "HEARTS"),
    TWO_SPADES(2, "2", "SPADES"),
    TWO_DIAMONDS(2, "2", "DIAMONDS"),
    TWO_CLUBS(2, "2", "CLUBS"),

    THREE_HEARTS(3, "3", "HEARTS"),
    THREE_SPADES(3, "3", "SPADES"),
    THREE_DIAMONDS(3, "3", "DIAMONDS"),
    THREE_CLUBS(3, "3", "CLUBS"),

    FOUR_HEARTS(4, "4", "HEARTS"),
    FOUR_SPADES(4, "4", "SPADES"),
    FOUR_DIAMONDS(4, "4", "DIAMONDS"),
    FOUR_CLUBS(4, "4", "CLUBS"),

    FIVE_HEARTS(5, "5", "HEARTS"),
    FIVE_SPADES(5, "5", "SPADES"),
    FIVE_DIAMONDS(5, "5", "DIAMONDS"),
    FIVE_CLUBS(5, "5", "CLUBS"),

    SIX_HEARTS(6, "6", "HEARTS"),
    SIX_SPADES(6, "6", "SPADES"),
    SIX_DIAMONDS(6, "6", "DIAMONDS"),
    SIX_CLUBS(6, "6", "CLUBS"),

    SEVEN_HEARTS(7, "7", "HEARTS"),
    SEVEN_SPADES(7, "7", "SPADES"),
    SEVEN_DIAMONDS(7, "7", "DIAMONDS"),
    SEVEN_CLUBS(7, "7", "CLUBS"),

    EIGHT_HEARTS(8, "8", "HEARTS"),
    EIGHT_SPADES(8, "8", "SPADES"),
    EIGHT_DIAMONDS(8, "8", "DIAMONDS"),
    EIGHT_CLUBS(8, "8", "CLUBS"),

    NINE_HEARTS(9, "9", "HEARTS"),
    NINE_SPADES(9, "9", "SPADES"),
    NINE_DIAMONDS(9, "9", "DIAMONDS"),
    NINE_CLUBS(9, "9", "CLUBS"),

    TEN_HEARTS(10, "10", "HEARTS"),
    TEN_SPADES(10, "10", "SPADES"),
    TEN_DIAMONDS(10, "10", "DIAMONDS"),
    TEN_CLUBS(10, "10", "CLUBS"),

    JACK_HEARTS(11, "J", "HEARTS"),
    JACK_SPADES(11, "J", "SPADES"),
    JACK_DIAMONDS(11, "J", "DIAMONDS"),
    JACK_CLUBS(11, "J", "CLUBS"),

    QUEEN_HEARTS(12, "Q", "HEARTS"),
    QUEEN_SPADES(12, "Q", "SPADES"),
    QUEEN_DIAMONDS(12, "Q", "DIAMONDS"),
    QUEEN_CLUBS(12, "Q", "CLUBS"),

    KING_HEARTS(13, "K", "HEARTS"),
    KING_SPADES(13, "K", "SPADES"),
    KING_DIAMONDS(13, "K", "DIAMONDS"),
    KING_CLUBS(13, "K", "CLUBS");

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

    public static List<Card> createDeck() {
        List<Card> cards = Arrays.stream(CardEnum.values())
                .map(c -> new Card(c.getValue(), c.getSuit(), c.getFaceValue()))
                .collect(Collectors.toList());
        return cards;
    }
}
