package org.xander;

import com.mongodb.client.FindIterable;
import org.bson.Document;
import org.junit.Test;

import static com.mongodb.client.model.Sorts.ascending;

public class ClientDBSortTest extends ClientDBAccessTest {
    @Test
    public void testAddCollection() {
    }

    private void staticSortData() {
        FindIterable<Document> sort = collection
                .find()
                .sort(ascending("borough", "address.zipcode"));
        printValues(sort);

    }

    private void sortData() {
        FindIterable<Document> iterable = collection
                .find()
                .sort(new Document("borough", 1)
                .append("address.zipcode", 1));
        printValues(iterable);
    }
}
