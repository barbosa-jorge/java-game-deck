package com.game.gamedeck.services;

import com.game.gamedeck.responses.CreateGameResponse;

public interface GameService {
    CreateGameResponse createGame();
    void deleteGame(String gameId);
}
