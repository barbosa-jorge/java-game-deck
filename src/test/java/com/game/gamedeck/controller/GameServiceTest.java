package com.game.gamedeck.controller;

import com.game.gamedeck.model.CardEnum;
import com.game.gamedeck.model.Game;
import com.game.gamedeck.model.Player;
import com.game.gamedeck.repositories.GameRepository;
import com.game.gamedeck.requests.CreateGameRequestDTO;
import com.game.gamedeck.responses.GameResponseDTO;
import com.game.gamedeck.responses.OperationStatus;
import com.game.gamedeck.services.impl.GameServiceImpl;
import com.game.gamedeck.shared.constants.RequestOperationName;
import com.game.gamedeck.shared.constants.RequestOperationStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.*;

public class GameServiceTest {

    @InjectMocks
    private GameServiceImpl gameService;

    @Mock
    private GameRepository gameRepository;

    @Mock
    private ModelMapper mapper;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test()
    public void createGame_successfully() {

        Optional<Game> mockedGame = getMockedGame();
        GameResponseDTO expectedGameResponseDTO = getGameResponseDTO();

        when(gameRepository.save(any())).thenReturn(mockedGame);
        when(mapper.map(any(), any())).thenReturn(expectedGameResponseDTO);

        GameResponseDTO response = gameService.createGame(getCreateGameRequestDTO());

        verify(gameRepository, times(1)).save(any());
        assertEquals(expectedGameResponseDTO.getId(), response.getId());
        assertEquals(expectedGameResponseDTO.getGameDeckCards(), response.getGameDeckCards());
        assertEquals(expectedGameResponseDTO.getPlayers(), response.getPlayers());

    }

    @Test()
    public void deleteGame_successfully() {

        when(gameRepository.delete(any())).thenReturn(getMockedGame());

        OperationStatus operationStatus = gameService.deleteGame("5ed98daf2cd10901dc4f8422");

        verify(gameRepository, times(1)).delete(any());
        assertEquals(RequestOperationName.DELETE, operationStatus.getOperationName());
        assertEquals(RequestOperationStatus.SUCCESS, operationStatus.getStatus());

    }

    @Test
    public void getAllGames_successfully_BDDStyle() {

        // Given
        List<Game> mockedGames = Arrays.asList(getMockedGame().get());
        given(gameRepository.findAll()).willReturn(mockedGames);
        given(mapper.map(any(), any())).willReturn(getGameResponseDTO());

        // When
        List<GameResponseDTO> gamesResponseDTO = gameService.getAllGames();

        // Then
        then(gameRepository).should().findAll();
        assertThat(gamesResponseDTO, hasSize(1));
        assertThat(gamesResponseDTO, hasItem(getGameResponseDTO()));
    }

    @Test
    public void dealCardsToPlayer_successfully_BDD() {

        // Given
        given(gameRepository.findById(anyString())).willReturn(getMockedGame());
        given(gameRepository.save(any())).willReturn(getMockedGame());
        given(mapper.map(any(), any())).willReturn(getGameResponseDTO());

        // When
        GameResponseDTO gameResponseDTO = gameService.dealCards("5ed98daf2cd10901dc4f8422", "jorge");

        // Then
        then(gameRepository).should().save(any());
        assertThat(gameResponseDTO.getPlayers().get(0).getOnHandCards(), hasSize(1));
        assertThat(gameResponseDTO.getPlayers().get(0).getOnHandCards(), hasItem(CardEnum.TWO_HEARTS));

    }

    private GameResponseDTO getGameResponseDTO() {
        GameResponseDTO response = new GameResponseDTO();
        response.setId("5ed98daf2cd10901dc4f8422");
        response.setGameDeckCards(Arrays.asList(CardEnum.ACE_HEARTS));

        List<CardEnum> cards = new ArrayList<>();
        cards.add(CardEnum.TWO_HEARTS);

        response.setPlayers(Arrays.asList(new Player("jorge", cards)));
        return response;
    }

    private Optional<Game> getMockedGame() {
        Game game = new Game();
        game.setId("5ed98daf2cd10901dc4f8422");

        List<CardEnum> cards = new ArrayList<>();
        cards.add(CardEnum.TWO_HEARTS);

        game.setPlayers(Arrays.asList(new Player("jorge", cards)));

        List<CardEnum> gameDeckCards = new ArrayList<>();
        gameDeckCards.add(CardEnum.ACE_HEARTS);
        gameDeckCards.add(CardEnum.ACE_CLUBS);

        game.setGameDeckCards(gameDeckCards);
        return Optional.ofNullable(game);
    }

    private CreateGameRequestDTO getCreateGameRequestDTO() {
        CreateGameRequestDTO request = new CreateGameRequestDTO();
        request.setNumberOfDecks(1);
        request.setPlayers(Arrays.asList("jorge"));
        return request;
    }
}
