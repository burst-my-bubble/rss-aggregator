import java.util.Date;
import java.util.List;

public class Article {
    
    private String title;
    private List<String> author;
    private Date publishedDate;
    private float sentiment;
    private String entities;
    private String url;
    private String description;
    
    public Article(String ttle, String description, String url, List<String> author, Date publishedDate) {
        this.title = title;
        this.author = author;
        this.url = url;
        this.description = description;
        this.publishedDate = publishedDate;
    }

    public void addSentiment(float sentiment) {
        this.sentiment = sentiment;
    }

    public void addEntities(String entities) {
        this.entities = entities;
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
}