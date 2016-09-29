package org.xander.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import org.xander.model.Car;

import java.util.List;

@Repository
public class CarDao {
    @Autowired
    private MongoOperations mongoOperations;

    public void save(Car car) {
        mongoOperations.save(car);
    }

    public Car get(String id) {
        return mongoOperations.findOne(Query.query(Criteria.where("id").is(id)), Car.class);
    }

    public List<Car> getAll() {
        return mongoOperations.findAll(Car.class);
    }

    public void remove(String id) {
        mongoOperations.remove(Query.query(Criteria.where("id").is(id)), Car.class);
    }
}
