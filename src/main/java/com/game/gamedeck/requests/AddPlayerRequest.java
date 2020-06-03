package com.game.gamedeck.requests;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Data
public class AddPlayerRequest {
    @Size(min = 3, message="{error.player.name.min.size}")
    @NotEmpty(message = "{error.player.name.not.blank}")
    private String playerName;
}
