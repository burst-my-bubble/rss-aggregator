import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import com.mongodb.client.FindIterable;
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
    public boolean urlExists(String url) {
        return articles.find(new Document("url", url)).first() != null;
    }

    @Override
    public List<Pair<String, Object>> getFeeds() {
        FindIterable<Document> iterDoc = feeds.find();
        List<Pair<String, Object>> result = new ArrayList<>();
        Iterator it = iterDoc.iterator();
        while (it.hasNext()) {
            Document d = (Document) it.next();
            result.add(new Pair<>((String) d.get("url"), d.get("_id")));
        }
        return result;
    }

    /**
     * Gets a list of all news articles in the RSS feed at a given news site's
     * URL. Each article is also entered into the database.
     * @param feedId
     * @param url the location of the news source
     * @return a list of Mongo documents corresponding to the articles
     */
    @Override
    public void insertArticles(List<Article> articlesToBeInserted, Object feedId) {
        articles.insertMany(articlesToBeInserted.stream().map(e -> 
            new Document()
                .append("title", e.getTitle())
                .append("author", e.getAuthor())
                .append("published_date", e.getPublishedDate())
                .append("url", e.getUrl())
                .append("description", e.getDescription())
                .append("feed_id", feedId)
        ).collect(Collectors.toList()));
    }
    
}