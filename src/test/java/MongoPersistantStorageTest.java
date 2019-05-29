import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.jupiter.api.Test;

import org.bson.Document;

class MongoPersistentStorageTest {

    MongoClient client = MongoClients.create();
    MongoDatabase database = client.getDatabase("burstMyBubble");
    private final MongoCollection<Document> articles = database.getCollection("articles");
    private final MongoCollection<Document> feeds = database.getCollection("feeds");


    @Test
    public void returnsSameInformationAsDirectReadFromRSS(){
        //TODO
    
    }


}