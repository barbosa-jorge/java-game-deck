package com.game.gamedeck.controller;

import com.game.gamedeck.exceptions.GameException;
import com.game.gamedeck.model.CardEnum;
import com.game.gamedeck.model.Game;
import com.game.gamedeck.model.Player;
import com.game.gamedeck.repositories.GameRepository;
import com.game.gamedeck.requests.AddPlayerRequestDTO;
import com.game.gamedeck.requests.CreateGameRequestDTO;
import com.game.gamedeck.responses.GameResponseDTO;
import com.game.gamedeck.responses.OperationStatus;
import com.game.gamedeck.responses.PlayerTotalResponseDTO;
import com.game.gamedeck.services.impl.GameServiceImpl;
import com.game.gamedeck.shared.constants.RequestOperationName;
import com.game.gamedeck.shared.constants.RequestOperationStatus;
import org.hamcrest.collection.IsMapContaining;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.context.MessageSource;

import java.util.*;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.collection.IsMapWithSize.aMapWithSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.*;

public class GameServiceTest {

    public static final String GAME_ID = "5ed98daf2cd10901dc4f8422";
    public static final String PLAYER_NAME_JORGE = "jorge";
    public static final String PLAYER_NAME_MARIA = "maria";

    @InjectMocks
    private GameServiceImpl gameService;

    @Mock
    private GameRepository gameRepository;

    @Mock
    private ModelMapper mapper;

    @Mock
    private MessageSource messageSource;

    private Game mockedGame;

    private GameResponseDTO expectedGameResDTO;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        List<CardEnum> mockedGameCards = getMockedGameCards(CardEnum.ACE_HEARTS, CardEnum.ACE_CLUBS);
        Player mockedPlayer = getMockedPlayer(PLAYER_NAME_JORGE, CardEnum.TWO_HEARTS);
        this.mockedGame = createMockedGame(mockedGameCards, mockedPlayer);
        this.expectedGameResDTO = getMockedGameResponseDTO(mockedGameCards, mockedPlayer);
    }

    @Test()
    public void givenValidCreateGameRequestDTO_thenCreateGameSuccessfully() {

        // GIVEN
        List<CardEnum> mockedGameCards = getMockedGameCards(CardEnum.ACE_HEARTS, CardEnum.ACE_CLUBS);
        Player mockedPlayer = getMockedPlayer(PLAYER_NAME_JORGE, CardEnum.TWO_HEARTS);
        Game mockedGame = createMockedGame(mockedGameCards, mockedPlayer);
        GameResponseDTO mockedGameResponseDTO = getMockedGameResponseDTO(mockedGameCards, mockedPlayer);
        CreateGameRequestDTO createGameRequestDTO = getCreateGameRequestDTO(1, PLAYER_NAME_JORGE);

        given(gameRepository.save(any())).willReturn(Optional.ofNullable(mockedGame));
        given(mapper.map(any(), any())).willReturn(mockedGameResponseDTO);

        // WHEN
        GameResponseDTO response = gameService.createGame(createGameRequestDTO);

        // THEN
        then(gameRepository).should().save(any());
        assertEquals(mockedGameResponseDTO.getId(), response.getId());
        assertEquals(mockedGameResponseDTO.getGameDeckCards(), response.getGameDeckCards());
        assertEquals(mockedGameResponseDTO.getPlayers(), response.getPlayers());
    }

    @Test()
    public void givenExistentPlayerAndGame_thenDeleteGameSuccessfully() {

        // GIVEN
        given(gameRepository.delete(any())).willReturn(Optional.ofNullable(this.mockedGame));

        // WHEN
        OperationStatus operationStatus = gameService.deleteGame(GAME_ID);

        // THEN
        then(gameRepository).should().delete(any());
        assertEquals(RequestOperationName.DELETE, operationStatus.getOperationName());
        assertEquals(RequestOperationStatus.SUCCESS, operationStatus.getStatus());

    }

    @Test(expected = GameException.class)
    public void givenInvalidGameId_whenDeleteGame_thenThrowsException() {
        // GIVEN
        given(gameRepository.delete(any())).willThrow(GameException.class);
        // WHEN
        gameService.deleteGame(GAME_ID);
        // THEN
        then(gameRepository).should().delete(any());
    }

    @Test(expected = GameException.class)
    public void givenNullGameId_whenDeleteGame_thenThrowsException() {

        // GIVEN
        String gameId = null;
        given(messageSource.getMessage(anyString(), any(), any())).willReturn("error");

        //WHEN
        gameService.deleteGame(gameId);

        //THEN
        then(gameRepository).should(never()).delete(any());
    }

    @Test
    public void whenCallGetAllGames_thenGetAllGamesSuccessfully() {

        // Given
        List<Game> mockedGames = Arrays.asList(Optional.ofNullable(this.mockedGame).get());
        given(gameRepository.findAll()).willReturn(mockedGames);
        given(mapper.map(any(), any())).willReturn(this.expectedGameResDTO);

        // When
        List<GameResponseDTO> gamesResponseDTO = gameService.getAllGames();

        // Then
        then(gameRepository).should().findAll();
        assertThat(gamesResponseDTO, hasSize(1));
        assertThat(gamesResponseDTO, hasItem(this.expectedGameResDTO));
    }

    @Test
    public void givenValidPlayerNameAndGameId_whenCallDealCardsToPlayer_thenDealsCardSuccessfully() {

        // Given
        given(gameRepository.findById(anyString())).willReturn(Optional.ofNullable(this.mockedGame));
        given(gameRepository.save(any())).willReturn(Optional.ofNullable(this.mockedGame));
        given(mapper.map(any(), any())).willReturn(this.expectedGameResDTO);
        ArgumentCaptor<Game> gameArgumentCaptor = ArgumentCaptor.forClass(Game.class);

        // When
        GameResponseDTO gameResponseDTO = gameService.dealCards(GAME_ID, PLAYER_NAME_JORGE);
        List<CardEnum> playerOneOnHandCards = gameResponseDTO.getPlayers().get(0).getOnHandCards();

        // Then
        then(gameRepository).should().save(gameArgumentCaptor.capture());
        assertThat(gameArgumentCaptor.getValue(), is(Optional.ofNullable(this.mockedGame).get()));
        assertThat(playerOneOnHandCards, hasSize(2));

    }

    @Test
    public void givenValidGameId_whenCallGetPlayerTotal_thenReturnsPlayersTotal() {

        // Given
        given(gameRepository.findGameOnlyWithPlayers(anyString())).willReturn(Optional.ofNullable(this.mockedGame));

        // When
        List<PlayerTotalResponseDTO> playersTotals = gameService.getPlayersTotals(GAME_ID);
        PlayerTotalResponseDTO playerOneInfo = playersTotals.get(0);

        // Then
        assertThat(playersTotals, hasSize(1));
        assertThat(playerOneInfo.getPlayer(), is(PLAYER_NAME_JORGE));
        assertThat(playerOneInfo.getTotal(), is(2));

    }

    @Test
    public void givenValidGameId_whenCallShuffleCards_thenShuffleGameCards() {

        // Given
        List<CardEnum> gameDeckCards = Optional.ofNullable(this.mockedGame).get().getGameDeckCards();
        given(gameRepository.findGameOnlyWithCards(anyString())).willReturn(Optional.ofNullable(this.mockedGame));
        given(gameRepository.updateGameCards(anyString(), any())).willReturn(Optional.ofNullable(this.mockedGame));
        given(mapper.map(any(), any())).willReturn(this.expectedGameResDTO);

        // When
        GameResponseDTO gameResponseDTO = gameService.shuffleCards(GAME_ID);
        List<CardEnum> afterShuffleCards = gameResponseDTO.getGameDeckCards();

        // Then
        assertThat(afterShuffleCards, hasSize(gameDeckCards.size()));
        assertThat(afterShuffleCards, hasItems(gameDeckCards.get(0), gameDeckCards.get(1)));

    }

    @Test
    public void givenValidGameId_whenCallGetCountCardsLeft_thenReturnsSuccessfully() {

        // Given
        given(gameRepository.findGameOnlyWithCards(anyString())).willReturn(Optional.ofNullable(this.mockedGame));
        given(gameRepository.updateGameCards(anyString(), any())).willReturn(Optional.ofNullable(this.mockedGame));
        given(mapper.map(any(), any())).willReturn(this.expectedGameResDTO);

        // When
        Map<String, Long> countCardsLeft = gameService.getCountCardsLeft(GAME_ID);

        // Then
        assertThat(countCardsLeft, is(aMapWithSize(2)));
        assertThat(countCardsLeft, hasKey( equalTo("HEARTS")));
        assertThat(countCardsLeft, hasKey( equalTo("CLUBS")));
        assertThat(countCardsLeft, hasValue(equalTo(1L)));
        assertThat(countCardsLeft, hasValue(equalTo(1L)));
        assertThat(countCardsLeft, hasEntry("HEARTS", 1L));
        assertThat(countCardsLeft, hasEntry("CLUBS", 1L));
        assertThat(countCardsLeft, IsMapContaining.hasEntry("HEARTS", 1L));
        assertThat(countCardsLeft, IsMapContaining.hasEntry("CLUBS", 1L));

    }

    @Test
    public void givenValidGameID_whenCallGetCountRemainingCards_thenReturnsSuccessfully() {

        // Given
        given(gameRepository.findGameOnlyWithCards(anyString())).willReturn(Optional.ofNullable(this.mockedGame));

        // When
        TreeMap<CardEnum, Long> countCardsLeft = gameService.getCountRemainingCardsSortedBySuitAndFaceValue(GAME_ID);

        // Then
        assertThat(countCardsLeft, IsMapContaining.hasEntry(CardEnum.ACE_HEARTS, 1L));
        assertThat(countCardsLeft, IsMapContaining.hasEntry(CardEnum.ACE_CLUBS, 1L));

    }

    @Test
    public void givenValidPlayerNameAndGameId_whenCallGetPlayerCards_thenReturnsSuccessfully() {

        // Given
        given(gameRepository.findGameOnlyWithPlayer(anyString(), anyString()))
                .willReturn(Optional.ofNullable(this.mockedGame));

        // When
        List<CardEnum> playerCards = gameService.getPlayerCards(GAME_ID, PLAYER_NAME_JORGE);

        // Then
        assertThat(playerCards, hasSize(1));
        assertThat(playerCards, hasItem(CardEnum.TWO_HEARTS));

    }

    @Test
    public void givenValidGameIdAndAddRequest_whenCallAddPlayer_thenAddPlayerSuccessfully() {

        // Given
        this.mockedGame.getPlayers().add(new Player(PLAYER_NAME_MARIA));
        this.expectedGameResDTO.getPlayers().add(new Player(PLAYER_NAME_MARIA));

        given(gameRepository.isPlayerExists(anyString(), anyString())).willReturn(false);
        given(gameRepository.addNewPlayer(anyString(), anyString())).willReturn(Optional.ofNullable(this.mockedGame));
        given(mapper.map(any(), any())).willReturn(this.expectedGameResDTO);

        AddPlayerRequestDTO requestDTO = new AddPlayerRequestDTO();
        requestDTO.setPlayerName(PLAYER_NAME_MARIA);

        // When
        GameResponseDTO gameResponseDTO = gameService.addPlayer(GAME_ID, requestDTO);

        // Then
        assertThat(gameResponseDTO.getPlayers(), hasItem(new Player(PLAYER_NAME_MARIA)));
        assertThat(gameResponseDTO.getPlayers(), hasSize(2));

    }

    @Test
    public void givenValidGameIdAndPlayer_whenCallRemovePlayer_thenRemoveSuccessfully() {

        // Given
        this.mockedGame.getPlayers().add(new Player(PLAYER_NAME_MARIA));

        given(gameRepository.removePlayer(anyString(), anyString())).willReturn(Optional.ofNullable(this.mockedGame));
        given(mapper.map(any(), any())).willReturn(this.expectedGameResDTO);

        // When
        GameResponseDTO gameResponseDTO = gameService.removePlayer(GAME_ID, PLAYER_NAME_MARIA);

        // Then
        assertThat(gameResponseDTO.getPlayers(), not(hasItem(new Player(PLAYER_NAME_MARIA))));
        assertThat(gameResponseDTO.getPlayers(), hasSize(1));
    }

    @Test
    public void givenValidGameId_whenCallAddDeck_thenAddDeckSuccessfully() {

        // Given
        this.expectedGameResDTO.getGameDeckCards().addAll(CardEnum.createDeck());

        given(gameRepository.addNewDeck(anyString(), any())).willReturn(Optional.ofNullable((this.mockedGame)));
        given(mapper.map(any(), any())).willReturn(this.expectedGameResDTO);

        ArgumentCaptor<List<CardEnum>> cardArgumentCaptor = ArgumentCaptor.forClass(List.class);

        // When
        GameResponseDTO gameResponseDTO = gameService.addDeck(GAME_ID);

        // Then
        then(gameRepository).should().addNewDeck(any(), cardArgumentCaptor.capture());
        assertThat(cardArgumentCaptor.getValue(), hasSize(52));
        assertThat(gameResponseDTO.getGameDeckCards(), hasSize(54));

    }

    private GameResponseDTO getMockedGameResponseDTO(List<CardEnum> cards, Player... players) {
        GameResponseDTO response = new GameResponseDTO();
        response.setId(GAME_ID);
        response.setGameDeckCards(cards);
        response.setPlayers(Arrays.stream(players).collect(Collectors.toList()));
        return response;
    }

    private List<CardEnum> getMockedGameCards(CardEnum... cards) {
        List<CardEnum> mockedCards = new ArrayList<>();
        Arrays.stream(cards).forEach(card -> mockedCards.add(card));
        return mockedCards;
    }

    private Player getMockedPlayer(String playerName, CardEnum... cards) {
        return new Player(playerName, Arrays.stream(cards).collect(Collectors.toList()));
    }

    private Game createMockedGame(List<CardEnum> gameCards, Player... players) {
        Game game = new Game();
        game.setId(GAME_ID);
        game.setPlayers(Arrays.stream(players).collect(Collectors.toList()));
        game.setGameDeckCards(gameCards);
        return game;
    }

    private CreateGameRequestDTO getCreateGameRequestDTO(int numberOfDecks, String... playerNames) {
        CreateGameRequestDTO request = new CreateGameRequestDTO();
        request.setNumberOfDecks(numberOfDecks);
        request.setPlayers(Arrays.stream(playerNames).collect(Collectors.toList()));
        return request;
    }
}
