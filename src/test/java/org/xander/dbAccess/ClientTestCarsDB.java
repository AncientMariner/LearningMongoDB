package org.xander.dbAccess;

import com.mongodb.BasicDBObject;
import com.mongodb.Block;
import com.mongodb.MongoClient;
import com.mongodb.MongoCommandException;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;
import com.mongodb.util.JSON;
import org.bson.Document;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.gt;
import static com.mongodb.client.model.Filters.lt;
import static com.mongodb.client.model.Projections.exclude;
import static com.mongodb.client.model.Projections.excludeId;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ClientTestCarsDB {
    private MongoClient mongoClient;
    private MongoDatabase database;

    private static final String CARS_COLLECTION = "cars";
    private final String DATABASE_NAME = "test_another";
    private MongoCollection<Document> collection;


    @Before
    public void setUp() {
        mongoClient = new MongoClient(ClientDBAccessTest.HOST, ClientDBAccessTest.PORT);
        database = mongoClient.getDatabase(DATABASE_NAME);
        collection = database.getCollection(CARS_COLLECTION);
    }

    @Test
    public void data() {
//        modifyData();
        printValues(collection.find());
    }

    private void carsTemporaryDatabase() {
        MongoIterable<String> dbCollectionNames = database.listCollectionNames();
        String expectedCollectionName = CARS_COLLECTION;

        assertThat("expected collection is not present", dbCollectionNames.first(), is(expectedCollectionName));
    }

    private void dbStatistics() {
        final String dbstatsCollection = "dbstats";
        Document stats = database.runCommand(new Document(dbstatsCollection, 1));

        for (Map.Entry<String, Object> set : stats.entrySet()) {
            System.out.format("%s: %s%n", set.getKey(), set.getValue());
        }
    }

    private void dataRetrival() {
        try (MongoCursor<Document> cur = collection.find().iterator()) {
            while (cur.hasNext()) {

                Document doc = cur.next();

                List<Object> list = new ArrayList(doc.values());
                System.out.print(list.get(1));
                System.out.print(": ");
                System.out.println(list.get(2));
            }
        }
    }

    private void queryOperator() {
        BasicDBObject query = new BasicDBObject("price", new BasicDBObject("$gt", 50000));

//        collection.find(query).forEach((Block)document -> System.out.println(((Document)document).toJson()));

        collection.find(query).forEach(new Block<Document>() {
            @Override
            public void apply(final Document document) {
                System.out.println(document.toJson());
            }
        });
    }

    private void factoryFilterQuery() {
        int descendingOrder = -1;
        FindIterable it = collection.find(and(lt("price", 50000),
                                       gt("price", 20000))).sort(new Document("price", descendingOrder));

        ArrayList<Document> docs = new ArrayList();
        it.into(docs);

//        docs.forEach(document -> System.out.println(document));
        for (Document doc : docs) {
            System.out.println(doc);
        }
    }

    private void projections() {
        FindIterable it1 = collection.find().projection(excludeId());
        FindIterable it2 = collection.find().projection(exclude("name"));
        FindIterable it3 = collection.find().projection(exclude("_id"));
        FindIterable it4 = collection.find().projection(exclude("name", "price"));

        ArrayList<Document> docs = new ArrayList();
        it3.into(docs);

        for (Document doc : docs) {
            System.out.println(doc);
        }
    }

    private void limitingDataOutPut() {
        FindIterable it = collection.find().skip(2).limit(4);

        it.forEach((Block<Document>) System.out::println);
    }

    private void createCollection() {
        try {
            database.createCollection(CARS_COLLECTION);
        } catch (MongoCommandException e) {
            database.getCollection(CARS_COLLECTION).drop();
        }
        List<Document> writes = new ArrayList<>();
        MongoCollection<Document> carsCol = database.getCollection(CARS_COLLECTION);

        Document d1 = new Document("_id", 1);
        d1.append("name", "Audi");
        d1.append("price", 52642);
        writes.add(d1);

        Document d2 = new Document("_id", 2);
        d2.append("name", "Mercedes");
        d2.append("price", 57127);
        writes.add(d2);

        Document d3 = new Document("_id", 3);
        d3.append("name", "Skoda");
        d3.append("price", 9000);
        writes.add(d3);

        Document d4 = new Document("_id", 4);
        d4.append("name", "Volvo");
        d4.append("price", 29000);
        writes.add(d4);

        Document d5 = new Document("_id", 5);
        d5.append("name", "Bentley");
        d5.append("price", 350000);
        writes.add(d5);

        Document d6 = new Document("_id", 6);
        d6.append("name", "Citroen");
        d6.append("price", 21000);
        writes.add(d6);

        Document d7 = new Document("_id", 7);
        d7.append("name", "Hummer");
        d7.append("price", 41400);
        writes.add(d7);

        writes.add(new Document("_id", 8)
                .append("name", "Volkswagen")
                .append("price", 21600));

        carsCol.insertMany(writes);
    }

    private void createCollectionFromJson() {
        String collectionName = "continents";
        collection = database.getCollection(collectionName);

        BasicDBObject africa = (BasicDBObject) JSON.parse("{_id : 1, name : 'Africa'}");
        BasicDBObject asia = (BasicDBObject) JSON.parse("{_id : 2, name : 'Asia'}");
        BasicDBObject europe = (BasicDBObject) JSON.parse("{_id : 3, name : 'Europe'}");
        BasicDBObject america = (BasicDBObject) JSON.parse("{_id : 4, name : 'America'}");
        BasicDBObject australia = (BasicDBObject) JSON.parse("{_id : 6, name : 'Australia'}");

        collection.insertOne(new Document(africa));
        collection.insertOne(new Document(asia));
        collection.insertOne(new Document(europe));
        collection.insertOne(new Document(america));
        collection.insertOne(new Document(australia));
        collection.insertOne(new Document((BasicDBObject) JSON.parse("{_id : 7, name : 'Antarctica'}")));

        printValues(database.getCollection(collectionName).find());
        cleanSpecificCollection(collectionName);
    }

    private void modifyData() {
//        collection.insertOne(new Document("name", "Mercury").append("_id", 9).append("price", 62000));
        collection.deleteOne(eq("name", "Mercury"));
        collection.updateOne(new Document("name", "Audi"), new Document("$set", new Document("price", 92000)));
    }

    private void cleanSpecificCollection(String collectionName) {
        database.getCollection(collectionName).drop();
    }

    private void printValues(FindIterable<Document> iterable) {
//        iterable.forEach((Block<Document>) document -> System.out.println(document));
        iterable.forEach((Block<Document>) System.out::println);
    }

    @After
    public void tearDown() {
        mongoClient.close();
    }
}
