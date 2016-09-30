package org.xander.dao;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.xander.model.Car;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

@ContextConfiguration(locations = {"classpath:/org/xander/model/applicationContext-dao.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
public class CarDaoTest {
    Car car;
    String idToRemove;

    @Autowired private CarDao carDao;

    @Before
    public void setUp() {
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

    @After
    public void tearDown() {
        if (idToRemove != null && !idToRemove.isEmpty()) {
            carDao.remove(idToRemove);
        }
    }
}
