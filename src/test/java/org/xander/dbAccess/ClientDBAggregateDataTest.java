package org.xander.dbAccess;

import com.mongodb.Block;
import com.mongodb.client.AggregateIterable;
import org.bson.Document;
import org.junit.Test;

import static java.util.Arrays.asList;

public class ClientDBAggregateDataTest extends ClientDBAccessTest {

    @Test
    public void update() {
    }

    private void matchAndGroup() {
        AggregateIterable<Document> iterable = collection.aggregate(asList(
                new Document("$match", new Document("borough", "Queens").append("cuisine", "Brazilian")),
                new Document("$group", new Document("_id", "$address.zipcode").append("countName", new Document("$sum", 1)))));

        printValues(iterable);
    }

    private void groupBySpecificField() {
        AggregateIterable<Document> iterable = collection.aggregate(asList(
                new Document("$group", new Document("_id", "$borough").append("countName", new Document("$sum", 1)))));

        printValues(iterable);
    }

    private void printValues(AggregateIterable<Document> iterable) {
        iterable.forEach(new Block<Document>() {
            @Override
            public void apply(final Document document) {
                System.out.println(document.toJson());
            }
        });
    }
}
