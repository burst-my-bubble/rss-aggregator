import java.util.Date;
import java.util.List;

public class Article {
    
    private String title;
    private List<String> author;
    private Date publishedDate;
    private float sentiment;
    private String entities;
    
    
    public Article(String title, String description, String url, List<String> author, Date publishedDate) {
        this.title = title;
        this.author = author;
        this.publishedDate = publishedDate;
    }

    public void addSentiment(float sentiment) {
        this.sentiment = sentiment;
    }

    public void addEntities(String entities) {
        this.entities = entities;
    }
}