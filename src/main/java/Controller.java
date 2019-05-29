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

import java.util.List;
import java.util.stream.Collectors;


/**
 * Controller oversees the process of aggregating and storing the articles.
 */
public class Controller {

  /**
   * Converts a list of articles to a list of pages containing their plaintext.
   * @param articles is a list of articles, each containing a url to a news article.
   * @return a list of pages where each page contains the plaintext corresponding
   * to one of the articles.
   */
  private static Pages convertArticlesToPages(List<Article> articles) throws IOException, BoilerpipeProcessingException {
    Pages pages = new Pages();
    for (int i = 0; i < articles.size(); i++) {
      Article article = articles.get(i);
      String html = getHTML(article.getUrl());
      String imageUrl = getImage(html);
      String mainText = getPlainText(html);
      pages.add(Integer.toString(i), "en", mainText);
    }
    return pages;
  }

  /**
   * Goes through each of the feeds, extracting and processing all of the articles,
   * storing them in a persistent storage.
   * @param storage is the destination for the articles to be put in
   * @param reader gets the articles from the feed
   * @param analyser process the articles to get the required data
   * @throws IOException thrown if there's a connection failure with the URL
   * @throws BoilerpipeProcessingException is thrown if there's an issue with 
   * getting the plaintext from the html
   * */
  public static void aggregateArticles(PersistentStorage storage, ArticleReader reader, TextAnalyser analyser)
      throws IOException, BoilerpipeProcessingException {
    List<Pair<String, Object>> feeds = storage.getFeeds();
    for (Pair<String, Object> feed: feeds) {
      List<Article> articles = reader.getArticles(feed.getFirst());
      List<Article> toBeInserted = articles.stream()
          .filter(a -> !storage.urlExists(a.getUrl()))
          .collect(Collectors.toList());

      Pages pages = convertArticlesToPages(toBeInserted);
      //DO SNETIMENT AND ENTITY STUFF
      //String entities = conn.getEntities(pages);
      //String sentiment = conn.getSentiment(pages);
      
      storage.insertArticles(toBeInserted, feed.getSecond());
    }

  }

  public static void main(String[] args) throws Exception {
    PersistentStorage storage = new MongoPersistentStorage();
    ArticleReader reader = new RomeArticleReader();
    TextAnalyser analyser = new AzureConnection(AzureConnection.getKey());
    aggregateArticles(storage, reader, analyser);
  }

  /**
     * Analyses all the given articles, updating their entries in the DB with their
     * keyphrases, entities and sentiment.
     * @param articles the list of Mongo docs which haven't been analysed
     * @return a list of updated Mongo documents
     */

  /**
   * Extracts the html body for a document at that document's given URL.
   * @param doc the news article that you want to get the HTML of
   * @return the HTML of the document
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
   * @return the plain text of the document
   */
  public static String getPlainText(String text) throws BoilerpipeProcessingException, IOException {
    return ArticleExtractor.INSTANCE.getText(text);
  }

  public static String getImage(String html) {
    Document doc = Jsoup.parse(html);
    Elements els = doc.select("head").select("meta[property=\"og:image\"]");
    Element el = els.first();
    return el == null ? null : el.attr("content");
  }
}
