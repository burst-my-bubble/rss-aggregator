import java.util.ArrayList;
import java.util.List;

/**
 * Stores a list of pages which can be put in a request to the Azure API.
 */
public class Pages {
	public List<Page> documents;

	public Pages() {
		this.documents = new ArrayList<Page>();
	}
	public void add(String id, String language, String text) {
	    this.documents.add (new Page (id, language, text));
	}
}