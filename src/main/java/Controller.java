import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import de.l3s.boilerpipe.BoilerpipeProcessingException;
import de.l3s.boilerpipe.extractors.ArticleExtractor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


/**
 * Controller oversees the process of aggregating and storing the articles.
 */
public class Controller {

  /**
   * Extracts the html body for a document at that document's given URL.
   * @param doc the news article that you want to get the HTML of.
   * @return the HTML of the document.
   */
  public static String getHTML(String urlAsString) throws IOException {
    URL url = new URL(urlAsString);
    BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
    String inputLine;
    StringBuilder htmlText = new StringBuilder();
    while ((inputLine = in.readLine()) != null)
      htmlText.append(inputLine);
    in.close();
    return htmlText.toString();
  }

  /**
   * Extracts the plain text from a document.
   * @param doc the document that you want to extract the plain text from.
   * @return the plain text of the document.
   */
  public static String getPlainText(String text) throws IOException {
    String result = "";
    try {
      result = ArticleExtractor.INSTANCE.getText(text);
    } catch (BoilerpipeProcessingException e) {
      e.printStackTrace();
    }
    return result;
  }

  /**
   * Gets the main image of a news article from its html.
   * @param article is the article that you want the image of.
   * @return the url to the image.
   */
  public static String getImage(Article article) {
    String html = "";
    try {
      html = getHTML(article.getUrl());
    } catch (IOException e) {
      e.printStackTrace();
    }
    Document doc = Jsoup.parse(html);
    Elements els = doc.select("head").select("meta[property=\"og:image\"]");
    Element el = els.first();
    return el == null ? null : el.attr("content");
  }

  public static void addImageUrls(List<Article> articles) {
    for (Article article : articles) {
      article.addImage(getImage(article));
    }
  }

  /**
   * Converts a list of articles to a list of pages containing their plaintext.
   * @param articles is a list of articles, each containing a url to a news article.
   * @return a list of pages where each page contains the plaintext corresponding
   * to one of the articles.
   */
  static Pages convertArticlesToPages(List<Article> articles) throws IOException {
    Pages pages = new Pages();
    for (int i = 0; i < articles.size(); i++) {
      Article article = articles.get(i);
      String html = getHTML(article.getUrl());
      String plainText = getPlainText(html);
      String mainText = plainText.substring(0, Math.min(plainText.length(), 5119));
      pages.add(Integer.toString(i), "en", mainText);
    }
    return pages;
  }


  /**
   * Adds the respective sentiment values and entities to each article.
   * @param sentimentAsString is a JSON string containing all of the sentiment
   * values.
   * @param entitiesAsString is a JSON string containing all of the entities
   * and their types.
   * @param articles is the list of articles which don't have any sentiment or
   * entities yet.
   */
  private static void processSentimentAndEntities(String sentimentAsString, String entitiesAsString, List<Article> articles) {
    JsonParser parser = new JsonParser();
    JsonObject json = parser.parse(entitiesAsString).getAsJsonObject();
    JsonArray docs = json.getAsJsonArray("documents");
    JsonObject jsonSentiment = parser.parse(sentimentAsString).getAsJsonObject();
    JsonArray docsSentiment = jsonSentiment.getAsJsonArray("documents");

    if (docsSentiment != null) {
      for (JsonElement el: docsSentiment) {
        JsonObject obj = el.getAsJsonObject();
        int index = obj.get("id").getAsInt();
        articles.get(index).addSentiment(obj.get("score").getAsFloat());
      }
    }
    if (docs != null) {
      //ASSUMES THAT THEY ARE ORDERED
      for (JsonElement el: docs) {
        JsonObject obj = el.getAsJsonObject();
        int index = obj.get("id").getAsInt();
        JsonArray entities = obj.getAsJsonArray("entities");
        List<Pair<String, String>> articleEntities = new ArrayList<>();
        int j = 0;
        for (JsonElement el2: entities) {
            if (j > 5) {
              break;
            }
            JsonObject obj2 = el2.getAsJsonObject();
            System.out.println(obj2.get("name").getAsString());
            System.out.println(obj2.get("type").getAsString());
            articleEntities.add(new Pair(obj2.get("name").getAsString(), obj2.get("type").getAsString()));
            j++;
        }
        articles.get(index).addEntities(articleEntities);
      }
    }
  }

  /**
   * Goes through each of the feeds, extracting and processing all of the
   * articles, storing them in a persistent storage.
   * @param storage  is the destination for the articles to be put in.
   * @param reader   gets the articles from the feed.
   * @param analyser process the articles to get the required data.
   * @throws IOException if there's an error with the connection for getting the 
   * articles.
   */
  public static void aggregateArticles(PersistentStorage storage, ArticleReader reader, TextAnalyser analyser)
      throws IOException
      {
    List<Pair<String, Object>> feeds = storage.getFeeds();
    /*for (Pair<String, Object> feed: feeds) {
      List<Article> articles = reader.getArticles(feed.getFirst());
      List<Article> toBeInserted = articles.stream()
          .filter(a -> !storage.urlExists(a.getUrl()))
          .collect(Collectors.toList());

      if(!toBeInserted.isEmpty()) {
        addImageUrls(toBeInserted);
        Pages pages = convertArticlesToPages(toBeInserted);
        String entities = analyser.getEntities(pages);
        String sentiment = analyser.getSentiment(pages);
        processSentimentAndEntities(sentiment, entities, toBeInserted);
    
        storage.insertArticles(toBeInserted, feed.getSecond());
      }
    }*/
    
      List<Article> articles = reader.getArticles(feeds.get(0).getFirst());
      System.out.println(articles.size());
      List<Article> toBeInserted = articles.stream()
          .filter(a -> !storage.urlExists(a.getUrl()))
          .limit(1)
          .collect(Collectors.toList());
      if(!toBeInserted.isEmpty()) {
        addImageUrls(toBeInserted);
        Pages pages = convertArticlesToPages(toBeInserted);
        String entities = analyser.getEntities(pages);
        String sentiment = analyser.getSentiment(pages);
        processSentimentAndEntities(sentiment, entities, toBeInserted);
        
        storage.insertArticles(toBeInserted, feeds.get(0).getSecond());
      
      }
      
  }

  public static void main(String[] args) throws Exception {
    PersistentStorage storage = new MongoPersistentStorage();
    ArticleReader reader = new RomeArticleReader();
    TextAnalyser analyser = new AzureConnection(AzureConnection.getKey());
    aggregateArticles(storage, reader, analyser);
  }
}
