import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;

/**
 * An article reader that uses Rome to retrieve the articles.
 */
public class RomeArticleReader implements ArticleReader {

    /**
     * {@inheritDoc}
     * @param url is the url of the news source.
     * @return a list of all articles at that news source.
     */
    @Override
    public List<Article> getArticles(String url) {
        List<String> articles = new ArrayList<>();
        try {
            URL feedUrl = new URL(url);
            SyndFeedInput input = new SyndFeedInput();
            SyndFeed feed = input.build(new XmlReader(feedUrl));

            return feed.getEntries().stream()
                .map(e -> {
                    List<String> authors = e.getAuthors().stream().map(f -> f.getName()).collect(Collectors.toList());
                    return new Article(e.getTitle(), e.getDescription().getValue()
                        , e.getUri(), authors, e.getPublishedDate());
                }).collect(Collectors.toList());
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("ERROR: " + ex.getMessage() + "Url:" + url);
            throw new RuntimeException(ex);
        }
    }

}