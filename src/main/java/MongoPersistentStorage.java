import java.util.List;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import org.bson.Document;

public class MongoPersistentStorage implements PersistentStorage {

    private final MongoCollection<Document> articles;
    private final MongoCollection<Document> feeds;

    public MongoPersistentStorage() {
        MongoClient client = MongoClients.create();
        MongoDatabase database = client.getDatabase("burstMyBubble");
        this.articles = database.getCollection("articles");
        this.feeds = database.getCollection("feeds");
    }

    @Override
    public List<Pair<String, String>> getFeeds() {
        return null;
    }

    @Override
    public void insertArticle(Article article) {

    }
    
}