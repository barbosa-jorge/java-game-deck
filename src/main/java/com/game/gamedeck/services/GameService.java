package com.game.gamedeck.services;

import com.game.gamedeck.model.CardEnum;
import com.game.gamedeck.model.Game;
import com.game.gamedeck.requests.AddPlayerRequest;
import com.game.gamedeck.requests.CreateGameRequest;

import java.util.List;

public interface GameService {
    List<Game> getAllGames();
    Game createGame(CreateGameRequest createGameRequest);
    void deleteGame(String gameId);
    Game dealCards(String gameId, String playerName);
    Game addPlayer(String gameId, AddPlayerRequest addPlayerRequest);
    Game removePlayer(String gameId, String playerName);
    Game addDeck(String gameId);
    List<CardEnum> getPlayerCards(String gameId, String playerName);
}
