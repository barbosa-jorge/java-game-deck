package com.game.gamedeck.services.impl;

import com.game.gamedeck.exceptions.GameException;
import com.game.gamedeck.exceptions.NotFoundException;
import com.game.gamedeck.model.CardEnum;
import com.game.gamedeck.model.Game;
import com.game.gamedeck.model.Player;
import com.game.gamedeck.repositories.GameRepository;
import com.game.gamedeck.requests.AddPlayerRequestDTO;
import com.game.gamedeck.requests.CreateGameRequestDTO;
import com.game.gamedeck.responses.GameResponseDTO;
import com.game.gamedeck.responses.OperationStatus;
import com.game.gamedeck.responses.PlayerTotalResponseDTO;
import com.game.gamedeck.services.GameService;
import com.game.gamedeck.shared.constants.AppErrorConstants;
import com.game.gamedeck.shared.constants.GameConstants;
import com.game.gamedeck.shared.constants.RequestOperationName;
import com.game.gamedeck.shared.constants.RequestOperationStatus;
import com.game.gamedeck.shared.utils.DeckUtils;
import org.modelmapper.ModelMapper;
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

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public List<GameResponseDTO> getAllGames() {
        return repository.findAll().stream()
                .map(game -> modelMapper.map(game, GameResponseDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public GameResponseDTO createGame(CreateGameRequestDTO createGameRequestDTO) {
        Game game = new Game();
        addPlayersToGame(createGameRequestDTO.getPlayers(), game);
        addCardsToGameDeck(createGameRequestDTO.getNumberOfDecks(), game);

        return repository.save(game)
                .map(savedGame -> modelMapper.map(savedGame, GameResponseDTO.class))
                .get();
    }

    @Override
    public OperationStatus deleteGame(String gameId) {
        requiredNonEmpty(gameId, GameConstants.GAME_ID);
        repository.delete(gameId)
            .orElseThrow(() -> new NotFoundException(buildErrorMessage(
                AppErrorConstants.ERROR_GAME_NOT_FOUND, NO_PARAMS)));

        return new OperationStatus(RequestOperationStatus.SUCCESS, RequestOperationName.DELETE);
    }

    @Override
    public GameResponseDTO dealCards(String gameId, String playerName) {
        requiredNonEmpty(gameId, GameConstants.GAME_ID);
        requiredNonEmpty(playerName, GameConstants.PLAYER_NAME);

        Game game = findGameById(gameId);
        validatePlayer(game.getPlayers(), playerName);
        pickCardAndAddToPlayer(game, playerName);

        return repository.save(game)
                .map(savedGame -> modelMapper.map(savedGame, GameResponseDTO.class))
                .get();
    }

    @Override
    public List<PlayerTotalResponseDTO> getPlayersTotals(String gameId) {

        List<Player> players = getGamePrayers(gameId);

        return players.stream()
                .map(this::getTotalsCardsForEachPlayer)
                .sorted(Comparator.comparing(PlayerTotalResponseDTO::getTotal).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public GameResponseDTO shuffleCards(String gameId) {
        List<CardEnum> gameCards = getGameCards(gameId);
        DeckUtils.shuffleCards(gameCards);

        Game savedGame = repository.updateGameCards(gameId, gameCards)
                .orElseThrow(() -> new NotFoundException(buildErrorMessage(
                        AppErrorConstants.ERROR_GAME_NOT_FOUND, NO_PARAMS)));

        return modelMapper.map(savedGame, GameResponseDTO.class);
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
    public GameResponseDTO addPlayer(String gameId, AddPlayerRequestDTO addPlayerRequestDTO) {
        requiredNonEmpty(gameId, GameConstants.GAME_ID);

        String playerName = addPlayerRequestDTO.getPlayerName();
        validateExistentPlayerInGame(gameId, playerName);

        return repository.addNewPlayer(gameId, playerName)
                .map(updatedGame -> modelMapper.map(updatedGame, GameResponseDTO.class))
                .get();
    }

    @Override
    public GameResponseDTO removePlayer(String gameId, String playerName) {
        requiredNonEmpty(gameId, GameConstants.GAME_ID);
        requiredNonEmpty(playerName, GameConstants.PLAYER_NAME);

        Game updatedGame = repository.removePlayer(gameId, playerName)
                .orElseThrow(() -> new NotFoundException(buildErrorMessage(
                        AppErrorConstants.ERROR_GAME_NOT_FOUND, NO_PARAMS)));

        return modelMapper.map(updatedGame, GameResponseDTO.class);
    }

    @Override
    public GameResponseDTO addDeck(String gameId) {
        Game updatedGame = repository.addNewDeck(gameId, CardEnum.createDeck())
                .orElseThrow(() -> new NotFoundException(buildErrorMessage(
                        AppErrorConstants.ERROR_GAME_NOT_FOUND, NO_PARAMS)));

        return modelMapper.map(updatedGame, GameResponseDTO.class);
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

    private PlayerTotalResponseDTO createPlayerTotal(String playerName, int total) {
        PlayerTotalResponseDTO playerTotalResponseDTO = new PlayerTotalResponseDTO();
        playerTotalResponseDTO.setPlayer(playerName);
        playerTotalResponseDTO.setTotal(total);
        return playerTotalResponseDTO;
    }

    private PlayerTotalResponseDTO getTotalsCardsForEachPlayer(Player player) {
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