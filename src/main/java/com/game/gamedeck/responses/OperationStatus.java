package com.game.gamedeck.responses;

import com.game.gamedeck.shared.constants.RequestOperationName;
import com.game.gamedeck.shared.constants.RequestOperationStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OperationStatus {
    private RequestOperationStatus status;
    private RequestOperationName operationName;
}
