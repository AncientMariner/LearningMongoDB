package org.xander.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.ContextConfiguration;
import org.xander.dao.CarDao;
import org.xander.model.Car;

import java.util.ArrayList;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

@ContextConfiguration(locations = {"classpath:/org/xander/service/applicationContext-service.xml",
                                   "classpath:/org/xander/model/applicationContext-dao.xml"})
public class CarServiceTest {
    @Mock private CarDao carDao;
    @Mock private Car car;
    @InjectMocks private CarService carService;

    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void add() {
        when(carDao.get(anyString())).thenReturn(car);

        carService.get(anyString());

        verify(carDao).get(anyString());
        verifyNoMoreInteractions(carDao);
    }

    @Test
    public void update() {
        carService.update(any(Car.class));

        verify(carDao).save(any(Car.class));
        verifyNoMoreInteractions(carDao);
    }

    @Test
    public void get() {
        when(carDao.get(anyString())).thenReturn(car);

        carService.get(anyString());

        verify(carDao).get(anyString());
        verifyNoMoreInteractions(carDao);
    }

    @Test
    public void findOne() {
        when(carDao.findOne(anyString())).thenReturn(car);

        carService.findOne(anyString());

        verify(carDao).findOne(anyString());
        verifyNoMoreInteractions(carDao);
    }

    @Test
    public void getAll() {
        when(carDao.getAll()).thenReturn(new ArrayList<>());
        carService.getAll();

        verify(carDao).getAll();
        verifyNoMoreInteractions(carDao);
    }

    @Test
    public void remove() {
        carService.remove(anyString());

        verify(carDao).remove(anyString());
        verifyNoMoreInteractions(carDao);
    }

    @Test
    public void testRemoveByEntity() {
        carService.removeByEntity(car);

        verify(carDao).removeByEntity(any(Car.class));
        verifyNoMoreInteractions(carDao);
    }

}