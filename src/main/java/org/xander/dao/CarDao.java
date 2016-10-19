package org.xander.dao;

import com.mongodb.BulkWriteResult;
import com.mongodb.CommandResult;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MapReduceCommand;
import com.mongodb.MapReduceOutput;
import com.mongodb.MongoClient;
import com.mongodb.MongoException;
import com.mongodb.WriteResult;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSInputFile;
import com.mongodb.util.JSON;
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
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.mongodb.core.script.ExecutableMongoScript;
import org.springframework.data.mongodb.core.script.NamedMongoScript;
import org.springframework.data.util.CloseableIterator;
import org.springframework.stereotype.Repository;
import org.xander.model.Car;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.Assert.assertTrue;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.project;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.sort;
import static org.springframework.data.mongodb.core.query.Criteria.where;

@Repository
public class CarDao {
    @Autowired
    private MongoOperations mongoOps;

    public void save(Car car) {
        mongoOps.save(car);
    }

    public String saveImage(String imageName) {
        File file = FileSystems.getDefault().getPath(".", imageName).toFile();
        assertTrue("file not found", file.exists());

        DBCollection collectionBasedOnName = getCollectionBasedOnName(getCollectionNameBasedOnClass(Car.class));
        DB db = collectionBasedOnName.getDB();

        GridFS gfsPhoto = new GridFS(db, "photo");
        GridFSInputFile gfsFile = null;
        String filename = null;
        try {
            gfsFile = gfsPhoto.createFile(file);
            gfsFile.setFilename(imageName);
            gfsFile.save();
            filename = ((GridFSDBFile) gfsPhoto.getFileList().getCollection().findOne()).getFilename();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            gfsPhoto.remove(imageName);
        }
//        gfsPhoto.getFileList().forEachRemaining(System.out::println);
//        GridFSDBFile one = gfsPhoto.findOne("Capture.PNG");

//        String newName = "newName.PNG";
//        GridFS gfsPhoto1 = new GridFS(db, "photo");
//        GridFSDBFile one = gfsPhoto.findOne(imageName);
//        try {
//            one.writeTo(newName);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        return filename;
    }

    public List<Car> updateNameToLowerCase(Car car) {
        Query queryToUpdate = new Query().addCriteria(where("name").is(car.getName()).and("price").is(50000));
//        Update updateToNewValue = Update.update("name", car.getName().toLowerCase());

        Update updateAnotherWay = new Update();
        updateAnotherWay.set("name", car.getName().toLowerCase());
        mongoOps.updateFirst(queryToUpdate, updateAnotherWay, Car.class);
        return mongoOps.find(new Query().addCriteria(where("name").is(car.getName().toLowerCase()).and("price").is(50000)), Car.class);
    }

    public Car get(String id) {
        Car one = (mongoOps.findOne(Query.query(where("id").is(id)), Car.class));
        return Optional.ofNullable(one).orElseThrow(() -> new CarNotFoundException(id));
    }

    public Car findOne(String id) {
        Car one = mongoOps.findOne(Query.query(where("id").is(id)), Car.class);
        return Optional.ofNullable(one).orElseThrow(() -> new CarNotFoundException(id));
    }

    public List<Car> getAll() {
        return mongoOps.findAll(Car.class);
    }

    public List<Car> getEntityLikeQuery() {
        String tagName = "Merc";

        Query query = new Query();
        query.limit(2);
        query.addCriteria(where("name").regex(tagName));

        return mongoOps.find(query, Car.class);
    }

    public void remove(String id) {
        mongoOps.remove(Query.query(where("id").is(id)), Car.class);
    }

    public WriteResult removeByEntity(Car car) {
        return mongoOps.remove(car);
    }


    public Car convertJsonToDbObject(String json) {
        DBObject dbObject = (DBObject) JSON.parse(json);
        mongoOps.insert(dbObject, "cars");

        Car car = mongoOps.findOne(Query.query(where("name").is("Audi TT")), Car.class);

        mongoOps.remove(car);
        return car;
    }

    public boolean collectionPresent(String collectionName) {
        return mongoOps.collectionExists(collectionName);
    }

    public AggregationResults<ArrayList> aggregate() {
        Aggregation aggregation = newAggregation(
                match(where("price").lt(60000)),
//                group("price").countName().as("total"),
                project("price").andExclude("_id"),
                sort(Sort.Direction.DESC, "price"));

        AggregationResults<ArrayList> aggregate = mongoOps.aggregate(aggregation, Car.class, ArrayList.class);
        return aggregate;
    }

    public BulkWriteResult bulkOperation(Document... documents) {
        BulkOperations bulkOperations = mongoOps.bulkOps(BulkOperations.BulkMode.ORDERED,
                getCollectionNameBasedOnClass(Car.class));
        for (Document document : documents) {
            bulkOperations.insert(document);
        }

        BulkWriteResult bulkWriteResult = bulkOperations.execute();
        return bulkWriteResult;
    }

    public long countName(String key, String criteriaDefinition) {
        Query query = Query.query(where(key).is(criteriaDefinition));
        return mongoOps.count(query, Car.class);
    }

    public CommandResult executeCommand(String jsonCommand) {
        return mongoOps.executeCommand(jsonCommand);
    }

    public List<String> executeQuery(Query query) {
        final List<String> ids = new ArrayList<>();

        mongoOps.executeQuery(query, getCollectionNameBasedOnClass(Car.class), new DocumentCallbackHandler() {
            @Override
            public void processDocument(DBObject dbObject) throws MongoException, DataAccessException {
                ids.add(dbObject.get("_id").toString());
            }
        });

        return ids;
    }

    public boolean exists(Query query) {
        return mongoOps.exists(query, getCollectionNameBasedOnClass(Car.class));
    }

    public Set<String> getCollectionNamesSet() {
        return mongoOps.getCollectionNames();
    }

    public DBCollection getCollectionBasedOnName(String name) {
        return mongoOps.getCollection(name);
    }

    public String getCollectionNameBasedOnClass(Class<Car> aClass) {
        return mongoOps.getCollectionName(aClass);
    }

    public void saveObjectWithCustomConverter(Car car) {
        final String host = "localhost";
        final int port = 27017;
        final String dbName = "test_another";

        SimpleMongoDbFactory mongoDbFactory = new SimpleMongoDbFactory(new MongoClient(host, port), dbName);
        MappingMongoConverter converter = new MappingMongoConverter(new DefaultDbRefResolver(mongoDbFactory),
                new MongoMappingContext());
//        custom converter to avoid saving car object _class field into the db
        converter.setTypeMapper(new DefaultMongoTypeMapper(null));
        MongoTemplate mongoTemplate = new MongoTemplate(mongoDbFactory, converter);

        mongoTemplate.save(car);
    }

    public boolean scriptOperations() {
        ScriptOperations scriptOps = mongoOps.scriptOps();
        ExecutableMongoScript echoScript = new ExecutableMongoScript("function(x) { db.cars.find();return typeof x; }");
        // Register script and call it later
        String scriptName = "echo";
        scriptOps.register(new NamedMongoScript(scriptName, echoScript));
//        Object resultOfScriptExecution = scriptOps.call(scriptName, "Date()");
        return mongoOps.scriptOps().exists(scriptName);
    }

    public int streamCollection() {
        CloseableIterator<Car> closeableIterator = mongoOps.stream(Query.query(where("price").gt(40000).orOperator(where("name").is("Hummer"))), Car.class);

        List<Car> cars = new ArrayList<>();

        closeableIterator.forEachRemaining(cars::add);
//        closeableIterator.forEachRemaining(System.out::println);

//        if we had a new list to create - it is better to use a reference and collect method
//          rather than to add values to the existing collection
//        cars.stream().map(car -> car.getName()).collect(Collectors.toList());

        closeableIterator.close();
        return cars.size();
    }

    public MapReduceOutput mapReduce() {
        DBCollection collection = mongoOps.getCollection(getCollectionNameBasedOnClass(Car.class));
        String map = "function () { "
                + "var priceRange; "

                + "if(this.price > 50000) "
                + "priceRange = 'moderate'; "
                + "else "
                + "priceRange = 'cheap';"
                +
                "emit(priceRange, {name: this.name});"
                + "}";
        String reduce = "function (key, values) { "
                + " var total = 0; "
                + " values.forEach (function(doc) { total += 1; }); "
                + " return {cars: total}; "
                + "}";

//        String map ="function () {"+
//                                  "emit('size', {countName:1});"+
//                    "}";
//
//        String reduce = "function (key, values) {"+
//                          "total = 0; "+
//                          "for (var i in values) { "+
//                          "total += values[i].countName; "+
//                        "} "+
//                        "return {countName:total} }";
        MapReduceCommand mapReduceCommand = new MapReduceCommand(collection,
                map,
                reduce,
                null,
                MapReduceCommand.OutputType.INLINE,
                null);
        return collection.mapReduce(mapReduceCommand);
    }
}
