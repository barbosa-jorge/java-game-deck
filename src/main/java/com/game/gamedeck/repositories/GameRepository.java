package com.game.gamedeck.repositories;

import com.game.gamedeck.model.Card;
import com.game.gamedeck.model.CardsBySuit;
import com.game.gamedeck.model.CardsBySuitAndValue;
import com.game.gamedeck.model.Game;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;

public interface GameRepository {
    Optional<Game> save(Game game);
    Optional<Game> delete(String gameId);
    Optional<Game> findById(String gameId);
    List<Game> findAll();
    Optional<Game> findGameOnlyWithCards(String gameId);
    Optional<Game> findGameOnlyWithPlayer(String gameId, String playerName);
    Optional<Game> findGameOnlyWithPlayers(String gameId);
    boolean isPlayerExists(String gameId, String playerName);
    Optional<Game> updateGameCards(String gameId, List<Card> cards);
    Optional<Game> addNewPlayer(String gameId, String playerName);
    Optional<Game> addNewDeck(String gameId, List<Card> cards);
    Optional<Game> removePlayer(String gameId, String playerName);
    List<CardsBySuit> countRemainingCardsBySuit(String gameId);
    List<CardsBySuitAndValue> countRemainingCardsSorted(String gameId, Sort sort);
}
