package org.xander.dao;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.BulkWriteResult;
import com.mongodb.CommandResult;
import com.mongodb.DBCollection;
import com.mongodb.MapReduceOutput;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.xander.model.Car;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

@ContextConfiguration(locations = {"classpath:/org/xander/model/applicationContext-dao.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
public class CarDaoTest {
    Car car;
    String idToRemove;

    @Autowired private CarDao carDao;

    @Before
    public void setUp() {
        carDao.collectionPresent("cars");
        car = new Car("TestCar", 90000);
        carDao.save(car);
        List<Car> all = carDao.getAll();
        for (Car carFromDb : all) {
            if (carFromDb.getName().equals(car.getName())) {
                idToRemove = carFromDb.getId();
            }
        }
    }

    @Test
    public void testSave() throws Exception {
        assertThat("id is not present", idToRemove != null, is(true));
        Car actualCar = carDao.get(idToRemove);

        assertThat("id is different", actualCar.getId(), is(idToRemove));
        assertThat("name is different", actualCar.getName(), is("TestCar"));
        assertThat("price is different", actualCar.getPrice(), is(90000));

        if (idToRemove != null && !idToRemove.isEmpty()) {
            carDao.remove(idToRemove);

            assertNull("car is not null", carDao.get(idToRemove));
        }
    }

    @Test
    public void testAggregation() {
        AggregationResults<ArrayList> aggregate = carDao.aggregate();
        Object result = ((Map.Entry) ((BasicDBObject) ((BasicDBList) aggregate.getRawResults().get("result")).toArray()[2]).entrySet().toArray()[0]).getKey();

        assertThat("price field is not present", result.equals("price"), is(true));
    }

    @Test
    public void testCollectionPresent() {
        assertTrue(carDao.collectionPresent("cars"));
    }

    @Test
    public void testBulkOperation() {
        BulkWriteResult bulkWriteResult = carDao.bulkOperation();
        assertNotNull(bulkWriteResult);
        assertThat("there were not 2 documents inserted", bulkWriteResult.getInsertedCount(), is(2));
    }

    @Test
    public void testCount() {
        long count = carDao.count();
        assertThat("there are more than 1 Volvo cars", count, is(1L));
    }

    @Test
    public void testExecuteCommand() {
        CommandResult commandResult = carDao.executeCommand();
        assertThat("size of the collection is 0", (Integer)commandResult.get("n") > 0, is(true));
    }

    @Test
    public void testExecuteQuery() {
        List<String> ids = carDao.executeQuery();
        assertThat("more than 1 id is found", ids.size(), is(1));
    }

    @Test
    public void testExists() {
        boolean exists = carDao.exists();
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
    public void testGetCollection() {
        DBCollection collection = carDao.getCollection();
        assertThat("collection name is not cars", collection.getName(), is("cars"));
        assertThat("db name is not test_another", collection.getDB().getName(), is("test_another"));
    }

    @Test
    public void testGetCollectionNameBasedOnClass() {
        String collectionNameBasedOnClass = carDao.getCollectionNameBasedOnClass(Car.class);
        assertThat("collection name is not cars", collectionNameBasedOnClass, is("cars"));
    }

    @Test
    public void testGetCollectionNamesSet() {
        Set<String> collectionSet = carDao.getCollectionNamesSet();
        assertThat("collection name is not present in the set", collectionSet.contains("cars"), is(true));
    }

    @Test
    public void testCustomConverter() {
        carDao.saveObjectWithCustomConverter();
    }

    @Test
    public void testScriptOperations() {
        assertTrue("script execution was not successful", carDao.scriptOperations());
    }

    @Test
    public void testStream() {
        int sizeOfTheCollection = carDao.stream();
        assertThat("number of documents is 0", sizeOfTheCollection > 0, is(true));
    }

    @Test
    public void testMapReduce() {
        MapReduceOutput output = carDao.mapReduce();
        output.results().forEach(System.out::println);
    }

    @After
    public void tearDown() {
        if (idToRemove != null && !idToRemove.isEmpty()) {
            carDao.remove(idToRemove);
        }
    }
}
