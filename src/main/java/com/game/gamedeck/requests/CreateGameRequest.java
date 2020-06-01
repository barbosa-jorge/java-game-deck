package com.game.gamedeck.requests;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class CreateGameRequest {
    private List<String> players = new ArrayList<>();
    private Integer numberOfDecks;
}
