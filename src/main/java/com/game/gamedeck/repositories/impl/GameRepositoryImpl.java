package com.game.gamedeck.repositories.impl;

import com.game.gamedeck.model.Game;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

@Repository
public class GameRepositoryImpl {

    public static final String GAME = "game";
    public static final String ID = "id";

    @Autowired
    private MongoTemplate mongoTemplate;

    public Game save(Game game) {
        return mongoTemplate.save(game, GAME);
    }

    public void remove(Game game) {
        mongoTemplate.remove(game, GAME);
    }

    public Game findById(String gameId) {
        return mongoTemplate.findOne(
                Query.query(Criteria.where(ID).is(gameId)), Game.class);
    }
}