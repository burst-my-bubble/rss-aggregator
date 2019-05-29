import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;

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