package org.xander.dao;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.BulkWriteResult;
import com.mongodb.CommandResult;
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
        carDao.executeQuery();
    }

    @Test
    public void testExists() {
        carDao.exists();
    }

    @Test
    public void testFind() {
        carDao.find();
    }

    @Test
    public void testGeoNear() {
        carDao.geoNear();
    }

    @Test
    public void testGetCollection() {
        carDao.getCollection();
    }

    @Test
    public void testConverted() {
        carDao.getMongoConverter();

    }

    @Test
    public void testScriptOperations() {
        carDao.scriptOperations();
    }

    @Test
    public void testStream() {
        carDao.stream();
    }

    @Test
    public void testMapReduce() {
        carDao.mapReduce();
    }


    @After
    public void tearDown() {
        if (idToRemove != null && !idToRemove.isEmpty()) {
            carDao.remove(idToRemove);
        }
    }
}
