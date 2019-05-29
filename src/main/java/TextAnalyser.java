/**
 * A basic text analyser that uses pages.
 */
public interface TextAnalyser {

    /**
     * Gets the sentiment values for all of the pages.
     * @param pages is a list of pages (type Page), each containing some text.
     * @return a string (JSON recommended) containing the sentiment values for
     * each page.
     */
    String getSentiment(Pages pages);

    /**
     * Gets the entities for all of the pages.
     * @param pages is a list of pages (type Page), each containing some text.
     * @return a string (JSON recommended) containing the entities for
     * each page.
     */
    String getEntities(Pages pages);
}