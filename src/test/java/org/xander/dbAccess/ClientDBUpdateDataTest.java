package org.xander.dbAccess;

import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.junit.Test;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class ClientDBUpdateDataTest extends ClientDBAccessTest {

    @Test
    public void update() {
    }

    private void replaceOne() {
        UpdateResult updateResult = collection.replaceOne(new Document("restaurant_id", "41704620"),
                new Document("address",
                        new Document()
                                .append("street", "2 Avenue")
                                .append("zipcode", "10075")
                                .append("building", "1480")
                                .append("coord", asList(-73.9557413, 40.7720266)))
                        .append("name", "Vella 2"));

        assertThat("modified more than 1 document", updateResult.getModifiedCount(), is(1L));
    }

    private void updateMultipleDocuments() {
        UpdateResult updateResult = collection.updateMany(new Document("address.zipcode", "10016").append("cuisine", "Other"),
                new Document("$set", new Document("cuisine", "Category To Be Determined"))
                        .append("$currentDate", new Document("lastModified", true)));

        assertThat("modified more than 0 document", updateResult.getModifiedCount(), is(0L));
    }

    private void updateAnEmbeddedField() {
        UpdateResult updateResult = collection.updateOne(new Document("name", "Juni"),
                new Document("$set", new Document("cuisine", "American (New)"))
                        .append("$currentDate", new Document("lastModified", true)));

        assertThat("modified more than 1 document", updateResult.getModifiedCount(), is(1L));
    }
}
