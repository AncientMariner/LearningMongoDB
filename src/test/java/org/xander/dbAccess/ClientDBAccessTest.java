package org.xander.dbAccess;

import com.mongodb.Block;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ClientDBAccessTest {
    protected MongoClient mongoClient;
    protected MongoDatabase db;
    public static final String DATABASE_NAME = "test";
    public static final String HOST = "localhost";
    public static final int PORT = 27017;
    public static final String RESTAURANTS_COLLECTION = "restaurants";
    protected MongoCollection<Document> collection;

    @Before
    public void setUp() {
        mongoClient = new MongoClient(HOST, PORT);
        db = mongoClient.getDatabase(DATABASE_NAME);
        collection = db.getCollection(RESTAURANTS_COLLECTION);
    }

    @Test
    public void dbPresent() {
        List<String> collectionNames = new ArrayList<>();
        db.listCollectionNames().forEach((Consumer<? super String>) e -> collectionNames.add(e));

        assertThat("there is no restraunt collection present", collectionNames.contains(RESTAURANTS_COLLECTION), is(true));
    }

    protected void cleanSpecificDatabase(String collectionName) {
        db.getCollection(collectionName).drop();
    }

    protected void printValues(FindIterable<Document> iterable) {
        iterable.forEach(new Block<Document>() {
            @Override
            public void apply(final Document document) {
                System.out.println(document);
            }
        });
    }

    @After
    public void tearDown() {
        mongoClient.close();
    }
}
