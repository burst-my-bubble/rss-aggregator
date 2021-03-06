import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * This class is a representation of a news article.
 */
public class Article {
    
    private String title;
    private List<String> author;
    private Date publishedDate;
    private float sentiment;
    private List<Entity> entities = new ArrayList<>();
    private String url;
    private String description;
    private String imageUrl;
    private Pair<String, Object> feed;
    private String plainText;
    
    public Article(String title, String description, String url, List<String> author, Date publishedDate) {
        this.title = title;
        this.author = author;
        this.url = url;
        this.description = description;
        this.publishedDate = publishedDate;
    }

    /**
     * Sentiment is a value calculated by the Azure Text Analytics API of a given
     * article. It used to determine how positive or negative that article is.
     * @param sentiment ranges from 0 (negative) to 1 (positive).
     */
    public void addSentiment(float sentiment) {
        this.sentiment = sentiment;
    }

    /**
     * Entities are determined by the Azure Text Analytics API for each article.
     * Entities are keywords / phrases that are categorised and contain links to more
     * information about them.
     * @param entities is in the format of a JSON document.
     */
    public void addEntities(List<Entity> entities) {
        this.entities = entities;
    }

    public void addImage(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setFeed(Pair<String, Object> feed){
        this.feed = feed;
    }

    public void setPlainText(String plainText){
        this.plainText = plainText;
    }

    public String getPlainText(){
        return plainText;
    }

    public Pair<String, Object> getFeed(){
        return feed;
    }

    public String getTitle() {
        return title;
    }

    public List<String> getAuthor() {
        return author;
    }

    public Date getPublishedDate() {
        return publishedDate;
    }

    public String getUrl() {
        return url;
    }

    public String getDescription() {
        return description;
    }

    public String getImage() {
        return imageUrl;
    }

    public float getSentiment() {
        return sentiment;
    }

    public List<Entity> getEntities() {
        return entities;
    }
}