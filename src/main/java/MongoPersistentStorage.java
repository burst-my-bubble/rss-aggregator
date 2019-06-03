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
    private final MongoCollection<Document> entities;


    public MongoPersistentStorage() {
        String databasePath = System.getenv("DATABASE_URI");
        if (databasePath == null) {
            databasePath = "localhost";
        }
        MongoClient client = MongoClients.create("mongodb://"+databasePath);
        MongoDatabase database = client.getDatabase("burstMyBubble");
        this.articles = database.getCollection("articles");
        this.feeds = database.getCollection("feeds");
        this.entities = database.getCollection("entities");
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
     * Inserts a list of entities into the entities table, updating their score.
     * @param docs is a list of Mongo documents representing the entities.
     */
    private void insertEntities(List<Document> docs) {
        for (Document entity : docs) {
            if (entities.find(entity).first() == null) {
               Document newEntity = entity;
               newEntity.append("score", 0);
               entities.insertOne(newEntity); 
            } else {
                entities.findOneAndUpdate(new Document("name", entity.get("name")).append("category", entity.get("category")),
                    new Document("$inc", new Document("score", 1)));
            }
        }
    }

    /**
     * {@inheritDoc}
     * @param articlesToBeInserted is the list of articles to be inserted into
     * the Mongo storage.
     * @param feedId is the id of the news source.
     */
    @Override
    public void insertArticles(List<Article> articlesToBeInserted, Object feedId) {
        System.out.println("gonna do some inserts");
        articles.insertMany(articlesToBeInserted.stream().map(e -> {
            List<Document> docs = e.getEntities().stream().map(entity -> {
                    return new Document()
                        .append("name", entity.getActualName())
                        .append("displayName", entity.getDisplayName())
                        .append("category", entity.getCategory());
            }).collect(Collectors.toList());
            insertEntities(docs);
            return new Document()
                .append("title", e.getTitle())
                .append("author", e.getAuthor())
                .append("published_date", e.getPublishedDate())
                .append("url", e.getUrl())
                .append("description", e.getDescription())
                .append("feed_id", feedId)
                .append("image_url", e.getImage())
                .append("entities", docs)
                .append("sentiment", e.getSentiment());
        }).collect(Collectors.toList()));
    }
}