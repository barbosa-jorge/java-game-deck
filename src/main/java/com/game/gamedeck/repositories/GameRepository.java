package com.game.gamedeck.repositories;

import com.game.gamedeck.model.CardEnum;
import com.game.gamedeck.model.Game;

import java.util.List;
import java.util.Optional;

public interface GameRepository {
    Game save(Game game);
    Optional<Game> delete(String gameId);
    Optional<Game> findById(String gameId);
    List<Game> findAll();
    Optional<Game> findGameOnlyWithCards(String gameId);
    Optional<Game> findGameOnlyWithPlayer(String gameId, String playerName);
    Optional<Game> findGameOnlyWithPlayers(String gameId);
    boolean isPlayerExists(String gameId, String playerName);
    Optional<Game> updateGameCards(String gameId, List<CardEnum> cards);
    Optional<Game> addNewPlayer(String gameId, String playerName);
    Optional<Game> addNewDeck(String gameId, List<CardEnum> cards);
    Optional<Game> removePlayer(String gameId, String playerName);
}
