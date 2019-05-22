import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import java.util.Iterator;
import org.bson.Document;
import com.mongodb.MongoCredential;

public class Controller {
  public static void main(String[] args) {
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
    MongoCollection<Document> collection = database.getCollection("rssFeeds");
    FindIterable<Document> iterDoc = collection.find();
    int i = 1;

    // Getting the iterator
    Iterator it = iterDoc.iterator();

    while (it.hasNext()) {
      Document d = (Document) it.next();
      String k = (String) d.get("url");
      collection.insertMany(FeedReader.getUri(k));
      System.out.println(k);
      i++;
    }
  }
}
