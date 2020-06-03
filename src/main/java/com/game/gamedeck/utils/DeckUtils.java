package com.game.gamedeck.utils;

import com.game.gamedeck.model.CardEnum;
import org.springframework.util.CollectionUtils;

import java.util.List;

public class DeckUtils {

    public static void shuffleCards(List<CardEnum> cards) {

        if (CollectionUtils.isEmpty(cards)) {
            return;
        }

        for (int i = 0; i < cards.size(); i++) {
            int index = (int) (Math.random() * cards.size());
            CardEnum currentCard = cards.get(i);
            CardEnum movedCard = cards.get(index);
            cards.set(index, currentCard);
            cards.set(i, movedCard);
        }
    }
}
