package com.game.gamedeck.services.impl;

import com.game.gamedeck.exceptions.GameException;
import com.game.gamedeck.model.CardEnum;
import com.game.gamedeck.model.Game;
import com.game.gamedeck.model.Player;
import com.game.gamedeck.repositories.GameRepository;
import com.game.gamedeck.requests.AddPlayerRequest;
import com.game.gamedeck.requests.CreateGameRequest;
import com.game.gamedeck.services.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GameServiceImpl implements GameService {

    @Autowired
    private GameRepository repository;

    @Override
    public List<Game> getAllGames() {
        return repository.findAll();
    }

    public Game createGame(CreateGameRequest createGameRequest) {

        validateMandatoryRequestFields(createGameRequest);

        Game game = new Game();
        addPlayersToGame(createGameRequest.getPlayers(), game);
        addCardsToGameDeck(createGameRequest.getNumberOfDecks(), game);

        return repository.save(game);
    }

    @Override
    public void deleteGame(String gameId) {
        requiredNonEmpty(gameId, "Game Id");

        Game game = repository.findById(gameId)
                .orElseThrow(() -> new GameException("Game not found!"));

        repository.delete(game);
    }

    @Override
    public Game dealCards(String gameId, String playerName) {

        requiredNonEmpty(gameId, "Game Id");
        requiredNonEmpty(playerName, "Player Name");

        Game game = repository.findById(gameId).
                orElseThrow(() -> new GameException("Game not found!"));

        pickCardAndAddToPlayer(game, playerName);

        return repository.save(game);
    }

    @Override
    public List<CardEnum> getPlayerCards(String gameId, String playerName) {

        // TODO need to be implemented
        return new ArrayList<>();
    }

    public Game addPlayer(String gameId, AddPlayerRequest addPlayerRequest) {

        String playerName = addPlayerRequest.getPlayerName();

        requiredNonEmpty(gameId, "Game Id");
        requiredNonEmpty(playerName, "Player Name");

        Game game = repository.findById(gameId).
                orElseThrow(() -> new GameException("Game not found!"));

        if (game.getPlayers().contains(playerName)) {
            throw new GameException("User already exists");
        }

        game.getPlayers().add(new Player(playerName));

        return repository.save(game);
    }

    public Game removePlayer(String gameId, String playerName) {

        requiredNonEmpty(gameId, "Game Id");
        requiredNonEmpty(playerName, "Player Name");

        Game game = repository.findById(gameId).
                orElseThrow(() -> new GameException("Game not found!"));

        game.getPlayers().removeIf(p -> p.getName().equalsIgnoreCase(playerName));
        return repository.save(game);

    }

    public Game addDeck(String gameId) {
        Game game = repository.findById(gameId).
                orElseThrow(() -> new GameException("Game not found!"));

        game.getGameDeckCards().addAll(CardEnum.createDeck());
        return repository.save(game);
    }

    private void pickCardAndAddToPlayer(Game game, String playerName) {

        if (CollectionUtils.isEmpty(game.getGameDeckCards())) {
            throw new GameException("No more cards available!");
        }

        CardEnum pickedCard = game.getGameDeckCards().remove(0);

        List<Player> players = game.getPlayers().stream()
            .map(player -> {
                if (player.getName().equalsIgnoreCase(playerName)) {
                    player.getOnHandCards().add(pickedCard);
                    return player;
                }
                return player;
            }).collect(Collectors.toList());

        game.setPlayers(players);
    }

    private void addPlayersToGame(List<String> playerNames, Game game) {
        List<Player> players = playerNames.stream()
                .map(playerName -> new Player(playerName))
                .collect(Collectors.toList());
        game.setPlayers(players);
    }

    private void addCardsToGameDeck(int numberOfDecks, Game game) {
        List<CardEnum> gameDeckCards = new ArrayList<>();

        for (int i = 0; i < numberOfDecks; i++) {
            gameDeckCards.addAll(CardEnum.createDeck());
        }

        game.setGameDeckCards(gameDeckCards);
    }

    private void validateMandatoryRequestFields(CreateGameRequest request) {

        if (request.getNumberOfDecks() < 1) {
            throw new GameException("Please, add at least one deck of cards to the game.");
        }

        if (CollectionUtils.isEmpty(request.getPlayers())) {
            throw new GameException("Please, add at least one player to the game.");
        }
    }

    private void requiredNonEmpty(String fieldValue, String fieldName) {
        if (StringUtils.isEmpty(fieldValue)) {
            throw new GameException(
                    String.format("%s cannot be neither null nor empty", fieldName));
        }
    }
}
