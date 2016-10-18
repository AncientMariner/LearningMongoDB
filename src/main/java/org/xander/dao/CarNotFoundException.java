package org.xander.dao;

public class CarNotFoundException extends RuntimeException {
    public CarNotFoundException(String id) {
        super(String.format("No car entry found with id: <%s>", id));
    }
}
