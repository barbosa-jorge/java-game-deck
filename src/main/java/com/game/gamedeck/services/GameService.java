package com.game.gamedeck.services;

import com.game.gamedeck.model.CardEnum;
import com.game.gamedeck.model.Game;
import com.game.gamedeck.requests.AddPlayerRequest;
import com.game.gamedeck.requests.CreateGameRequest;
import com.game.gamedeck.responses.PlayerTotal;

import java.util.List;
import java.util.Map;

public interface GameService {
    List<Game> getAllGames();
    Game createGame(CreateGameRequest createGameRequest);
    void deleteGame(String gameId);
    Game dealCards(String gameId, String playerName);
    Game addPlayer(String gameId, AddPlayerRequest addPlayerRequest);
    Game removePlayer(String gameId, String playerName);
    Game addDeck(String gameId);
    List<CardEnum> getPlayerCards(String gameId, String playerName);
    List<PlayerTotal> getPlayersTotals(String gameId);
    Map<String, Long> getCountCardsLeft(String gameId);
}
