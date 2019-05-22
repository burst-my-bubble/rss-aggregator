
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.*;

import com.rometools.rome.feed.synd.SyndContent;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;

    /**
     * It Reads and prints any RSS/Atom feed type.
     * <p>
     * @author Alejandro Abdelnur
     *
     */
    public class FeedReader {

        private static final List<String> newsSources = List.of("http://feeds.bbci.co.uk/news/rss.xml",
                "http://rss.cnn.com/rss/edition.rss",
                "https://www.dailymail.co.uk/articles.rss",
                "https://www.buzzfeed.com/index.xml",
                "https://www.theguardian.com/uk/rss",
                "https://9to5mac.com/feed/",
                "https://www.thesun.co.uk/feed/",
                "https://www.telegraph.co.uk/rss.xml",
                "https://www.huffingtonpost.co.uk/feeds/index.xml",
                "http://www.pinknews.co.uk/feed/");

        private static void getUri(String url) {
            List<String> articles = new ArrayList<>();
            try {
                // Replace with getting urls from db
                URL feedUrl = new URL(url);

                SyndFeedInput input = new SyndFeedInput();
                SyndFeed feed = input.build(new XmlReader(feedUrl));

                for (SyndEntry entry : feed.getEntries()) {
                    String uri = entry.getUri();
                    String description = entry.getDescription().getValue();
                    String title = entry.getTitle();
                    Date publishedDate = entry.getPublishedDate();
                    //Add to db
                }
            }
            catch (Exception ex) {
                ex.printStackTrace();
                System.out.println("ERROR: "+ex.getMessage());
            }
        }



        public static void main(String[] args) {
            ExecutorService executorService = Executors.newFixedThreadPool(4);
            for (String sourceUrl : newsSources) {
                executorService.submit(() -> getUri(sourceUrl));
            }

        }

    }
