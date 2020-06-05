package com.game.gamedeck.controller;

import com.game.gamedeck.model.CardEnum;
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

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GameService gameService;

    @Test
    public void createGame_successfully() throws Exception {

        GameResponseDTO responseDTO = getMockedGameResponseDTO();
        when(gameService.createGame(any())).thenReturn(responseDTO);

        RequestBuilder request = MockMvcRequestBuilders
                .post("/api/games")
                .content(new Gson().toJson(getCreateGameRequestDTO()))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isCreated())
                .andExpect(content().json(getExpectedMockedGameResponse()))
                .andReturn();

    }

    private String getExpectedMockedGameResponse() {
        return "{ id: 5ed98daf2cd10901dc4f8422, gameDeckCards: [ACE_HEARTS, ACE_CLUBS], players : [ { name: jorge , onHandCards: [] } ] }";
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
}
