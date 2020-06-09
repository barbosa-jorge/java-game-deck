package com.game.gamedeck.controller;

import com.game.gamedeck.model.Card;
import com.game.gamedeck.model.Game;
import com.game.gamedeck.model.Player;
import com.game.gamedeck.repositories.GameRepository;
import com.game.gamedeck.requests.CreateGameRequestDTO;
import com.game.gamedeck.responses.GameResponseDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class GameControllerV1IT {

    private static final Card CARD_ACE_CLUBS = new Card(1,"CLUBS","A");
    private static final Card CARD_ACE_HEARTS = new Card(1,"HEARTS","A");
    private static final String PLAYER_NAME = "jorge";
    private static final Player PLAYER_JORGE = new Player(PLAYER_NAME);
    private static final String GAME_ID = "5ed98daf2cd10901dc4f8422";
    private static final String URI_API_GAMES = "/game-management/api/v1/games";

    @Autowired
    private TestRestTemplate restTemplate;

    @MockBean
    private GameRepository gameRepository;

    @Test
    public void createGame_successfully() {

        Optional<Game> mockedGame = getMockedGame();
        when(gameRepository.save(any())).thenReturn(mockedGame);

        GameResponseDTO response = restTemplate.postForEntity(URI_API_GAMES, getCreateGameRequestDTO(),
                GameResponseDTO.class).getBody();

        assertEquals(getMockedGameResponseDTO(), response);

    }

    private GameResponseDTO getMockedGameResponseDTO() {
        GameResponseDTO response = new GameResponseDTO();
        response.setPlayers(Arrays.asList(PLAYER_JORGE));
        response.setGameCards(Arrays.asList(CARD_ACE_HEARTS, CARD_ACE_CLUBS));
        response.setId(GAME_ID);
        return response;
    }

    private CreateGameRequestDTO getCreateGameRequestDTO() {
        CreateGameRequestDTO request = new CreateGameRequestDTO();
        request.setNumberOfDecks(1);
        request.setPlayers(Arrays.asList(PLAYER_NAME));
        return request;
    }

    private Optional<Game> getMockedGame() {
        Game game = new Game();
        game.setId(GAME_ID);
        game.setPlayers(Arrays.asList(PLAYER_JORGE));
        game.setGameCards(Arrays.asList(CARD_ACE_HEARTS, CARD_ACE_CLUBS));
        return Optional.ofNullable(game);
    }
}