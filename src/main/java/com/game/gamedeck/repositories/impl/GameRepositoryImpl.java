package com.game.gamedeck.repositories.impl;

import com.game.gamedeck.model.Game;
import com.game.gamedeck.repositories.GameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class GameRepositoryImpl implements GameRepository {

    public static final String GAME = "game";
    public static final String ID = "id";

    @Autowired
    private MongoTemplate mongoTemplate;

    public Game save(Game game) {
        return mongoTemplate.save(game, GAME);
    }

    public void delete(Game game) {
        mongoTemplate.remove(game, GAME);
    }

    public Optional<Game> findById(String gameId) {
        Game game = mongoTemplate
                .findOne(Query.query(Criteria.where(ID).is(gameId)), Game.class);
        return Optional.ofNullable(game);
    }

    public List<Game> findAll() {
        return mongoTemplate.findAll(Game.class);
    }
}