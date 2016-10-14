package org.xander.dbAccess;

import com.mongodb.client.result.DeleteResult;
import org.bson.Document;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class ClientDBRemoveDataTest extends ClientDBAccessTest {

    @Test
    public void update() {
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
