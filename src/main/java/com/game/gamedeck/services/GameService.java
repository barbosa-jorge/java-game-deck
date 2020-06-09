package com.game.gamedeck.services;

import com.game.gamedeck.model.Card;
import com.game.gamedeck.model.CardsBySuit;
import com.game.gamedeck.model.CardsBySuitAndValue;
import com.game.gamedeck.requests.AddPlayerRequestDTO;
import com.game.gamedeck.requests.CreateGameRequestDTO;
import com.game.gamedeck.responses.GameResponseDTO;
import com.game.gamedeck.responses.OperationStatus;
import com.game.gamedeck.responses.PlayerTotalResponseDTO;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Map;

public interface GameService {
    GameResponseDTO createGame(CreateGameRequestDTO createGameRequestDTO);
    OperationStatus deleteGame(String gameId);
    GameResponseDTO dealCards(String gameId, String playerName);
    GameResponseDTO addPlayer(String gameId, AddPlayerRequestDTO addPlayerRequestDTO);
    GameResponseDTO removePlayer(String gameId, String playerName);
    GameResponseDTO addDeck(String gameId);
    GameResponseDTO shuffleCards(String gameId);
    List<GameResponseDTO> getAllGames();
    List<Card> getPlayerCards(String gameId, String playerName);
    List<PlayerTotalResponseDTO> getPlayersTotals(String gameId);
    Map<String, Long> getCardsLeftBySuitUsingCollectors(String gameId);
    List<CardsBySuit> getCountRemainingCardsBySuit(String gameId);
    List<CardsBySuitAndValue> getCountRemainingCardsSorted(String gameId, Sort sort);
}
