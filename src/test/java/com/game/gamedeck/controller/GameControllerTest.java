package com.game.gamedeck.controller;

import com.game.gamedeck.model.CardEnum;
import com.game.gamedeck.model.Player;
import com.game.gamedeck.responses.GameResponseDTO;
import com.game.gamedeck.services.GameService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class GameControllerTest {

    @InjectMocks
    private GameController gameController;

    @Mock
    private GameService gameService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test()
    void createGame_successfully() {

        GameResponseDTO gameResponseDTO = createGameResponseDTO();

        when(gameService.createGame(any())).thenReturn(gameResponseDTO);
        GameResponseDTO createdGameResponse = gameController.createGame(any()).getBody();

        verify(gameService, times(1)).createGame(any());
        assertEquals(createdGameResponse.getId(), gameResponseDTO.getId());
        assertEquals(createdGameResponse.getGameDeckCards(), gameResponseDTO.getGameDeckCards());
        assertEquals(createdGameResponse.getPlayers(), gameResponseDTO.getPlayers());

    }

    @Test()
    void getAllGames_successfully() {

        List<GameResponseDTO> gameResponseDTOs = Arrays.asList(createGameResponseDTO());

        when(gameService.getAllGames()).thenReturn(gameResponseDTOs);
        List<GameResponseDTO> games = gameController.findAllGames().getBody();

        verify(gameService, times(1)).getAllGames();
        assertEquals(games.get(0).getId(), gameResponseDTOs.get(0).getId());
        assertEquals(games.get(0).getGameDeckCards(), gameResponseDTOs.get(0).getGameDeckCards());
        assertEquals(games.get(0).getPlayers(), gameResponseDTOs.get(0).getPlayers());

    }

    private GameResponseDTO createGameResponseDTO() {
        GameResponseDTO response = new GameResponseDTO();
        response.setId("id");
        response.setGameDeckCards(Arrays.asList(CardEnum.ACE_HEARTS));
        response.setPlayers(Arrays.asList(new Player("jorge")));
        return response;
    }
}
