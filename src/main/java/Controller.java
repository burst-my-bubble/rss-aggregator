import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import java.util.Iterator;
import java.util.List;

import org.bson.Document;
import com.mongodb.MongoCredential;

public class Controller {

      private static final List<String> newsSources =
      List.of("http://feeds.bbci.co.uk/news/rss.xml",
      "https://www.dailymail.co.uk/articles.rss",
      "https://www.buzzfeed.com/index.xml", "https://www.theguardian.com/uk/rss",
      "https://9to5mac.com/feed/", "https://www.thesun.co.uk/feed/",
      "https://www.telegraph.co.uk/rss.xml",
      "https://www.huffingtonpost.co.uk/feeds/index.xml",
      "http://www.pinknews.co.uk/feed/");
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
    MongoCollection<Document> collection = database.getCollection("feeds");
    FindIterable<Document> iterDoc = collection.find();
    boolean usingDatabase = true;
    // Getting the iterator
    Iterator it = iterDoc.iterator();
    if (usingDatabase) {
    while (it.hasNext()) {
      Document d = (Document) it.next();
      String k = (String) d.get("url");
      List<Document> articles = FeedReader.getArticles(k);
      FeedReader.processArticles(articles);
      collection.insertMany(articles);
      //System.out.println(k);
    }

    } else {
    for (String url : newsSources) {
      List<Document> articles = FeedReader.getArticles(url);
      FeedReader.processArticles(articles);
      collection.insertMany(articles);
    }

    }
  }
}
