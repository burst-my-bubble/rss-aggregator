import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import java.net.URL;

import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;

class RomeArticleReaderTest {

    RomeArticleReader articleReader = new RomeArticleReader();

    @Test
    public void returnsSameInformationAsDirectReadFromRSS()
            throws IllegalArgumentException, FeedException, IOException {
    String url = "https://feeds.bbci.co.uk/news/rss.xml";

    Article adapterArticle = articleReader.getArticles(url).get(0);
    URL feedUrl = new URL(url);
    SyndFeedInput input = new SyndFeedInput();
    SyndFeed feed = input.build(new XmlReader(feedUrl));

    SyndEntry thirdPartyArticle = feed.getEntries().get(0);

    assertEquals(thirdPartyArticle.getTitle(), adapterArticle.getTitle());
  }


  @Test
  public void returnsSameSizeListAsDirectRead()
          throws IllegalArgumentException, FeedException, IOException {
  String url = "https://feeds.bbci.co.uk/news/rss.xml";

  int adapterSize = articleReader.getArticles(url).size();
  URL feedUrl = new URL(url);
  SyndFeedInput input = new SyndFeedInput();
  SyndFeed feed = input.build(new XmlReader(feedUrl));

  int thirdPartySize = feed.getEntries().size();

  assertEquals(thirdPartySize, adapterSize);
}
}