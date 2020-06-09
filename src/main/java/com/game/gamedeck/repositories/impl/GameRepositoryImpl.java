package com.game.gamedeck.repositories.impl;

import com.game.gamedeck.model.*;
import com.game.gamedeck.repositories.GameRepository;
import com.mongodb.BasicDBObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

@Repository
public class GameRepositoryImpl implements GameRepository {

    public static final String GAME = "game";
    public static final String ID = "id";
    private static final FindAndModifyOptions FIND_AND_MODIFY_OPTIONS_RETURN_TRUE = FindAndModifyOptions
            .options().returnNew(true);

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public Optional<Game> save(Game game) {
        return Optional.ofNullable(mongoTemplate.save(game, GAME));
    }

    @Override
    public Optional<Game> delete(String gameId) {
        Query query = Query.query(Criteria.where("_id").is(gameId));
        Game removedGame = mongoTemplate.findAndRemove(query, Game.class);
        return Optional.ofNullable(removedGame);
    }

    @Override
    public Optional<Game> findById(String gameId) {
        Game game = mongoTemplate
                .findOne(Query.query(Criteria.where(ID).is(gameId)), Game.class);
        return Optional.ofNullable(game);
    }

    @Override
    public List<Game> findAll() {
        return mongoTemplate.findAll(Game.class);
    }

    @Override
    public Optional<Game> findGameOnlyWithPlayer(String gameId, String playerName) {
        Criteria filterById = Criteria.where("_id").is(gameId);
        Criteria filterByPlayersName = Criteria.where("players.name").is(playerName);

        Query query = Query.query(filterById.andOperator(filterByPlayersName));
        query.fields().include("players.$").exclude("_id");

        Game game = mongoTemplate.findOne(query, Game.class);
        return Optional.ofNullable(game);
    }

    @Override
    public Optional<Game> findGameOnlyWithCards(String gameId) {
        Query query = Query.query(Criteria.where("_id").is(gameId));
        query.fields().include("gameCards").exclude("_id");

        Game game = mongoTemplate.findOne(query, Game.class);
        return Optional.ofNullable(game);
    }

    @Override
    public Optional<Game> findGameOnlyWithPlayers(String gameId) {
        Query query = Query.query(Criteria.where("_id").is(gameId));
        query.fields().include("players").exclude("_id");

        Game game = mongoTemplate.findOne(query, Game.class);
        return Optional.ofNullable(game);
    }

    @Override
    public boolean isPlayerExists(String gameId, String playerName) {
        Query query = Query.query(Criteria.where("_id").is(gameId).and("players.name").is(playerName));
        return mongoTemplate.exists(query, Game.class);
    }

    public Optional<Game> updateGameCards(String gameId, List<Card> cards) {
        Query query = Query.query(Criteria.where("_id").is(gameId));
        Update update = Update.update("gameCards", cards);

        Game updatedGame = mongoTemplate.findAndModify(query, update,
                FIND_AND_MODIFY_OPTIONS_RETURN_TRUE, Game.class);
        return Optional.ofNullable(updatedGame);
    }

    public Optional<Game> addNewPlayer(String gameId, String playerName) {
        Query query = Query.query(Criteria.where("_id").is(gameId));
        Update update = new Update().push("players", new Player(playerName));

        Game updatedGame =  mongoTemplate.findAndModify(query, update,
                FIND_AND_MODIFY_OPTIONS_RETURN_TRUE, Game.class);

        return Optional.ofNullable(updatedGame);
    }

    public Optional<Game> removePlayer(String gameId, String playerName) {
        Query query = Query.query(Criteria.where("_id").is(gameId).and("players.name").is(playerName));
        Update update = new Update().pull("players", new BasicDBObject("name", playerName));

        Game updatedGame = mongoTemplate.findAndModify(query, update,
                FIND_AND_MODIFY_OPTIONS_RETURN_TRUE, Game.class);
        return Optional.ofNullable(updatedGame);
    }

    @Override
    public Optional<Game> addNewDeck(String gameId, List<Card> cards) {
        Query query = Query.query(Criteria.where("_id").is(gameId));
        Update update = new Update().push("gameCards").each(cards);

        Game updatedGame = mongoTemplate.findAndModify(query, update,
                FIND_AND_MODIFY_OPTIONS_RETURN_TRUE, Game.class);
        return Optional.ofNullable(updatedGame);
    }

    public List<CardsBySuit> countRemainingCardsBySuit(String gameId) {
        Aggregation agg = newAggregation(
                match(Criteria.where("_id").is(gameId)),
                unwind("gameCards"),
                group("gameCards.suit").count().as("total"),
                project("total").and("suit").previousOperation()
        );

        AggregationResults<CardsBySuit> groupResults = mongoTemplate
                .aggregate(agg, Game.class, CardsBySuit.class);
        return groupResults.getMappedResults();
    }

    public List<CardsBySuitAndValue> countRemainingCardsSorted(String gameId, Sort sort) {

        Aggregation agg = newAggregation(
                match(Criteria.where("_id").is(gameId)),
                unwind("gameCards"),
                group("gameCards.suit","gameCards.value", "gameCards.faceValue").count().as("total"),
                project("total", "suit", "value", "faceValue").and("card").previousOperation(),
                sort(sort)
        );

        AggregationResults<CardsBySuitAndValue> groupResults = mongoTemplate
                .aggregate(agg, Game.class, CardsBySuitAndValue.class);

        return groupResults.getMappedResults();
    }
}