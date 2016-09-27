package org.xander;

import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.junit.Test;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class ClientDBRemoveDataTest extends ClientDBAccessTest {

    @Test
    public void testUpdate() {
    }

    private void dropTheCollection() {
        collection.drop();
    }

    private void deleteAll() {
        collection.deleteMany(new Document());
    }

    private void deleteMany() {
        DeleteResult deleteResult = collection.deleteMany(new Document("borough", "Manhattan"));
        assertThat("deleted more than 0 document", deleteResult.getDeletedCount(), is(0L));
    }
}
