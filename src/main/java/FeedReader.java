
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
                " http://www.pinknews.co.uk/feed/");

        private static List<String> getUri(String url) {
            List<String> articles = new ArrayList<>();
            try {
                URL feedUrl = new URL(url);

                SyndFeedInput input = new SyndFeedInput();
                SyndFeed feed = input.build(new XmlReader(feedUrl));

                for (SyndEntry entry : feed.getEntries()) {
                    articles.add(entry.getUri());
                }
            }
            catch (Exception ex) {
                ex.printStackTrace();
                System.out.println("ERROR: "+ex.getMessage());
            }
            return articles;
        }

        public static void main(String[] args) {
            HashMap<String, List<String>> articles = new HashMap<>();
            for (String sourceUrl : newsSources) {
                articles.put(sourceUrl, getUri(sourceUrl));
            }
            System.out.println(articles.get("https://www.thesun.co.uk/feed/"));
        }

    }
