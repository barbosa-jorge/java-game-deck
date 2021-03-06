package com.game.gamedeck.controller;

import com.game.gamedeck.model.Card;
import com.game.gamedeck.requests.AddPlayerRequestDTO;
import com.game.gamedeck.requests.CreateGameRequestDTO;
import com.game.gamedeck.responses.GameResponseDTO;
import com.game.gamedeck.responses.OperationStatus;
import com.game.gamedeck.responses.PlayerTotalResponseDTO;
import com.game.gamedeck.services.GameService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@RestController
@RequestMapping("/game-management/api/v1")
public class GameControllerV1 {

    @Autowired
    private GameService gameService;

    @GetMapping("/games")
    @ApiOperation(value="Find all games", notes="Find all games created")
    public ResponseEntity<List<GameResponseDTO>> findAllGames() {
        return new ResponseEntity<>(gameService.getAllGames(), HttpStatus.OK);
    }

    @PostMapping("/games")
    @ApiOperation(value="Create Game", notes="Create new game")
    public ResponseEntity<GameResponseDTO> createGame(@Valid @RequestBody CreateGameRequestDTO createGameRequestDTO) {
        return new ResponseEntity<>(gameService
                .createGame(createGameRequestDTO), HttpStatus.CREATED);
    }

    @DeleteMapping("/games/{game-id}")
    @ApiOperation(value="Delete Game", notes="Delete Game")
    public ResponseEntity<OperationStatus> deleteGame(@PathVariable("game-id") String gameId) {
        return new ResponseEntity(gameService.deleteGame(gameId), HttpStatus.OK);
    }

    @PutMapping("/games/{game-id}/players/{player-name}/deal-cards")
    @ApiOperation(value="Deal cards to a player",
            notes="Deal cards to a player. You need to enter a valid game id and player name. ")
    public ResponseEntity<GameResponseDTO> dealCards(@PathVariable("game-id") String gameId,
                                                     @PathVariable("player-name") String playerName) {
        return new ResponseEntity<>(gameService.dealCards(gameId, playerName), HttpStatus.OK);
    }

    @PostMapping("/games/{game-id}/players")
    @ApiOperation(value="Add player",
            notes="Add a new player into the game. " +
                    "You need to enter a valid game id and a valid json object containing the player name.")
    public ResponseEntity<GameResponseDTO> addPlayer(@PathVariable("game-id") String gameId,
                                                     @Valid @RequestBody AddPlayerRequestDTO addPlayerRequestDTO) {
        return new ResponseEntity<>(gameService.addPlayer(gameId, addPlayerRequestDTO),
                HttpStatus.CREATED);
    }

    @DeleteMapping("/games/{game-id}/players/{player-name}")
    @ApiOperation(value="Delete player",
            notes="Delete a player from the game. You need to enter an existent game id and player name.")
    public ResponseEntity<GameResponseDTO> removePlayer(@PathVariable("game-id") String gameId,
                                                        @PathVariable("player-name") String playerName) {

        return new ResponseEntity<>(gameService.removePlayer(gameId, playerName),
                HttpStatus.OK);
    }

    @PostMapping("/games/{game-id}/decks")
    @ApiOperation(value="Add deck", notes="Add a new deck into the game You need to enter a valid game id.")
    public ResponseEntity<GameResponseDTO> addDeck(@PathVariable("game-id") String gameId) {
        return new ResponseEntity<>(gameService.addDeck(gameId), HttpStatus.CREATED);
    }

    @GetMapping("/games/{game-id}/players/{player-name}/cards")
    @ApiOperation(value="Retrieve Player's cards",
            notes="Retrieve player's cards. You need to enter a valid game id and player name. ")
    public ResponseEntity<List<Card>> getPlayerCards(@PathVariable("game-id") String gameId,
                                                     @PathVariable("player-name") String playerName) {
        return new ResponseEntity<>(gameService.getPlayerCards(gameId, playerName), HttpStatus.OK);
    }

    @GetMapping("/games/{game-id}/players/totals")
    @ApiOperation(value="Retrieve player's totals", notes="Retrieve player's totals. You need to enter a valid game id.")
    public ResponseEntity<List<PlayerTotalResponseDTO>> getPlayersTotals(@PathVariable("game-id") String gameId) {
        return new ResponseEntity(gameService.getPlayersTotals(gameId), HttpStatus.OK);
    }

    @GetMapping("/games/{game-id}/cards/summary/remaining/suit")
    @ApiOperation(value="Retrieve count of cards left in the deck per suit",
            notes="Retrieve count of the cards left in the deck per suit. You need to enter a valid game id.")
    public ResponseEntity<Map<String, Long>> getCountRemainingCardsBySuit(@PathVariable("game-id") String gameId) {
        return new ResponseEntity(gameService.getCountRemainingCardsBySuit(gameId), HttpStatus.OK);
    }

    @GetMapping("/games/{game-id}/cards/summary/remaining")
    @ApiOperation(value="Retrieve count of cards left in the card, sorting them by suit and face value",
            notes="Retrieve count of cards left in the card, sorting them by suit and face value. " +
                    "You need to enter a valid game id.")
    public ResponseEntity<TreeMap<Card, Long>> getCountRemainingCardsSorted(
            @PathVariable("game-id") String gameId, Sort sort) {
        return new ResponseEntity(gameService
                .getCountRemainingCardsSorted(gameId, sort), HttpStatus.OK);
    }

    @PutMapping("/games/{game-id}/cards/shuffle")
    @ApiOperation(value="Shuffle cards", notes="Shuffle game cards. You need to enter a valid game id.")
    public ResponseEntity<GameResponseDTO> shuffleCards(@PathVariable("game-id") String gameId) {
        return new ResponseEntity(gameService.shuffleCards(gameId), HttpStatus.OK);
    }
}
