import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import java.util.Iterator;
import org.bson.Document;
import org.bson.types.ObjectId;

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
    MongoCollection<Document> articles = database.getCollection("articles");
    MongoCollection<Document> feeds = database.getCollection("feeds");
    FindIterable<Document> iterDoc = feeds.find();
    int i = 1;

    // Getting the iterator
    Iterator it = iterDoc.iterator();

    while (it.hasNext()) {
      Document d = (Document) it.next();
      System.out.println(d);
      String k = (String) d.get("url");
      FeedReader.getUri(k, (ObjectId) d.get("_id"), articles);
      System.out.println(k);
      i++;
    }
  }
}
