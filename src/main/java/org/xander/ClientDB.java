package org.xander;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;

public class ClientDB {
    public static void main(String[] args) {
        MongoClient mongoClient = new MongoClient("localhost", 27017);
        MongoDatabase db = mongoClient.getDatabase("test");

        for (String name : db.listCollectionNames()) {
            System.out.println(name);
        }
        mongoClient.close();
    }
}
