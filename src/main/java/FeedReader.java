
import java.net.URL;

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

        public static void main(String[] args) {
            boolean ok = false;
            if (args.length==1) {
                try {
                    URL feedUrl = new URL(args[0]);

                    SyndFeedInput input = new SyndFeedInput();
                    SyndFeed feed = input.build(new XmlReader(feedUrl));


                    ok = true;
                    for (SyndEntry entry : feed.getEntries()) {
                        System.out.println(entry.getUri());
                    }
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                    System.out.println("ERROR: "+ex.getMessage());
                }
            }

            if (!ok) {
                System.out.println();
                System.out.println("FeedReader reads and prints any RSS/Atom feed type.");
                System.out.println("The first parameter must be the URL of the feed to read.");
                System.out.println();
            }
        }

    }
