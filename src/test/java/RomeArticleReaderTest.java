import static org.junit.Assert.assertThat;
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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
}