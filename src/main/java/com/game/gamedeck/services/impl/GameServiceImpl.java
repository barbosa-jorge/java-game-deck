package com.game.gamedeck.services.impl;

import com.game.gamedeck.exceptions.GameException;
import com.game.gamedeck.exceptions.NotFoundException;
import com.game.gamedeck.model.CardEnum;
import com.game.gamedeck.model.Game;
import com.game.gamedeck.model.Player;
import com.game.gamedeck.repositories.GameRepository;
import com.game.gamedeck.requests.AddPlayerRequest;
import com.game.gamedeck.requests.CreateGameRequest;
import com.game.gamedeck.responses.PlayerTotal;
import com.game.gamedeck.services.GameService;
import com.game.gamedeck.shared.constants.AppErrorConstants;
import com.game.gamedeck.shared.constants.GameConstants;
import com.game.gamedeck.shared.utils.DeckUtils;
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
        Game game = new Game();
        addPlayersToGame(createGameRequest.getPlayers(), game);
        addCardsToGameDeck(createGameRequest.getNumberOfDecks(), game);
        return repository.save(game);
    }

    @Override
    public void deleteGame(String gameId) {
        requiredNonEmpty(gameId, GameConstants.GAME_ID);
        repository.delete(gameId)
            .orElseThrow(() -> new NotFoundException(buildErrorMessage(
                AppErrorConstants.ERROR_GAME_NOT_FOUND, NO_PARAMS)));
    }

    @Override
    public Game dealCards(String gameId, String playerName) {
        requiredNonEmpty(gameId, GameConstants.GAME_ID);
        requiredNonEmpty(playerName, GameConstants.PLAYER_NAME);

        Game game = findGameById(gameId);
        validatePlayer(game.getPlayers(), playerName);

        pickCardAndAddToPlayer(game, playerName);
        return repository.save(game);
    }

    @Override
    public List<PlayerTotal> getPlayersTotals(String gameId) {

        List<Player> players = getGamePrayers(gameId);

        return players.stream()
                .map(this::getTotalsCardsForEachPlayer)
                .sorted(Comparator.comparing(PlayerTotal::getTotal).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public Game shuffleCards(String gameId) {
        List<CardEnum> gameCards = getGameCards(gameId);
        DeckUtils.shuffleCards(gameCards);

        return repository.updateGameCards(gameId, gameCards)
                .orElseThrow(() -> new NotFoundException(buildErrorMessage(
                        AppErrorConstants.ERROR_GAME_NOT_FOUND, NO_PARAMS)));
    }

    @Override
    public Map<String, Long> getCountCardsLeft(String gameId) {

        List<CardEnum> gameCards = getGameCards(gameId);

        return gameCards.stream()
                .collect(Collectors.groupingBy(CardEnum::getSuit, Collectors.counting()));
    }

    @Override
    public TreeMap<CardEnum, Long> getCountRemainingCardsSortedBySuitAndFaceValue(String gameId) {

        Comparator<CardEnum> sortBySuit = Comparator.comparing(CardEnum::getSuit);
        Comparator<CardEnum> sortByValueDesc = Comparator.comparing(CardEnum::getValue).reversed();
        Comparator<CardEnum> sortBySuitAndValueDesc = sortBySuit.thenComparing(sortByValueDesc);

        List<CardEnum> gameCards = getGameCards(gameId);

        return gameCards.stream()
            .collect(Collectors.groupingBy(Function.identity(),
                () -> new TreeMap<>(sortBySuitAndValueDesc),
                    Collectors.counting()));
    }

    @Override
    public List<CardEnum> getPlayerCards(String gameId, String playerName) {
        return repository.findGameOnlyWithPlayer(gameId, playerName)
                .orElseThrow(() -> new NotFoundException(buildErrorMessage(
                        AppErrorConstants.ERROR_PLAYER_NOT_FOUND, NO_PARAMS)))
                .getPlayers().get(0).getOnHandCards();
    }

    @Override
    public Game addPlayer(String gameId, AddPlayerRequest addPlayerRequest) {
        requiredNonEmpty(gameId, GameConstants.GAME_ID);

        String playerName = addPlayerRequest.getPlayerName();
        validateExistentPlayerInGame(gameId, playerName);

        return repository.addNewPlayer(gameId, playerName).get();

    }

    @Override
    public Game removePlayer(String gameId, String playerName) {
        requiredNonEmpty(gameId, GameConstants.GAME_ID);
        requiredNonEmpty(playerName, GameConstants.PLAYER_NAME);

        return repository.removePlayer(gameId, playerName)
                .orElseThrow(() -> new NotFoundException(buildErrorMessage(
                        AppErrorConstants.ERROR_GAME_NOT_FOUND, NO_PARAMS)));
    }

    @Override
    public Game addDeck(String gameId) {
        return repository.addNewDeck(gameId, CardEnum.createDeck())
                .orElseThrow(() -> new NotFoundException(buildErrorMessage(
                        AppErrorConstants.ERROR_GAME_NOT_FOUND, NO_PARAMS)));
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
            .orElseThrow(() -> new NotFoundException(buildErrorMessage(
                AppErrorConstants.ERROR_GAME_NOT_FOUND, NO_PARAMS)));
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

    private void validatePlayer(List<Player> players, String playerName) {
        if (!isExistentPlayer(players, playerName)) {
            throw new NotFoundException(buildErrorMessage(
                    AppErrorConstants.ERROR_PLAYER_NOT_FOUND, NO_PARAMS));
        }
    }

    private void validateExistentPlayerInGame(String gameId, String playerName) {

        boolean isPlayerAlreadyInGame = repository.isPlayerExists(gameId, playerName);

        if (isPlayerAlreadyInGame) {
            throw new GameException(buildErrorMessage(
                    AppErrorConstants.ERROR_USER_ALREADY_EXISTS, new Object[]{ playerName }));
        }
    }

    private boolean isExistentPlayer(List<Player> players, String playerName) {
        return players.stream()
                .anyMatch(p -> p.getName().equalsIgnoreCase(playerName));
    }

    private List<Player> getGamePrayers(String gameId) {
        return repository.findGameOnlyWithPlayers(gameId)
                .orElseThrow(() -> new NotFoundException(buildErrorMessage(
                        AppErrorConstants.ERROR_GAME_NOT_FOUND, NO_PARAMS)))
                .getPlayers();
    }

    private List<CardEnum> getGameCards(String gameId) {
        return repository.findGameOnlyWithCards(gameId)
                .orElseThrow(() -> new NotFoundException(buildErrorMessage(
                        AppErrorConstants.ERROR_GAME_NOT_FOUND, NO_PARAMS)))
                .getGameDeckCards();
    }
}