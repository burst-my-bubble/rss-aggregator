import java.util.List;

/**
 * An interface for storage that can store articles from news feeds.
 */
public interface PersistentStorage {
    
    /**
     * Gets the news sources from the database.
     * @return a list of pairs consisting of the feed's URL and its id.
     */
    public List<Pair<String, Object>> getFeeds();
    
    /**
     * Checks whether the given url of an article already exists in the storage.
     * @param url the url of the article.
     * @return true if the url is in the arcle, false otherwise.
     */
    public boolean urlExists(String url);
    
    /**
     * Inserts a list of articles into the storage, noting which feed they came
     * from.
     * @param articles is the list of articles to be inserted.
     * @param feedId is the id of the news source/feed.
     */
    public void insertArticles(List<Article> articles, Object feedId);
}