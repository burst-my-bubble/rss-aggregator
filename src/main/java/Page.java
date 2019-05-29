/**
 * A format that the Azure API requires for a request.
 */
class Page {
    public String id, language, text;

    public Page(String id, String language, String text){
        this.id = id;
		this.language = language;
        this.text = text;
    }
}
