package com.game.gamedeck.repositories;

import com.game.gamedeck.model.Game;

import java.util.List;
import java.util.Optional;

public interface GameRepository {
    Game save(Game game);
    void delete(Game game);
    Optional<Game> findById(String gameId);
    List<Game> findAll();
}
