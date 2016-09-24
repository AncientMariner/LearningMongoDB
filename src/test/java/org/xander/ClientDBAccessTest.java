package org.xander;

import com.mongodb.Block;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;
import org.bson.Document;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ClientDBAccessTest {
    protected MongoClient mongoClient;
    protected MongoDatabase db;
    protected final String databaseName = "test";
    public static final String HOST = "localhost";
    public static final int PORT = 27017;
    public static final String RESTAURANTS_COLLECTION = "restaurants";
    protected MongoCollection<Document> collection;

    @Before
    public void setUp() {
        mongoClient = new MongoClient(HOST, PORT);
        db = mongoClient.getDatabase(databaseName);
        collection = db.getCollection(RESTAURANTS_COLLECTION);
    }

    @Test
    public void testDBPresent() {
        MongoIterable<String> dbCollectionNames = db.listCollectionNames();

        MongoIterable<String> dbCollectionNames1 = dbCollectionNames;
        for (String s : dbCollectionNames1) {
            System.out.println(s);
        }
        assertThat("expected collection is not present", dbCollectionNames1.first(), is(RESTAURANTS_COLLECTION));
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
