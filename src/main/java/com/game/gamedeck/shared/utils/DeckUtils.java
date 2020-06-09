package com.game.gamedeck.shared.utils;

import com.game.gamedeck.model.Card;
import org.springframework.util.CollectionUtils;

import java.util.List;

public class DeckUtils {

    public static void shuffleCards(List<Card> cards) {

        if (CollectionUtils.isEmpty(cards)) {
            return;
        }

        for (int i = 0; i < cards.size(); i++) {
            int index = (int) (Math.random() * cards.size());
            Card currentCard = cards.get(i);
            Card movedCard = cards.get(index);
            cards.set(index, currentCard);
            cards.set(i, movedCard);
        }
    }
}
