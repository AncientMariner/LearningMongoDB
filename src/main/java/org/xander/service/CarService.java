package org.xander.service;

import com.mongodb.WriteResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.xander.dao.CarDao;
import org.xander.model.Car;

import java.util.List;

@Service
public class CarService {
    @Autowired
    private CarDao carDao;

    public void add(Car car) {
        carDao.save(car);
    }

    public void update(Car contact) {
        carDao.save(contact);
    }

    public Car get(String id) {
        return carDao.get(id);
    }

    public List<Car> getAll() {
        return carDao.getAll();
    }

    public void remove(String id) {
        carDao.remove(id);
    }

    public Car findOne(String id) {
        return carDao.findOne(id);
    }

    public WriteResult removeByEntity(Car car) {
        return carDao.removeByEntity(car);
    }
}
