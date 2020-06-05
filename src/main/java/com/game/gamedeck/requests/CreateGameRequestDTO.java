package com.game.gamedeck.requests;

import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Data
public class CreateGameRequestDTO {
    @NotEmpty(message = "{error.add.player.to.game}")
    private List<@Valid @Size(min = 3, message="{error.player.name.min.size}") String> players =
            new ArrayList<>();

    @Min(1)
    @NotNull(message = "{error.add.deck.to.game}")
    private Integer numberOfDecks;
}
