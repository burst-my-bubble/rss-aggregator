import java.util.ArrayList;
import java.util.List;

public class Pages {
	public List<Page> documents;

	public Pages() {
		this.documents = new ArrayList<Page>();
	}
	public void add(String id, String language, String text) {
	    this.documents.add (new Page (id, language, text));
	}
}