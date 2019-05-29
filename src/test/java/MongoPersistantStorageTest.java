import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

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