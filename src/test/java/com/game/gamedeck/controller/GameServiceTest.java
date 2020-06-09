package com.game.gamedeck.controller;

import com.game.gamedeck.exceptions.GameException;
import com.game.gamedeck.model.Card;
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

    private static final Card CARD_ACE_CLUBS = new Card(1,"CLUBS","A");
    private static final Card CARD_ACE_HEARTS = new Card(1,"HEARTS","A");
    private static final Card CARD_TWO_HEARTS = new Card(2, "HEARTS", "2");
    public static final String PLAYER_NAME_JORGE = "jorge";
    public static final String PLAYER_NAME_MARIA = "maria";
    private static final Player PLAYER_JORGE = new Player(PLAYER_NAME_JORGE);
    private static final String URI_API_GAMES = "/api/games";
    public static final String GAME_ID = "5ed98daf2cd10901dc4f8422";
    public static final int NUMBER_OF_DECKS = 1;
    public static final String SUIT_HEARTS = "HEARTS";
    public static final String SUIT_CLUBS = "CLUBS";

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
        List<Card> mockedGameCards = getMockedGameCards(CARD_ACE_HEARTS, CARD_ACE_CLUBS);
        Player mockedPlayer = getMockedPlayer(PLAYER_NAME_JORGE, CARD_TWO_HEARTS);
        this.mockedGame = createMockedGame(mockedGameCards, mockedPlayer);
        this.expectedGameResDTO = getMockedGameResponseDTO(mockedGameCards, mockedPlayer);
    }

    @Test()
    public void givenValidCreateGameRequestDTO_thenCreateGameSuccessfully() {

        // GIVEN
        List<Card> mockedGameCards = getMockedGameCards(CARD_ACE_HEARTS, CARD_ACE_CLUBS);
        Player mockedPlayer = getMockedPlayer(PLAYER_NAME_JORGE, CARD_TWO_HEARTS);
        Game mockedGame = createMockedGame(mockedGameCards, mockedPlayer);
        GameResponseDTO mockedGameResponseDTO = getMockedGameResponseDTO(mockedGameCards, mockedPlayer);
        CreateGameRequestDTO createGameRequestDTO = getCreateGameRequestDTO(NUMBER_OF_DECKS, PLAYER_NAME_JORGE);

        given(gameRepository.save(any())).willReturn(Optional.ofNullable(mockedGame));
        given(mapper.map(any(), any())).willReturn(mockedGameResponseDTO);

        // WHEN
        GameResponseDTO response = gameService.createGame(createGameRequestDTO);

        // THEN
        then(gameRepository).should().save(any());
        assertEquals(mockedGameResponseDTO.getId(), response.getId());
        assertEquals(mockedGameResponseDTO.getGameCards(), response.getGameCards());
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
        List<Card> playerOneOnHandCards = gameResponseDTO.getPlayers().get(0).getOnHandCards();

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
        List<Card> gameDeckCards = Optional.ofNullable(this.mockedGame).get().getGameCards();
        given(gameRepository.findGameOnlyWithCards(anyString())).willReturn(Optional.ofNullable(this.mockedGame));
        given(gameRepository.updateGameCards(anyString(), any())).willReturn(Optional.ofNullable(this.mockedGame));
        given(mapper.map(any(), any())).willReturn(this.expectedGameResDTO);

        // When
        GameResponseDTO gameResponseDTO = gameService.shuffleCards(GAME_ID);
        List<Card> afterShuffleCards = gameResponseDTO.getGameCards();

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
        Map<String, Long> countCardsLeft = gameService.getCardsLeftBySuitUsingCollectors(GAME_ID);

        // Then
        assertThat(countCardsLeft, is(aMapWithSize(2)));
        assertThat(countCardsLeft, hasKey( equalTo(SUIT_HEARTS)));
        assertThat(countCardsLeft, hasKey( equalTo(SUIT_CLUBS)));
        assertThat(countCardsLeft, hasValue(equalTo(1L)));
        assertThat(countCardsLeft, hasValue(equalTo(1L)));
        assertThat(countCardsLeft, hasEntry(SUIT_HEARTS, 1L));
        assertThat(countCardsLeft, hasEntry(SUIT_CLUBS, 1L));
        assertThat(countCardsLeft, IsMapContaining.hasEntry(SUIT_HEARTS, 1L));
        assertThat(countCardsLeft, IsMapContaining.hasEntry(SUIT_CLUBS, 1L));

    }

    @Test
    public void givenValidGameID_whenCallGetCountRemainingCards_thenReturnsSuccessfully() {

        // Given
        given(gameRepository.findGameOnlyWithCards(anyString())).willReturn(Optional.ofNullable(this.mockedGame));

        // When
        TreeMap<Card, Long> countCardsLeft = gameService.getCountRemainingCardsSortedUsingCollectors(GAME_ID);

        // Then
        assertThat(countCardsLeft, IsMapContaining.hasEntry(CARD_ACE_HEARTS, 1L) );
        assertThat(countCardsLeft, IsMapContaining.hasEntry(CARD_ACE_CLUBS, 1L));

    }

    @Test
    public void givenValidPlayerNameAndGameId_whenCallGetPlayerCards_thenReturnsSuccessfully() {

        // Given
        given(gameRepository.findGameOnlyWithPlayer(anyString(), anyString()))
                .willReturn(Optional.ofNullable(this.mockedGame));

        // When
        List<Card> playerCards = gameService.getPlayerCards(GAME_ID, PLAYER_NAME_JORGE);

        // Then
        assertThat(playerCards, hasSize(1));
        assertThat(playerCards, hasItem(CARD_TWO_HEARTS));

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
        this.expectedGameResDTO.getGameCards().addAll(CardEnum.createDeck());

        given(gameRepository.addNewDeck(anyString(), any())).willReturn(Optional.ofNullable((this.mockedGame)));
        given(mapper.map(any(), any())).willReturn(this.expectedGameResDTO);

        ArgumentCaptor<List<Card>> cardArgumentCaptor = ArgumentCaptor.forClass(List.class);

        // When
        GameResponseDTO gameResponseDTO = gameService.addDeck(GAME_ID);

        // Then
        then(gameRepository).should().addNewDeck(any(), cardArgumentCaptor.capture());
        assertThat(cardArgumentCaptor.getValue(), hasSize(52));
        assertThat(gameResponseDTO.getGameCards(), hasSize(54));

    }

    private GameResponseDTO getMockedGameResponseDTO(List<Card> cards, Player... players) {
        GameResponseDTO response = new GameResponseDTO();
        response.setId(GAME_ID);
        response.setGameCards(cards);
        response.setPlayers(Arrays.stream(players).collect(Collectors.toList()));
        return response;
    }

    private List<Card> getMockedGameCards(Card... cards) {
        List<Card> mockedCards = new ArrayList<>();
        Arrays.stream(cards).forEach(card -> mockedCards.add(card));
        return mockedCards;
    }

    private Player getMockedPlayer(String playerName, Card... cards) {
        return new Player(playerName, Arrays.stream(cards).collect(Collectors.toList()));
    }

    private Game createMockedGame(List<Card> gameCards, Player... players) {
        Game game = new Game();
        game.setId(GAME_ID);
        game.setPlayers(Arrays.stream(players).collect(Collectors.toList()));
        game.setGameCards(gameCards);
        return game;
    }

    private CreateGameRequestDTO getCreateGameRequestDTO(int numberOfDecks, String... playerNames) {
        CreateGameRequestDTO request = new CreateGameRequestDTO();
        request.setNumberOfDecks(numberOfDecks);
        request.setPlayers(Arrays.stream(playerNames).collect(Collectors.toList()));
        return request;
    }
}
