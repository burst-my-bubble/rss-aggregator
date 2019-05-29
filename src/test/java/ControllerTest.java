import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.jupiter.api.Test;

import de.l3s.boilerpipe.BoilerpipeProcessingException;

class ControllerTest {

  @Test
  public void getPlainTextExtractsPlainTextFromHTML() throws BoilerpipeProcessingException, IOException {
    final String htmlText ="<blockquote class=\"twitter-tweet\" data-conversation=\"none\" " +
      "data-lang=\"en\"><p lang=\"en\" dir=\"ltr\">3. I am and always will be Labour.  But hard " +
      "not to point out difference in the way anti-Semitism cases have been handled.</p>&mdash;" +
      " Alastair PEOPLE’S VOTE Campbell (@campbellclaret) " +
      "<a href=\"https://twitter.com/campbellclaret/status/1133332264589373445?ref_src=twsrc%5Etfw\">" +
      "May 28, 2019</a></blockquote>";
    final String expectedOutput = "3. I am and always will be Labour.  But hard not to point out " + 
      "difference in the way anti-Semitism cases have been handled.\n";

    assertEquals(expectedOutput, Controller.getPlainText(htmlText));
  }

  @Test
  public void getHTMLTextFromURL() throws IOException {
    final String url = "https://example.com";
    String htmlText = Controller.getHTML(url);
    assert(htmlText.contains("This domain is established to be used for illustra"));
  }

  @Rule
  public JUnitRuleMockery context = new JUnitRuleMockery();
  TextAnalyser analyser = context.mock(TextAnalyser.class);
  PersistentStorage storage = context.mock(PersistentStorage.class);
  ArticleReader reader = context.mock(ArticleReader.class);

  @Test
  public void retrievesArticlesAndInsertsIntoDatabase() throws IOException, BoilerpipeProcessingException {
    List<Pair<String, Object>> expectedFeed = new ArrayList();
    List<Article> articles = new ArrayList();
    articles.add(new Article("a", "b", "https://example.com", List.of("a"), new Date()));
    expectedFeed.add(new Pair("https://example.com", null));
    context.checking(new Expectations() {{
      oneOf(storage).getFeeds(); will(returnValue(expectedFeed));
      oneOf(reader).getArticles("https://example.com"); will(returnValue(articles));
      oneOf(storage).urlExists("https://example.com"); will(returnValue(false));
      oneOf(analyser).getEntities(with(any(Pages.class))); will(returnValue("{\"documents\":[{\"id\":\"0\",\"entities\":[{\"name\":\"Example.com\",\"matches\":[{\"wikipediaScore\":0.70300735599858144,\"entityTypeScore\":0.8,\"text\":\"Example Domain\",\"offset\":0,\"length\":14}],\"wikipediaLanguage\":\"en\",\"wikipediaId\":\"Example.com\",\"wikipediaUrl\":\"https://en.wikipedia.org/wiki/Example.com\",\"bingId\":\"02ad04e0-2440-f399-1c14-fb2b8f77645c\",\"type\":\"Other\"}]}],\"errors\":[]}"));
      oneOf(analyser).getSentiment(with(any(Pages.class))); will(returnValue("{\"documents\":[{\"id\":\"0\",\"score\":0.88038372993469238}],\"errors\":[]}"));
      oneOf(analyser).getSentiment(with(any(Pages.class))); will(returnValue("{\"documents\":[{\"id\":\"0\",\"score\":0.88038372993469238}],\"errors\":[]}"));
      oneOf(storage).insertArticles(articles, null);
    }});
    Controller.aggregateArticles(storage, reader, analyser);
  }
  
  @Test
  public void doesntInsertArticleIfAlreadyExists() throws IOException, BoilerpipeProcessingException {
    List<Pair<String, Object>> expectedFeed = new ArrayList();
    List<Article> articles = new ArrayList();
    articles.add(new Article("a", "b", "https://example.com", List.of("a"), new Date()));
    expectedFeed.add(new Pair("https://example.com", null));
    context.checking(new Expectations() {{
      oneOf(storage).getFeeds(); will(returnValue(expectedFeed));
      oneOf(reader).getArticles("https://example.com"); will(returnValue(articles));
      oneOf(storage).urlExists("https://example.com"); will(returnValue(true));
      oneOf(analyser).getEntities(with(any(Pages.class))); will(returnValue("entities"));
      oneOf(analyser).getSentiment(with(any(Pages.class))); will(returnValue("sentiment"));
      oneOf(storage).insertArticles(List.of(), null);
    }});
    Controller.aggregateArticles(storage, reader, analyser);
  }
}