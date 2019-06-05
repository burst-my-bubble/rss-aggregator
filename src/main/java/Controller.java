import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import de.l3s.boilerpipe.BoilerpipeProcessingException;
import de.l3s.boilerpipe.extractors.ArticleExtractor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Iterator;
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

  final static int MAX_NAME_LENGTH = 20;

  private static BufferedReader getHTMLBufferedReader(String urlAsString) throws IOException {
    URL url = new URL(urlAsString);
    HttpURLConnection con = (HttpURLConnection) url.openConnection();
    con.addRequestProperty("User-Agent","Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:56.0) Gecko/20100101 Firefox/56.0");
    if (con.getResponseCode() % 300 < 100) {
      con.setInstanceFollowRedirects(false);
      URL secondUrl = new URL(con.getHeaderField("Location"));
      URLConnection con2 = secondUrl.openConnection();
      return new BufferedReader(new InputStreamReader(con2.getInputStream()));
    }
    return new BufferedReader(new InputStreamReader(con.getInputStream()));
  }

  /**
   * Extracts the html body for a document at that document's given URL.
   * 
   * @param doc the news article that you want to get the HTML of.
   * @return the HTML of the document.
   */
  public static String getHTML(String urlAsString) throws IOException {
    BufferedReader in = getHTMLBufferedReader(urlAsString);
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
  public static boolean getImage(Article article) {
    String html = "";
    try {
      html = getHTML(article.getUrl());
      article.setPlainText(getPlainText(html));
      Document doc = Jsoup.parse(html);
      Elements els = doc.select("head").select("meta[property=\"og:image\"]");
      Element el = els.first();
      article.addImage(el == null ? null : el.attr("content"));
      return true;
    } catch (Exception e) {
      e.printStackTrace();
      System.out.println("Error on image for " + article.getFeed().getFirst());
      return false;
    }
  }

  public static void addImageUrls(List<Article> articles) {

    Iterator<Article> it = articles.iterator();

    while(it.hasNext()){
      if(!getImage(it.next())){
        it.remove();
      }
    }
  }

  /**
   * Converts a list of articles to a list of pages containing their plaintext.
   * @param articles is a list of articles, each containing a url to a news article.
   * @return a list of pages where each page contains the plaintext corresponding
   * to one of the articles.
   */
  static Pages convertArticlesToPages(List<Article> articles, int startVal) throws IOException {
    Pages pages = new Pages();
    for (int i = 0; i < articles.size(); i++) {
      Article article = articles.get(i);
      String plainText = article.getPlainText();
      String mainText = plainText.substring(0, Math.min(plainText.length(), 5119));
      pages.add(Integer.toString(i + startVal), "en", mainText);
    }
    return pages;
  }

  /**
   * Used to remove entities if their categories are useless.
   * @param c is the category of the entity that is being checked.
   * @return true if the given category is useless.
   */
  private static boolean isUselessCategory(String c) {
    return c.equals("DateTime") || c.equals("Quantity") || c.equals("URL") || c.equals("Email");
  }

  /**
   * Removes brackets and anything inside the brackets from a word.
   * @param word is the string to be processed.
   * @return the modified string.
   */
  private static String removeBrackets(String word) {
    for (int i = 0; i < word.length(); i++) {
      if (word.charAt(i) == '(') {
        return word.substring(0, i);
      }
    }
    return word;
  }

  /**
   * Gets an acronym from a string, comprising of its upper case letters.
   * @param name the string that's acronym is to be calculateed.
   * @return the acronym.
   */
  private static String getAcronym(String name) {
    name = removeBrackets(name);
    String acronym = "";
    for (int i = 0; i < name.length(); i++) {
      if (Character.isUpperCase(name.charAt(i))) {
        acronym += name.charAt(i);
      }
    }
    return acronym;
  }

  /**
   * Removes brackets from the entity name and turns it into an acronym if it's
   * an organization and the name is too long.
   * @param e is the entity to be shortened.
   * @return the modified entity.
   */
  private static Entity shortenOrgName(Entity e) {
    e = new Entity(removeBrackets(e.getActualName()), e.getCategory());
    if (e.isOrganization() && e.getActualName().length() > MAX_NAME_LENGTH) {
      e.setDisplayName(getAcronym(e.getActualName()));
    }
    return e;
  }

  private static boolean isSuitableOtherEntity(Entity e) {
    return (e.isOther() && e.getActualName().length() < 5);
  }

  /**
   * Runs various functions on the list of entities to remove unnecessary ones 
   * and to tidy the names.
   * @param entities is the list of unmodified entities.
   * @return the list of modified entities.
   */
  private static List<Entity> cleanEntities(List<Entity> entities) {
    entities.removeIf(e -> isUselessCategory(e.getCategory()));
    entities.removeIf(e -> isSuitableOtherEntity(e));
    return entities.stream().map(e -> shortenOrgName(e)).collect(Collectors.toList());
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
        List<Entity> articleEntities = new ArrayList<>();
        for (JsonElement el2: entities) {
            JsonObject obj2 = el2.getAsJsonObject();
            articleEntities.add(new Entity(obj2.get("name").getAsString(), obj2.get("type").getAsString()));
        }
        articleEntities = cleanEntities(articleEntities);
        System.out.println(articleEntities);
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
    List<Article> toBeInserted = new ArrayList();
    for (Pair<String, Object> feed: feeds) {
      List<Article> articles = reader.getArticles(feed.getFirst());
      List<Article> newArticles = articles.stream()
          .filter(a -> !storage.urlExists(a.getUrl()))
          .limit(10)
          .collect(Collectors.toList());

      newArticles.forEach(a -> a.setFeed(feed));
      addImageUrls(newArticles);
      toBeInserted.addAll(newArticles);
      System.out.println("Reading from feed " + feed.getFirst() + ". There are " + newArticles.size() + " articles.");
    }

    if(!toBeInserted.isEmpty()) {
      //addImageUrls(toBeInserted);
      List<Pages> pageList = new ArrayList<>();
      int i = 0;
      while(i < toBeInserted.size()){
        int end = Math.min(i + 100, toBeInserted.size());
        pageList.add(convertArticlesToPages(toBeInserted.subList(i, end), i));
        i = i + 100;
      }

      //Pages pages = convertArticlesToPages(toBeInserted);


      for(Pages p : pageList) {
        String entities = analyser.getEntities(p);
        String sentiment = analyser.getSentiment(p);
        processSentimentAndEntities(sentiment, entities, toBeInserted);
      }

  
      storage.insertArticles(toBeInserted);
    }
   /* 
      List<Article> articles = reader.getArticles(feeds.get(7).getFirst());
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
*/      
  }

  public static void main(String[] args) throws Exception {
    System.setProperty("http.agent", "");
    PersistentStorage storage = new MongoPersistentStorage();
    ArticleReader reader = new RomeArticleReader();
    TextAnalyser analyser = new AzureConnection("99a745e70285402385155c3aec4fba97");
    aggregateArticles(storage, reader, analyser);
  }
}
