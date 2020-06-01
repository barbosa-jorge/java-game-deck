package com.game.gamedeck.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document("game")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Game {
    @Id
    private String id;
    private List<CardEnum> gameDeckCards = new ArrayList<>();
    private List<Player> players = new ArrayList<>();
}
