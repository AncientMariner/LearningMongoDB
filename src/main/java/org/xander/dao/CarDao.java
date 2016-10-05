package org.xander.dao;

import com.mongodb.BulkWriteResult;
import com.mongodb.CommandResult;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MapReduceCommand;
import com.mongodb.MapReduceOutput;
import com.mongodb.MongoClient;
import com.mongodb.MongoException;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.DocumentCallbackHandler;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.ScriptOperations;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.convert.DefaultDbRefResolver;
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.script.ExecutableMongoScript;
import org.springframework.data.mongodb.core.script.NamedMongoScript;
import org.springframework.data.util.CloseableIterator;
import org.springframework.stereotype.Repository;
import org.xander.model.Car;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.project;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.sort;
import static org.springframework.data.mongodb.core.query.Criteria.where;

@Repository
public class CarDao {
    @Autowired private MongoOperations mongoOps;

    public void save(Car car) {
        mongoOps.save(car);
    }

    public Car get(String id) {
        return mongoOps.findOne(Query.query(where("id").is(id)), Car.class);
    }

    public List<Car> getAll() {
        return mongoOps.findAll(Car.class);
    }

    public void remove(String id) {
        mongoOps.remove(Query.query(where("id").is(id)), Car.class);
    }

    public boolean collectionPresent(String collectionName) {
        return mongoOps.collectionExists(collectionName);
    }

    public AggregationResults<ArrayList> aggregate() {
        Aggregation aggregation = newAggregation(
                match(where("price").lt(60000)),
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
        Query query = Query.query(where("name").is("Volvo"));
        return mongoOps.count(query, Car.class);
    }

    public CommandResult executeCommand() {
        return mongoOps.executeCommand("{ " + "\"count\" : \"" + "cars" + "\"" + " }");
    }

    public List<String> executeQuery() {
        Query query = new Query();
        query.addCriteria(where("name").is("Citroen"));

        final List<String> ids = new ArrayList<>();

        mongoOps.executeQuery(query, "cars", new DocumentCallbackHandler() {
            @Override
            public void processDocument(DBObject dbObject) throws MongoException, DataAccessException {
                ids.add(dbObject.get("_id").toString());
            }
        });

        return ids;
    }

    public boolean exists() {
        return mongoOps.exists(new Query().addCriteria(where("name").is("Mercedes")), "cars");
    }

    public Set<String> getCollectionNamesSet() {
        return mongoOps.getCollectionNames();
    }

    public DBCollection getCollection() {
        return mongoOps.getCollection(getCollectionNameBasedOnClass(Car.class));
    }

    public String getCollectionNameBasedOnClass(Class<Car> carClass) {
        return mongoOps.getCollectionName(carClass);
    }


    public void saveObjectWithCustomConverter() {
        String host = "localhost";
        int port = 27017;
        String dbName = "test_another";

        SimpleMongoDbFactory mongoDbFactory = new SimpleMongoDbFactory(new MongoClient(host, port), dbName);
        MappingMongoConverter converter = new MappingMongoConverter(new DefaultDbRefResolver(mongoDbFactory),
                                                                    new MongoMappingContext());
//        custom converter to avoid saving car object _class field into the db
        converter.setTypeMapper(new DefaultMongoTypeMapper(null));

        new MongoTemplate(mongoDbFactory, converter).save(new Car("BMW", 50000));
    }

    public boolean scriptOperations() {
        ScriptOperations scriptOps = mongoOps.scriptOps();
        ExecutableMongoScript echoScript = new ExecutableMongoScript("function(x) { db.cars.find();return typeof x; }");
        // Register script and call it later
        String scriptName = "echo";
        scriptOps.register(new NamedMongoScript(scriptName, echoScript));
        Object resultOfScriptExecution = scriptOps.call(scriptName, "Date()");
        boolean echo = mongoOps.scriptOps().exists(scriptName);
        return echo;
    }

    public int stream() {
        CloseableIterator<Car> closeableIterator = mongoOps.stream(Query.query(where("price").gt(40000)), Car.class);

        List<Car> cars = new ArrayList<>();

        closeableIterator.forEachRemaining(cars::add);
//        closeableIterator.forEachRemaining(System.out::println);

        closeableIterator.close();
        return cars.size();
    }

    public MapReduceOutput mapReduce() {
        DBCollection collection = mongoOps.getCollection(getCollectionNameBasedOnClass(Car.class));

        String map ="function () { " +
                "var priceRange; " +
                "if(this.price > 50000) " +
                    "priceRange = 'moderate'; " +
                "else " +
                    "priceRange = 'cheap';" +
                "emit(priceRange, {name: this.name});" +
                "}";

        String reduce = "function (key, values) { "+
                " var total = 0; "+
                " values.forEach (function(doc) { total += 1; }); " +
                " return {cars: total}; " +
                "}";

//        String map ="function () {"+
//                "emit('size', {count:1});"+
//                "}";
//
//        String reduce = "function (key, values) { "+
//                " total = 0; "+
//                " for (var i in values) { "+
//                " total += values[i].count; "+
//                " } "+
//                " return {count:total} }";

        return collection.mapReduce(new MapReduceCommand(collection,
                                                         map,
                                                         reduce,
                                                         null,
                                                         MapReduceCommand.OutputType.INLINE,
                                                         null));
    }
}
