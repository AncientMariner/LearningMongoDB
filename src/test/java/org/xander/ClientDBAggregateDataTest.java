package org.xander;

import com.mongodb.Block;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.junit.Test;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class ClientDBAggregateDataTest extends ClientDBAccessTest {

    @Test
    public void testUpdate() {
    }

    private void matchAndGroup() {
        AggregateIterable<Document> iterable = collection.aggregate(asList(
                new Document("$match", new Document("borough", "Queens").append("cuisine", "Brazilian")),
                new Document("$group", new Document("_id", "$address.zipcode").append("count", new Document("$sum", 1)))));

        printValues(iterable);
    }

    private void groupBySpecificField() {
        AggregateIterable<Document> iterable = collection.aggregate(asList(
                new Document("$group", new Document("_id", "$borough").append("count", new Document("$sum", 1)))));

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
