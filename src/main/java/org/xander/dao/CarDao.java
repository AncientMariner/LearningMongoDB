package org.xander.dao;

import com.mongodb.BulkWriteResult;
import com.mongodb.CommandResult;
import com.mongodb.DBCollection;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.ScriptOperations;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.convert.MongoConverter;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import org.xander.model.Car;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.project;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.sort;

@Repository
public class CarDao {
    @Autowired
    private MongoOperations mongoOps;

    public void save(Car car) {
        mongoOps.save(car);
    }

    public Car get(String id) {
        return mongoOps.findOne(Query.query(Criteria.where("id").is(id)), Car.class);
    }

    public List<Car> getAll() {
        return mongoOps.findAll(Car.class);
    }

    public void remove(String id) {
        mongoOps.remove(Query.query(Criteria.where("id").is(id)), Car.class);
    }

    public boolean collectionPresent(String collectionName) {
        return mongoOps.collectionExists(collectionName);
    }

    public AggregationResults<ArrayList> aggregate() {
        Aggregation aggregation = newAggregation(
                match(Criteria.where("price").lt(60000)),
//                group("price").count().as("total"),
                project("price").andExclude("_id"),
                sort(Sort.Direction.DESC, "price"));

        AggregationResults<ArrayList> aggregate = mongoOps.aggregate(aggregation, Car.class, ArrayList.class);
        return aggregate;
    }

    public BulkWriteResult bulkOperation() {
        Document newDocument1 = new Document("name", "Mercedes")
                .append("price", 150000);

        Document newDocument2 = new Document("name", "Mercedes AMG")
                .append("price", 250000);

        BulkWriteResult bulkWriteResult = mongoOps
                .bulkOps(BulkOperations.BulkMode.ORDERED, "cars")
                .insert(newDocument1)
                .insert(newDocument2)
                .execute();
        return bulkWriteResult;
    }

    public long count() {
        Query query = Query.query(Criteria.where("name").is("Volvo"));
        return mongoOps.count(query, Car.class);
    }

    public CommandResult executeCommand() {
        return mongoOps.executeCommand("{ " + "\"count\" : \"" + "cars" + "\"" + " }");
    }

    public void executeQuery() {
//    mongoOps.executeQuery();
    }

    public void exists() {
//        mongoOps.exists()
    }

    public void find() {
//        mongoOps.find();
    }

    public void geoNear() {
//        mongoOps.geoNear();
    }

    public void getCollection() {
        String collectionName = mongoOps.getCollectionName(Car.class);
        DBCollection cars = mongoOps.getCollection(collectionName);
        Set<String> collectionNames = mongoOps.getCollectionNames();
    }

    public MongoConverter getMongoConverter() {
        return mongoOps.getConverter();
    }

    public void scriptOperations() {
        ScriptOperations scriptOperations = mongoOps.scriptOps();
        System.out.println(scriptOperations);
    }

    public void stream() {
//        mongoOps.stream();
    }

    public void mapReduce() {
//        mongoOps.mapReduce();
    }
}
