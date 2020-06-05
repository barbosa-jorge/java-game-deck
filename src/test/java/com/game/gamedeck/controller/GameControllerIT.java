package com.game.gamedeck.controller;

import com.game.gamedeck.model.CardEnum;
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
public class GameControllerIT {

    @Autowired
    private TestRestTemplate restTemplate;

    @MockBean
    private GameRepository gameRepository;

    @Test
    public void createGame_successfully() {

        Optional<Game> mockedGame = getMockedGame();
        when(gameRepository.save(any())).thenReturn(mockedGame);

        GameResponseDTO response = restTemplate.postForEntity("/api/games", getCreateGameRequestDTO(),
                GameResponseDTO.class).getBody();

        assertEquals(getMockedGameResponseDTO(), response);

    }

    private GameResponseDTO getMockedGameResponseDTO() {
        GameResponseDTO response = new GameResponseDTO();
        response.setPlayers(Arrays.asList(new Player("jorge")));
        response.setGameDeckCards(Arrays.asList(CardEnum.ACE_HEARTS, CardEnum.ACE_CLUBS));
        response.setId("5ed98daf2cd10901dc4f8422");
        return response;
    }

    private CreateGameRequestDTO getCreateGameRequestDTO() {
        CreateGameRequestDTO request = new CreateGameRequestDTO();
        request.setNumberOfDecks(1);
        request.setPlayers(Arrays.asList("jorge"));
        return request;
    }

    private Optional<Game> getMockedGame() {
        Game game = new Game();
        game.setId("5ed98daf2cd10901dc4f8422");
        game.setPlayers(Arrays.asList(new Player("jorge")));
        game.setGameDeckCards(Arrays.asList(CardEnum.ACE_HEARTS, CardEnum.ACE_CLUBS));
        return Optional.ofNullable(game);
    }
}
