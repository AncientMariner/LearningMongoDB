package org.xander.model;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

public class CarTest {
    private Car car;
    @Before
    public void setUp() {
        car = new Car();
    }

    @Test
    public void testConstructor() {
        Car car1 = new Car("23", "Hyundai", 10000);
        Car car2 = new Car("Hyundai", 10000);

        assertThat("toString is different", car1.toString(), is("Car{id='23', name='Hyundai', price=10000}"));
        assertThat("toString is different", car2.toString(), is("Car{id='null', name='Hyundai', price=10000}"));
    }

    @Test
    public void testToString() {
        assertThat("toString is different", car.toString(), is("Car{id='null', name='null', price=0}"));
    }

    @Test
    public void testGetName() {
        car.setName("Nissan");
        assertThat("name is different", car.getName(), is("Nissan"));
    }

    @Test
    public void testGetPrice() {
        car.setPrice(90000);
        assertThat("price is different", car.getPrice(), is(90000));
    }

    @Test
    public void testGetId() {
        assertNull("price is different", car.getId());
    }

    @Test
    public void testSetName() {
        car.setName("Nissan");
        assertThat("name is different", car.getName(), is("Nissan"));
        car.setName("Toyota");
        assertThat("name is different", car.getName(), is("Toyota"));
    }

    @Test
    public void testSetPrice() {
        car.setPrice(90000);
        assertThat("price is different", car.getPrice(), is(90000));
        car.setPrice(190000);
        assertThat("price is different", car.getPrice(), is(190000));
    }
}
