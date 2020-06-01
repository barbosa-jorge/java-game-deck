package com.game.gamedeck.services.impl;

import com.game.gamedeck.model.Game;
import com.game.gamedeck.repositories.GameRepository;
import com.game.gamedeck.responses.CreateGameResponse;
import com.game.gamedeck.services.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GameServiceImpl implements GameService {

    @Autowired
    private GameRepository repository;

    public CreateGameResponse createGame() {
        //validate
        Game game = repository.save(new Game());
        return new CreateGameResponse();
    }

    @Override
    public void deleteGame(String gameId) {
        Game game = repository.findById(gameId);
        //validate game
        repository.delete(game);
    }
}
