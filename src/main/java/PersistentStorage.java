import java.util.List;

public interface PersistentStorage {
    public List<Pair<String, Object>> getFeeds();
    public boolean urlExists(String url);
    public void insertArticles(List<Article> article, Object feedId);
}