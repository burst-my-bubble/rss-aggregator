
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.jupiter.api.Test;
import static org.hamcrest.Matchers.greaterThan;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;



class AzureConnectionTest {

    TextAnalyser analyser = new AzureConnection(AzureConnection.getKey());

    @Test
    public void checkNeutralArticleIsGivenCorrectSentiment() throws IOException {
        Pages pages = new Pages();
        pages.add("1", "en", "There are 8 planets in the solar system");
        String sentiment = analyser.getSentiment(pages);
        JsonParser parser = new JsonParser();
        JsonObject jsonSentiment = parser.parse(sentiment).getAsJsonObject();
        JsonArray docsSentiment = jsonSentiment.getAsJsonArray("documents");
        for (JsonElement el: docsSentiment) {
            JsonObject obj = el.getAsJsonObject();
            float sent = obj.get("score").getAsFloat();
            assertTrue(sent > 0.25);
            assertTrue(sent < 0.75);
        }
    
    }

    @Test
    public void checkNegativeArticleIsGivenCorrectSentiment() throws IOException {
        Pages pages = new Pages();
        pages.add("1", "en", "I hate everything and everybody. I am very upset.");
        String sentiment = analyser.getSentiment(pages);
        JsonParser parser = new JsonParser();
        JsonObject jsonSentiment = parser.parse(sentiment).getAsJsonObject();
        JsonArray docsSentiment = jsonSentiment.getAsJsonArray("documents");
        for (JsonElement el: docsSentiment) {
            JsonObject obj = el.getAsJsonObject();
            float sent = obj.get("score").getAsFloat();
            assertTrue(sent < 0.25);
        }
    
    }

    @Test
    public void checkPositiveArticleIsGivenCorrectSentiment() throws IOException {
        Pages pages = new Pages();
        pages.add("1", "en", "I love rainbows and stars and am a very happy boy.");
        String sentiment = analyser.getSentiment(pages);
        JsonParser parser = new JsonParser();
        JsonObject jsonSentiment = parser.parse(sentiment).getAsJsonObject();
        JsonArray docsSentiment = jsonSentiment.getAsJsonArray("documents");
        for (JsonElement el: docsSentiment) {
            JsonObject obj = el.getAsJsonObject();
            float sent = obj.get("score").getAsFloat();
            assertTrue(sent > 0.75);
        }
    
    }

}