package org.xander.dao;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.BulkWriteResult;
import com.mongodb.CommandResult;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MapReduceOutput;
import com.mongodb.WriteResult;
import org.bson.Document;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.xander.model.Car;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.springframework.data.mongodb.core.query.Criteria.where;

@ContextConfiguration(locations = {"classpath:/org/xander/model/applicationContext-dao.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
public class CarDaoTest {
    public static final String COLLECTION_NAME = "cars";

    @Autowired private CarDao carDao;
    @Rule public final ExpectedException exception = ExpectedException.none();

    @Test
    public void removeByEntity() {
        Car actualCar = new Car("Mercedes", 90000);
        carDao.save(actualCar);

        assertNotNull("cound not get an entity for delete", actualCar);

        WriteResult writeResult = carDao.removeByEntity(actualCar);

        assertTrue("entity was not acknowledged", writeResult.wasAcknowledged());
        assertFalse("there was an update of existing entity", writeResult.isUpdateOfExisting());
        assertNull("upserted id is not null", writeResult.getUpsertedId());
        assertThat("entity was not removed", writeResult.getN(), is(1));

        exception.expect(CarNotFoundException.class);
        carDao.get(actualCar.getId());
    }

    @Test
    public void removeByEntityNotPresent() {
        Car car = new Car();
        String id = car.getId();
        WriteResult writeResult = carDao.removeByEntity(car);

        assertTrue("entity was not acknowledged", writeResult.wasAcknowledged());
        assertFalse("there was an update of existing entity", writeResult.isUpdateOfExisting());
        assertNull("upserted id is not null", writeResult.getUpsertedId());

        assertThat("entity was not removed", writeResult.getN(), is(0));

        exception.expect(CarNotFoundException.class);
        assertNull("car was not removed", carDao.get(id));
    }

    @Test
    public void getCarById() {
        Car car = new Car("TestCar", 90000);
        carDao.save(car);

        String id = car.getId();
        Car actualCar = carDao.findOne(id);
        assertThat("id is different", actualCar.getId(), is(car.getId()));
        assertThat("name is different", actualCar.getName(), is("TestCar"));
        assertThat("price is different", actualCar.getPrice(), is(90000));

        carDao.remove(id);
    }

    @Test
    public void getOptionalNoEntry() {
        exception.expect(CarNotFoundException.class);
        carDao.findOne("asd");
    }

    @Test
    public void aggregation() {
        AggregationResults<ArrayList> aggregate = carDao.aggregate();
        Object result = ((Map.Entry) ((BasicDBObject) ((BasicDBList) aggregate.getRawResults().get("result")).toArray()[2]).entrySet().toArray()[0]).getKey();

        assertThat("price field is not present", result.equals("price"), is(true));
    }

    @Test
    public void collectionPresent() {
        assertTrue(carDao.collectionPresent(COLLECTION_NAME));
    }

    @Test
    public void bulkOperation() {
        Document newDocument1 = new Document("name", "Mercedes").append("price", 150000);
        Document newDocument2 = new Document("name", "Mercedes AMG").append("price", 250000);

        BulkWriteResult bulkWriteResult = carDao.bulkOperation(newDocument1, newDocument2);

        assertNotNull(bulkWriteResult);
        assertThat("there were not 2 documents inserted", bulkWriteResult.getInsertedCount(), is(2));
    }

    @Test
    public void count() {
        String key = "name";
        String criteriaDefinition = "Volvo";

        assertThat("there are more than 1 Volvo cars", carDao.countName(key, criteriaDefinition), is(1L));
    }

    @Test
    public void executeCommand() {
        String jsonCommand = "{ " + "\"count\" : \"" + COLLECTION_NAME + "\"" + " }";
        CommandResult commandResult = carDao.executeCommand(jsonCommand);

        assertThat("size of the collection is 0", (Integer)commandResult.get("n") > 0, is(true));
    }

    @Test
    public void executeQuery() {
        Query query = new Query().addCriteria(where("name").is("Citroen"));
        List<String> ids = carDao.executeQuery(query);

        assertThat("more than 1 id is found", ids.size(), is(1));
    }

    @Test
    public void exists() {
        Query query = new Query().addCriteria(where("name").is("Mercedes"));
        boolean exists = carDao.exists(query);

        assertThat("Mercedes cars are not present", exists, is(true));
    }

//    @Test
//    public void shouldSupportGeoNearQueriesForAggregationWithDistanceField() {
//        mongoTemplate.insert(new Venue("Penn Station", -73.99408, 40.75057));
//        mongoTemplate.insert(new Venue("10gen Office", -73.99171, 40.738868));
//        mongoTemplate.insert(new Venue("Flatiron Building", -73.988135, 40.741404));
//
//        mongoTemplate.indexOps(Venue.class).ensureIndex(new GeospatialIndex("location"));
//
//        NearQuery geoNear = NearQuery.near(-73, 40, Metrics.KILOMETERS).num(10).maxDistance(150);
//
//        Aggregation agg = newAggregation(Aggregation.geoNear(geoNear, "distance"));
//        AggregationResults<DBObject> result = mongoTemplate.aggregate(agg, Venue.class, DBObject.class);
//
//        assertThat(result.getMappedResults(), hasSize(3));
//
//        DBObject firstResult = result.getMappedResults().get(0);
//        assertThat(firstResult.containsField("distance"), is(true));
//        assertThat((Double) firstResult.get("distance"), closeTo(117.620092203928, 0.00001));
//    }

    @Test
    public void getCollection() {
        String collectionNameBasedOnClass = carDao.getCollectionNameBasedOnClass(Car.class);
        DBCollection collection = carDao.getCollectionBasedOnName(collectionNameBasedOnClass);

        assertThat("collection name is not cars", collection.getName(), is(COLLECTION_NAME));
        assertThat("db name is not test_another", collection.getDB().getName(), is("test_another"));
    }

    @Test
    public void getCollectionNameBasedOnClass() {
        String collectionNameBasedOnClass = carDao.getCollectionNameBasedOnClass(Car.class);

        assertThat("collection name is not cars", collectionNameBasedOnClass, is(COLLECTION_NAME));
    }

    @Test
    public void getCollectionNamesSet() {
        Set<String> collectionSet = carDao.getCollectionNamesSet();

        assertThat("collection name is not present in the set", collectionSet.contains(COLLECTION_NAME), is(true));
    }

    @Test
    public void customConverter() {
        String carName = "BMW";
        int carPrice = 50000;
        carDao.saveObjectWithCustomConverter(new Car(carName, carPrice));
        Query query = new Query().addCriteria(where("name").is(carName).and("price").is(carPrice));

        assertThat("bmw car with 50000 price is not present", carDao.exists(query), is(true));
    }

    @Test
    public void scriptOperations() {
        assertTrue("script execution was not successful", carDao.scriptOperations());
    }

    @Test
    public void streamCollection() {
        int sizeOfTheCollection = carDao.streamCollection();

        assertThat("number of documents is 0", sizeOfTheCollection > 0, is(true));
    }

    @Test
    public void mapReduce() {
        MapReduceOutput output = carDao.mapReduce();
        Iterable<DBObject> results = output.results();

        assertThat("there are more than 2 categories of cars", ((ArrayList) results).size(), is(2));
    }
}
