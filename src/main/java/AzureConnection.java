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
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class AzureConnection {

    private final String accessKey;
    static String host = "https://uksouth.api.cognitive.microsoft.com";

    public AzureConnection(String accessKey) {
        this.accessKey = accessKey;
    }

    public static String getKey() {
        Path keypath = Paths.get("/home/hzm17/webapp-project/rss-aggregator", "api-key");
        String key = "";
        try {
            key = Files.readAllLines(keypath).get(0);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return key;
    }

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

    public String getSentiment(Pages pages) throws Exception {
      return request("/text/analytics/v2.1/sentiment", pages);
    }

    public String getEntities(Pages pages) throws Exception {
        return request("/text/analytics/v2.1/entities", pages);
    }

    public String getKeyPhrases(Pages pages) throws Exception {
        return request("/text/analytics/v2.1/keyPhrases", pages);
    }

    public static String prettify(String json_text) {
		JsonParser parser = new JsonParser();
		JsonObject json = parser.parse(json_text).getAsJsonObject();
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		return gson.toJson(json);
	}
	public static void main (String[] args) {
        AzureConnection conn = new AzureConnection(getKey());
		try {
			Pages documents = new Pages ();
			documents.add ("1", "en", "Microsoft is an It company.");

            String response = conn.getEntities(documents);
            JsonParser parser = new JsonParser();
            JsonObject json = parser.parse(response).getAsJsonObject();
            JsonArray docs = json.getAsJsonArray("documents");
            for (JsonElement el: docs) {
                JsonObject obj = el.getAsJsonObject();
                System.out.println(obj.get("id").getAsInt());
                JsonArray entities = obj.getAsJsonArray("entities");
                for (JsonElement el2: entities) {
                    JsonObject obj2 = el2.getAsJsonObject();
                    System.out.println(obj2.get("name").getAsString());
                    System.out.println(obj2.get("type").getAsString());
                }
            }

			System.out.println (prettify (response));
		}
		catch (Exception e) {
			System.out.println (e);
		}
	}
}