import java.util.List;

/**
 * An interface for a basic online article reader.
 */
public interface ArticleReader {

   /**
    * This method gets a list of articles at a given url.
    * @param url the location of the articles.
    * @return the list of articles at that location.
    */
   List<Article> getArticles(String url);
} 