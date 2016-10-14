package org.xander.dbAccess;

import com.mongodb.client.FindIterable;
import org.bson.Document;
import org.junit.Test;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.gt;
import static com.mongodb.client.model.Filters.lt;
import static com.mongodb.client.model.Filters.or;
import static java.util.Arrays.asList;

public class ClientDBFilterTest extends ClientDBAccessTest {
    @Test
    public void addCollection() {
    }

    private void conditionalFilter() {
        FindIterable<Document> iterable1 = collection.find(new Document("grades.score", new Document("$gt", 90)));
        FindIterable<Document> iterable2 = collection.find(new Document("grades.score", new Document("$lt", 3)));
        printValues(iterable2);
    }

    private void filterSpecificParameter() {
        FindIterable<Document> iterable = collection.find(new Document("borough", "Manhattan"));
        FindIterable<Document> iterable1 = collection.find(new Document("address.zipcode", "10075"));

        printValues(iterable);
    }

    private void staticFilter() {
        FindIterable<Document> documents = collection.find(eq("borough", "Manhattan"));
        FindIterable<Document> documents2 = collection.find(eq("address.street", "Francis Lewis Boulevard"));
        FindIterable<Document> documents1 = collection.find(eq("address.zipcode", "10075"));
        FindIterable<Document> documents3 = collection.find(eq("grades.grade", "B"));
        printValues(documents3);
    }

    private void conditionalStaticFilter() {
        FindIterable<Document> iterable1 = collection.find(gt("grades.score", 100));
        FindIterable<Document> iterable2 = collection.find(lt("grades.score", 10));

        printValues(iterable1);
    }

    private void combinedConditionalFilter() {
        FindIterable<Document> iterable1 = collection.find(
                new Document("cuisine", "Italian").append("address.zipcode", "10075"));
        FindIterable<Document> iterable2 = collection.find(
                new Document("$or", asList(new Document("cuisine", "Italian"),
                        new Document("address.zipcode", "10075"))));
        printValues(iterable2);
    }

    private void combinedConditionalStaticFilter() {
        FindIterable<Document> iterable1 = collection.find(and(eq("cuisine", "Italian"), eq("address.zipcode", "10075")));
        FindIterable<Document> iterable2 = collection.find(or(eq("cuisine", "Italian"), eq("address.zipcode", "10075")));
        printValues(iterable2);
    }
}
