
import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.*;

import com.rometools.rome.feed.synd.SyndContent;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.bson.Document;

import de.l3s.boilerpipe.BoilerpipeProcessingException;
import de.l3s.boilerpipe.extractors.ArticleExtractor;

public class FeedReader {

    

    /*
     * Returns a list of all the news articles in the RSS feed at a given news
     * site's URL. Each article is an entry in the database.
     */
    public static List<Document> getArticles(String url) {
        List<String> articles = new ArrayList<>();
        try {
            // Replace with getting urls from db
            URL feedUrl = new URL(url);

            SyndFeedInput input = new SyndFeedInput();
            SyndFeed feed = input.build(new XmlReader(feedUrl));

            return feed.getEntries().stream()
                    .map(e -> new Document("title", e.getTitle()).append("description", e.getDescription().getValue())
                            .append("uri", e.getUri()).append("publishedDate", e.getPublishedDate()).append("author",
                                    e.getAuthors().stream().map(f -> f.getName()).collect(Collectors.toList())))
                    .collect(Collectors.toList());
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("ERROR: " + ex.getMessage() + "Url:" + url);
            throw new RuntimeException(ex);
        }
    }

    /* Extracts html body for a document at a given URI */
    private static String getHTML(Document a) throws IOException {
        URL url = new URL(a.get("uri").toString());
        BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));

        String inputLine;
        StringBuilder htmlText = new StringBuilder();
        while ((inputLine = in.readLine()) != null)
            htmlText.append(inputLine);
        in.close();
        return htmlText.toString();
    }

    /* Extracts plain text from a document */
    private static String getPlainText(Document a) throws BoilerpipeProcessingException, IOException {
        return ArticleExtractor.INSTANCE.getText(getHTML(a));
    }


    private static void updateDBWithSentimentAndEntities(Document article, Page page) throws Exception {
        article.append("sentiment", GetSentiment.getSinglePageSentiment(page));
        article.append("keyPhrases", GetKeyPhrases.GetSinglePageKeyPhrases(page));
        article.append("entities", GetEntities.GetSinglePageEntity(page));
    }


    /* Analyses all the articles and stores the key phrases and sentiment value in
            the database */
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

        System.out.println(articles);
        return articles;
    }


       /* public static void main(String[] args) {
            ExecutorService executorService = Executors.newFixedThreadPool(4);
            for (String sourceUrl : newsSources) {
                executorService.submit(() -> getUri(sourceUrl));
            }

        }*/

    }
