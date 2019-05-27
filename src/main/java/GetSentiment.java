import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import javax.net.ssl.HttpsURLConnection;

/*
 * Gson: https://github.com/google/gson
 * Maven info:
 *     groupId: com.google.code.gson
 *     artifactId: gson
 *     version: 2.8.1
 *
 * Once you have compiled or downloaded gson-2.8.1.jar, assuming you have placed it in the
 * same folder as this file (GetSentiment.java), you can compile and run this program at
 * the command line as follows.
 *
 * Execute the following two commands to build and run (change gson version if needed):
 * javac GetSentiment.java -classpath .;gson-2.8.1.jar -encoding UTF-8
 * java -cp .;gson-2.8.1.jar GetSentiment
 */
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class GetSentiment {

    // ***********************************************
    // *** Update or verify the following values. ***
    // **********************************************

    // Replace the accessKey string value with your valid access key.
    private static String getKey() {
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
	static String accessKey = getKey();

// Replace or verify the region.

// You must use the same region in your REST API call as you used to obtain your access keys.
// For example, if you obtained your access keys from the westus region, replace 
// "westcentralus" in the URI below with "westus".

// NOTE: Free trial access keys are generated in the westcentralus region, so if you are using
// a free trial access key, you should not need to change this region.
	static String host = "https://uksouth.api.cognitive.microsoft.com";

	static String path = "/text/analytics/v2.1/sentiment";

	public static String getSinglePageSentiment (Page page) throws Exception {
		Pages pages = new Pages();
		pages.add(page.id, page.language, page.text);
		return getTheSentiment(pages);
    }
    
	public static String getTheSentiment (Pages pages) throws Exception {
		String text = new Gson().toJson(pages);
		byte[] encoded_text = text.getBytes("UTF-8");

		URL url = new URL(host+path);
		HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
		connection.setRequestMethod("POST");
		connection.setRequestProperty("Content-Type", "text/json");
		connection.setRequestProperty("Ocp-Apim-Subscription-Key", accessKey);
		connection.setDoOutput(true);

        DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
		wr.write(encoded_text, 0, encoded_text.length);
		wr.flush();
		wr.close();

		StringBuilder response = new StringBuilder ();
		BufferedReader in = new BufferedReader(
		new InputStreamReader(connection.getInputStream()));
		String line;
		while ((line = in.readLine()) != null) {
			response.append(line);
		}
		in.close();

		return response.toString();
    }

	/*	public static void main(String[] args) throws Exception {
		Page page = new Page("1", "en","I really enjoy the new XBox One S. It has a clean look, it has 4K/HDR resolution and it is affordable.");
		System.out.println(getSinglePageSentiment(page));
	}
*/
/*    private static void getFeedSentiment() {
			Documents documents = new Documents ();
			documents.add ("1", "en","I really enjoy the new XBox One S. It has a clean look, it has 4K/HDR resolution and it is affordable.");
        
    }*/
/*	public static void main (String[] args) {
		try {
			Pages documents = new Pages();
			documents.add ("1", "en","I really enjoy the new XBox One S. It has a clean look, it has 4K/HDR resolution and it is affordable.");

			String response = getTheSentiment (documents);
			System.out.println (prettify (response));
		}
		catch (Exception e) {
			System.out.println (e);
		}
	}*/
}