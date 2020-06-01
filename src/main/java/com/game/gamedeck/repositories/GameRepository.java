package com.game.gamedeck.repositories;

import com.game.gamedeck.model.Game;

public interface GameRepository {
    Game save(Game game);
    void delete(Game game);
    Game findById(String gameId);
}
