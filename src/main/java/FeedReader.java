

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.ReplaceOptions;
import com.mongodb.client.model.UpdateOptions;
import java.net.URL;

import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import java.util.stream.Collectors;
import org.bson.Document;
import org.bson.types.ObjectId;

/**
     * It Reads and prints any RSS/Atom feed type.
     * <p>
     * @author Alejandro Abdelnur
     *
     */
    public class FeedReader {

        /*private static final List<String> newsSources = List.of("http://feeds.bbci.co.uk/news/rss.xml",
                "http://rss.cnn.com/rss/edition.rss",
                "https://www.dailymail.co.uk/articles.rss",
                "https://www.buzzfeed.com/index.xml",
                "https://www.theguardian.com/uk/rss",
                "https://9to5mac.com/feed/",
                "https://www.thesun.co.uk/feed/",
                "https://www.telegraph.co.uk/rss.xml",
                "https://www.huffingtonpost.co.uk/feeds/index.xml",
                "http://www.pinknews.co.uk/feed/");*/
        private static final ReplaceOptions REPLACE_OPTIONS
          = ReplaceOptions.createReplaceOptions(new UpdateOptions().upsert(true));

        public static void getUri(String url,
            ObjectId id,
            MongoCollection<Document> articles) {
            try {
                // Replace with getting urls from db
                URL feedUrl = new URL(url);

                SyndFeedInput input = new SyndFeedInput();
                SyndFeed feed = input.build(new XmlReader(feedUrl));

                feed.getEntries().stream().forEach(e ->
                    articles.replaceOne(
                        new Document("url", e.getUri()),
                        new Document("title", e.getTitle())
                       .append("description", e.getDescription().getValue())
                       .append("url", e.getUri())
                       .append("published_date", e.getPublishedDate())
                       .append("author", e.getAuthors().stream().map(f -> f.getName()).collect(Collectors.toList()))
                       .append("feed_id", id), REPLACE_OPTIONS)
                );
            }
            catch (Exception ex) {
                ex.printStackTrace();
                System.out.println("ERROR: "+ex.getMessage());
            }
        }



       /* public static void main(String[] args) {
            ExecutorService executorService = Executors.newFixedThreadPool(4);
            for (String sourceUrl : newsSources) {
                executorService.submit(() -> getUri(sourceUrl));
            }

        }*/

    }
