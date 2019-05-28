import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.bson.Document;
import org.bson.types.ObjectId;

import de.l3s.boilerpipe.BoilerpipeProcessingException;
import de.l3s.boilerpipe.extractors.ArticleExtractor;

public class FeedReader {

    /**
     * Gets a list of all news articles in the RSS feed at a given news site's
     * URL. Each article is also entered into the database.
     * @param feedId
     * @param url the location of the news source
     * @return a list of Mongo documents corresponding to the articles
     */
    public static List<Document> getArticles(ObjectId feedId, String url) {
        List<String> articles = new ArrayList<>();
        try {
            URL feedUrl = new URL(url);

            SyndFeedInput input = new SyndFeedInput();
            SyndFeed feed = input.build(new XmlReader(feedUrl));

            return feed.getEntries().stream()
                    .map(e -> new Document("title", e.getTitle())
                        .append("description", e.getDescription().getValue())
                        .append("feed_id", feedId)
                        .append("url", e.getUri())
                        .append("published_date", e.getPublishedDate())
                        .append("author", e.getAuthors().stream().map(f -> f.getName()).collect(Collectors.toList())))
                    .collect(Collectors.toList());
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("ERROR: " + ex.getMessage() + "Url:" + url);
            throw new RuntimeException(ex);
        }
    }

    /**
     * Extracts the html body for a document at that document's given URL.
     * @param doc the news article that you want to get the HTML of
     * @return the HTML of the document
     */
    private static String getHTML(Document doc) throws IOException {
        URL url = new URL(doc.get("url").toString());
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
    private static String getPlainText(Document doc) throws BoilerpipeProcessingException, IOException {
        return ArticleExtractor.INSTANCE.getText(getHTML(doc));
    }


    /**
     * Updates the database entry for a single article with its sentiment,
     * keyphrases and entities.
     * @param article the Mongo document entry in the DB for that article
     * @param page the Azure API representation of the article
     */
    private static void updateDBWithSentimentAndEntities(Document article, Page page) throws Exception {
        article.append("sentiment", GetSentiment.getSinglePageSentiment(page));
        article.append("keyPhrases", GetKeyPhrases.GetSinglePageKeyPhrases(page));
        article.append("entities", GetEntities.GetSinglePageEntity(page));
    }


    /* Analyses all the articles and stores the key phrases and sentiment value in
            the database */



    /**
     * Analyses all the given articles, updating their entries in the DB with their
     * keyphrases, entities and sentiment.
     * @param articles the list of Mongo docs which haven't been analysed
     * @return a list of updated Mongo documents
     */
    public static List<Document> processArticles(List<Document> articles) throws Exception {
        Pages toBeAnalysed = new Pages();
        IntStream.range(0, articles.size()).forEach(a -> {
            try {
                toBeAnalysed.add(Integer.toString(a), "en", getPlainText(articles.get(a)));
            } catch (BoilerpipeProcessingException | IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        });

        for (int i = 0; i < articles.size(); i++) {
            updateDBWithSentimentAndEntities(articles.get(i), toBeAnalysed.documents.get(i));
        }

        return articles;
    }
}
