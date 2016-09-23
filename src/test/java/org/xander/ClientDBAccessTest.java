package org.xander;

import com.mongodb.Block;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
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

    @Before
    public void setUp() {
        String host = "localhost";
        int port = 27017;
        mongoClient = new MongoClient(host, port);
        db = mongoClient.getDatabase(databaseName);
    }

    @Test
    public void testDBPresent() {
        MongoIterable<String> dbCollectionNames = db.listCollectionNames();

        String expectedCollectionName = "restaurants";
        assertThat("database name is present", dbCollectionNames.first(), is(expectedCollectionName));
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
