import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.net.ssl.HttpsURLConnection;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * Manges the connection to Azure and handles the requests to it.
 */
public class AzureConnection implements TextAnalyser {

    private final String accessKey;
    static String host = "https://uksouth.api.cognitive.microsoft.com";

    public AzureConnection(String accessKey) {
        this.accessKey = accessKey;
    }

    /**
     * Gets the required key from your local storage.
     * @return the API key as a string
     */
    public static String getKey() {
        Path keypath = Paths.get("./", "api-key");
        String key = "";
        try {
            key = Files.readAllLines(keypath).get(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return key;
    }

    /**
     * Handles a generic request to the Azure API and getting its response.
     * 
     * @param path  is specific to the Text Analytics function that you want to use
     *              e.g. sentiment analysis, entity recognition
     * @param pages are used by the Azure API to wrap the information for the
     *              request
     * @return the JSON response from Azure API as a string
     * @throws Exception if there's connection issue
     */
    public String request(String path, Pages pages) throws Exception {
        String text = new Gson().toJson(pages);
        byte[] encoded_text = text.getBytes("UTF-8");

        URL url = new URL(host + path);
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "text/json");
        connection.setRequestProperty("Ocp-Apim-Subscription-Key", accessKey);
        connection.setDoOutput(true);

        DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
        wr.write(encoded_text, 0, encoded_text.length);
        wr.flush();
        wr.close();

        StringBuilder response = new StringBuilder();
        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String line;
        while ((line = in.readLine()) != null) {
            response.append(line);
        }
        in.close();

        return response.toString();
    }

    /**
     * {@inheritDoc}
     * @param pages wrap the text to be analysed in a format that Azure API
     * understands.
     * @return a JSON string containing the sentiment values of all pages.
     */
    public String getSentiment(Pages pages) {
        String result = "";
        try {
            result = request("/text/analytics/v2.1/sentiment", pages);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * {@inheritDoc}
     * @param pages wrap the text to be analysed in a format that Azure API
     * understands.
     * @return a JSON string containing the entities of all pages.
     */
    public String getEntities(Pages pages) {
        String result = "";
        try {
            result = request("/text/analytics/v2.1/entities", pages);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String prettify(String json_text) {
		JsonParser parser = new JsonParser();
		JsonObject json = parser.parse(json_text).getAsJsonObject();
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		return gson.toJson(json);
	}
}