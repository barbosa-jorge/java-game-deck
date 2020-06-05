package com.game.gamedeck.services;

import com.game.gamedeck.model.CardEnum;
import com.game.gamedeck.requests.AddPlayerRequestDTO;
import com.game.gamedeck.requests.CreateGameRequestDTO;
import com.game.gamedeck.responses.GameResponseDTO;
import com.game.gamedeck.responses.OperationStatus;
import com.game.gamedeck.responses.PlayerTotalResponseDTO;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public interface GameService {
    GameResponseDTO createGame(CreateGameRequestDTO createGameRequestDTO);
    OperationStatus deleteGame(String gameId);
    GameResponseDTO dealCards(String gameId, String playerName);
    GameResponseDTO addPlayer(String gameId, AddPlayerRequestDTO addPlayerRequestDTO);
    GameResponseDTO removePlayer(String gameId, String playerName);
    GameResponseDTO addDeck(String gameId);
    GameResponseDTO shuffleCards(String gameId);
    List<GameResponseDTO> getAllGames();
    List<CardEnum> getPlayerCards(String gameId, String playerName);
    List<PlayerTotalResponseDTO> getPlayersTotals(String gameId);
    Map<String, Long> getCountCardsLeft(String gameId);
    TreeMap<CardEnum, Long> getCountRemainingCardsSortedBySuitAndFaceValue(String gameId);
}
