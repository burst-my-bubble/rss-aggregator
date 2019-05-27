import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import com.mongodb.client.model.ReplaceOptions;
import com.mongodb.client.model.UpdateOptions;
import java.util.Iterator;

import java.util.List;
import org.bson.Document;
import org.bson.types.ObjectId;

public class Controller {
  private static final ReplaceOptions REPLACE_OPTIONS
        = ReplaceOptions.createReplaceOptions(new UpdateOptions().upsert(true));

  public static void main(String[] args) throws Exception {
    // Creating a Mongo client
    MongoClient mongo = MongoClients.create();

    // Creating Credentials
    /*MongoCredential credential;
    credential = MongoCredential.createCredential("sampleUser", "myDb",
        "password".toCharArray());
    System.out.println("Connected to the database successfully");*/

    // Accessing the database
    MongoDatabase database = mongo.getDatabase("burstMyBubble");

    // Creating a collection
    //System.out.println("Collection created successfully");

    // Retieving a collection
    MongoCollection<Document> articles = database.getCollection("articles");
    MongoCollection<Document> feeds = database.getCollection("feeds");
    FindIterable<Document> iterDoc = feeds.find();
    int i = 1;

    Iterator it = iterDoc.iterator();
      while (it.hasNext()) {
        Document d = (Document) it.next();
        System.out.println(d);
        String k = (String) d.get("url");
        List<Document> articlesList = FeedReader.getArticles((ObjectId) d.get("_id"), k);
        FeedReader.processArticles(articlesList);
        articlesList.forEach(e ->
          articles.replaceOne(new Document("url", e.get("url")), e, REPLACE_OPTIONS)
        );
        System.out.println(k);
        i++;
      }
    }
  }
