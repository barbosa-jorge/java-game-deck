package com.game.gamedeck.responses;

import com.game.gamedeck.model.Card;
import com.game.gamedeck.model.Player;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GameResponseDTO {
    private String id;
    private List<Card> gameCards = new ArrayList<>();
    private List<Player> players = new ArrayList<>();
}

