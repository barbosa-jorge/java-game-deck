package com.game.gamedeck.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Player {
    private String name;
    private List<Card> onHandCards = new ArrayList<>();

    public Player(String name) {
        this.name = name;
    }
}
