import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import de.l3s.boilerpipe.BoilerpipeProcessingException;

class ControllerTest {
  @Test
  public void onePlusOneShouldBeTwo() {
    assertEquals(1 + 1, 2);
  }

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

  
}