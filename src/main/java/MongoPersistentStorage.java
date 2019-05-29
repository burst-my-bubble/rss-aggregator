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

/**
 * A Mongo database to store feeds and articles with their respective data.
 */
public class MongoPersistentStorage implements PersistentStorage {

    private final MongoCollection<Document> articles;
    private final MongoCollection<Document> feeds;


    public MongoPersistentStorage() {
        MongoClient client = MongoClients.create("database");
        MongoDatabase database = client.getDatabase("burstMyBubble");
        this.articles = database.getCollection("articles");
        this.feeds = database.getCollection("feeds");
    }

    /**
     * {@inheritDoc}
     * @param url is the url of an article.
     */
    @Override
    public boolean urlExists(String url) {
        return articles.find(new Document("url", url)).first() != null;
    }

    /**
     * {@inheritDoc}
     */
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
     * {@inheritDoc}
     * @param articlesToBeInserted is the list of articles to be inserted into
     * the Mongo storage.
     * @param feedId is the id of the news source.
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