package com.game.gamedeck.controller;

import com.game.gamedeck.model.CardEnum;
import com.game.gamedeck.model.Game;
import com.game.gamedeck.model.Player;
import com.game.gamedeck.repositories.GameRepository;
import com.game.gamedeck.requests.CreateGameRequestDTO;
import com.game.gamedeck.responses.GameResponseDTO;
import com.game.gamedeck.services.impl.GameServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

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
    void createGame_successfully() {

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
//
//    @Test()
//    void getAllGames_successfully() {
//
//        List<GameResponseDTO> gameResponseDTOs = Arrays.asList(createGameResponseDTO());
//
//        when(gameService.getAllGames()).thenReturn(gameResponseDTOs);
//        List<GameResponseDTO> games = gameController.findAllGames().getBody();
//
//        verify(gameService, times(1)).getAllGames();
//        assertEquals(games.get(0).getId(), gameResponseDTOs.get(0).getId());
//        assertEquals(games.get(0).getGameDeckCards(), gameResponseDTOs.get(0).getGameDeckCards());
//        assertEquals(games.get(0).getPlayers(), gameResponseDTOs.get(0).getPlayers());
//
//    }

    private GameResponseDTO getGameResponseDTO() {
        GameResponseDTO response = new GameResponseDTO();
        response.setId("id");
        response.setGameDeckCards(Arrays.asList(CardEnum.ACE_HEARTS));
        response.setPlayers(Arrays.asList(new Player("jorge")));
        return response;
    }

    private Optional<Game> getMockedGame() {
        Game game = new Game();
        game.setId("5ed98daf2cd10901dc4f8422");
        game.setPlayers(Arrays.asList(new Player("jorge")));
        game.setGameDeckCards(Arrays.asList(CardEnum.ACE_HEARTS, CardEnum.ACE_CLUBS));
        return Optional.ofNullable(game);
    }

    private CreateGameRequestDTO getCreateGameRequestDTO() {
        CreateGameRequestDTO request = new CreateGameRequestDTO();
        request.setNumberOfDecks(1);
        request.setPlayers(Arrays.asList("jorge"));
        return request;
    }
}
