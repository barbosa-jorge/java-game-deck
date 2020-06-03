package com.game.gamedeck.services.impl;

import com.game.gamedeck.exceptions.GameException;
import com.game.gamedeck.model.CardEnum;
import com.game.gamedeck.model.Game;
import com.game.gamedeck.model.Player;
import com.game.gamedeck.repositories.GameRepository;
import com.game.gamedeck.requests.AddPlayerRequest;
import com.game.gamedeck.requests.CreateGameRequest;
import com.game.gamedeck.responses.PlayerTotal;
import com.game.gamedeck.services.GameService;
import com.game.gamedeck.shared.AppErrorConstants;
import com.game.gamedeck.shared.GameConstants;
import com.game.gamedeck.utils.DeckUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class GameServiceImpl implements GameService {

    private static final Object[] NO_PARAMS = null;

    @Autowired
    private GameRepository repository;

    @Autowired
    private MessageSource messageSource;

    @Override
    public List<Game> getAllGames() {
        return repository.findAll();
    }

    @Override
    public Game createGame(CreateGameRequest createGameRequest) {
        validateMandatoryRequestFields(createGameRequest);

        Game game = new Game();
        addPlayersToGame(createGameRequest.getPlayers(), game);
        addCardsToGameDeck(createGameRequest.getNumberOfDecks(), game);
        return repository.save(game);
    }

    @Override
    public void deleteGame(String gameId) {
        requiredNonEmpty(gameId, GameConstants.GAME_ID);
        Game game = findGameById(gameId);
        repository.delete(game);
    }

    @Override
    public Game dealCards(String gameId, String playerName) {
        requiredNonEmpty(gameId, GameConstants.GAME_ID);
        requiredNonEmpty(playerName, GameConstants.PLAYER_NAME);

        Game game = findGameById(gameId);
        pickCardAndAddToPlayer(game, playerName);
        return repository.save(game);
    }

    @Override
    public List<PlayerTotal> getPlayersTotals(String gameId) {
        Game game = findGameById(gameId);

        return game.getPlayers().stream()
                .map(this::getTotalsCardsForEachPlayer)
                .sorted(Comparator.comparing(PlayerTotal::getTotal).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public Game shuffleCards(String gameId) {
        Game game = findGameById(gameId);
        DeckUtils.shuffleCards(game.getGameDeckCards());
        return repository.save(game);
    }

    @Override
    public Map<String, Long> getCountCardsLeft(String gameId) {
        Game game = findGameById(gameId);

        return game.getGameDeckCards().stream()
                .collect(Collectors.groupingBy(CardEnum::getSuit, Collectors.counting()));
    }

    @Override
    public TreeMap<CardEnum, Long> getCountRemainingCardsSortedBySuitAndFaceValue(String gameId) {
        Game game = findGameById(gameId);

        Comparator<CardEnum> sortBySuit = Comparator.comparing(CardEnum::getSuit);
        Comparator<CardEnum> sortByValueDesc = Comparator.comparing(CardEnum::getValue).reversed();
        Comparator<CardEnum> sortBySuitAndValueDesc = sortBySuit.thenComparing(sortByValueDesc);

        return game.getGameDeckCards().stream()
            .collect(Collectors.groupingBy(Function.identity(),
                () -> new TreeMap<>(sortBySuitAndValueDesc),
                    Collectors.counting()));
    }

    @Override
    public List<CardEnum> getPlayerCards(String gameId, String playerName) {
        Game game = findGameById(gameId);

        Player player = game.getPlayers().stream()
            .filter(p -> p.getName().equalsIgnoreCase(playerName))
            .findFirst()
            .orElseThrow(() -> new GameException(buildErrorMessage(
                AppErrorConstants.ERROR_PLAYER_NOT_FOUND, NO_PARAMS)));

        return player.getOnHandCards();
    }

    @Override
    public Game addPlayer(String gameId, AddPlayerRequest addPlayerRequest) {
        String playerName = addPlayerRequest.getPlayerName();

        requiredNonEmpty(gameId, "Game Id");
        requiredNonEmpty(playerName, "Player Name");

        Game game = findGameById(gameId);
        validateExistentPlayerInGame(game, playerName);
        game.getPlayers().add(new Player(playerName));
        return repository.save(game);
    }

    @Override
    public Game removePlayer(String gameId, String playerName) {
        requiredNonEmpty(gameId, "Game Id");
        requiredNonEmpty(playerName, "Player Name");

        Game game = findGameById(gameId);
        game.getPlayers().removeIf(p -> p.getName().equalsIgnoreCase(playerName));
        return repository.save(game);
    }

    @Override
    public Game addDeck(String gameId) {
        Game game = findGameById(gameId);
        game.getGameDeckCards().addAll(CardEnum.createDeck());
        return repository.save(game);
    }

    private int calculatePlayerCards(List<CardEnum> onHandCards) {
        return onHandCards.stream().mapToInt(card -> card.getValue()).sum();
    }

    private void pickCardAndAddToPlayer(Game game, String playerName) {
        validateAvailableCards(game);

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
            throw new GameException(buildErrorMessage(
                AppErrorConstants.ERROR_ADD_DECK_TO_GAME, NO_PARAMS));
        }
        if (CollectionUtils.isEmpty(request.getPlayers())) {
            throw new GameException(buildErrorMessage(
                AppErrorConstants.ERROR_ADD_PLAYER_TO_GAME, NO_PARAMS));
        }
    }

    private void requiredNonEmpty(String fieldValue, String fieldName) {
        if (StringUtils.isEmpty(fieldValue)) {
            throw new GameException(buildErrorMessage(
                AppErrorConstants.ERROR_FIELD_CANNOT_BE_EMPTY,
                    new Object[]{ fieldName }));
        }
    }

    private PlayerTotal createPlayerTotal(String playerName, int total) {
        PlayerTotal playerTotal = new PlayerTotal();
        playerTotal.setPlayer(playerName);
        playerTotal.setTotal(total);
        return playerTotal;
    }

    private PlayerTotal getTotalsCardsForEachPlayer(Player player) {
        int total = calculatePlayerCards(player.getOnHandCards());
        return createPlayerTotal(player.getName(), total);
    }

    private Game findGameById(String gameId) {
        return repository.findById(gameId)
            .orElseThrow(() -> new GameException(buildErrorMessage(
                AppErrorConstants.ERROR_GAME_NOT_FOUND, NO_PARAMS)));
    }

    private void validateExistentPlayerInGame(Game game, String playerName) {
        if (game.getPlayers().contains(playerName)) {
            throw new GameException(buildErrorMessage(
                AppErrorConstants.ERROR_USER_ALREADY_EXISTS, new Object[]{ playerName }));
        }
    }

    private void validateAvailableCards(Game game) {
        if (CollectionUtils.isEmpty(game.getGameDeckCards())) {
            throw new GameException(buildErrorMessage(
                AppErrorConstants.ERROR_NO_MORE_CARDS_AVAILABLE, NO_PARAMS));
        }
    }

    private String buildErrorMessage(String errorBundleKey, Object[] params) {
        return messageSource.getMessage(errorBundleKey, params,
            LocaleContextHolder.getLocale());
    }
}