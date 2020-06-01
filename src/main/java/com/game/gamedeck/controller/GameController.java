package com.game.gamedeck.controller;

import com.game.gamedeck.requests.CreateGameRequest;
import com.game.gamedeck.responses.CreateGameResponse;
import com.game.gamedeck.services.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class GameController {

    @Autowired
    private GameService gameService;

    @GetMapping("/games")
    public ResponseEntity<CreateGameResponse> createGame(CreateGameRequest createGameRequest) {
        return new ResponseEntity<CreateGameResponse>(gameService.createGame(),
                HttpStatus.CREATED);
    }

    @DeleteMapping("/games/{id}")
    public ResponseEntity deleteGame(@PathVariable("id") String gameId) {
        gameService.deleteGame(gameId);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }
}
