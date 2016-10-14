package org.xander.dbAccess;

import org.bson.Document;
import org.junit.Test;

public class ClientDBIndexTest extends ClientDBAccessTest{
    @Test
    public void index() {
    }

    private void createCompoundIndex() {
        db.getCollection("restaurants").createIndex(new Document("cuisine", 1).append("address.zipcode", -1));
    }

    private String createAscendingIndex() {
        return collection.createIndex(new Document("cuisine", 1));
    }
}
