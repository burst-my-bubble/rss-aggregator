import java.util.List;

public interface PersistentStorage {
    public List<Pair<String, String>> getFeeds();
    public void insertArticle(Article article);
}