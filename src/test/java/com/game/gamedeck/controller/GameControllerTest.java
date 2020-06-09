package com.game.gamedeck.controller;

import com.game.gamedeck.model.Card;
import com.game.gamedeck.model.Player;
import com.game.gamedeck.requests.CreateGameRequestDTO;
import com.game.gamedeck.responses.GameResponseDTO;
import com.game.gamedeck.services.GameService;
import com.google.gson.Gson;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Arrays;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(GameController.class)
public class GameControllerTest {

    private static final Card CARD_ACE_CLUBS = new Card(1,"CLUBS","A");
    private static final Card CARD_ACE_HEARTS = new Card(1,"HEARTS","A");
    private static final String PLAYER_NAME = "jorge";
    private static final Player PLAYER_JORGE = new Player(PLAYER_NAME);
    private static final String GAME_ID = "5ed98daf2cd10901dc4f8422";
    private static final String URI_API_GAMES = "/api/games";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GameService gameService;

    @Test
    public void createGame_successfully() throws Exception {

        GameResponseDTO responseDTO = getMockedGameResponseDTO();
        when(gameService.createGame(any())).thenReturn(responseDTO);

        RequestBuilder request = MockMvcRequestBuilders
                .post(URI_API_GAMES)
                .content(new Gson().toJson(getCreateGameRequestDTO()))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isCreated())
                .andExpect(content().json(getExpectedMockedGameResponse()))
                .andReturn();

    }

    private String getExpectedMockedGameResponse() {
        return "{ id: 5ed98daf2cd10901dc4f8422, " +
                "gameCards: [" +
                    "{value: 1, suit: HEARTS, faceValue: A}, " +
                    "{value: 1, suit: CLUBS, faceValue: A}], " +
                "players : [{ name: jorge , onHandCards: []}]}";
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
}
