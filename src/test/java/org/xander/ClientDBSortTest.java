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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Sorts.ascending;
import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ClientDBSortTest extends ClientDBAccessTest {
    @Test
    public void testAddCollection() {
    }

    private void staticSortData() {
        FindIterable<Document> sort = db.getCollection("restaurants")
                .find()
                .sort(ascending("borough", "address.zipcode"));
        printValues(sort);

    }

    private void sortData() {
        FindIterable<Document> iterable = db.getCollection("restaurants")
                .find()
                .sort(new Document("borough", 1)
                .append("address.zipcode", 1));
        printValues(iterable);
    }
}
