package de.micromata.genome.tpsb.htmlunit;

import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.util.UrlUtils;

import de.micromata.genome.tpsb.CommonTestBuilder;
import de.micromata.genome.tpsb.TpsbException;

public class HtmlWebTestApp<T extends HtmlWebTestApp< ? >> extends CommonTestBuilder<T>
{
  protected HtmlWebClient webClient;

  public HtmlWebTestApp()
  {
    webClient = new HtmlWebClient();
  }

  public T setBaseUrl(String url)
  {
    webClient.setBaseUrl(url);

    return getBuilder();
  }

  public T setJavascriptEnabled(boolean enabled)
  {
    webClient.setJavaScriptEnabled(enabled);
    return getBuilder();
  }

  public <P extends HtmlPageBase< ? >> P doLoadPage(final String url, Class<P> expected)
  {
    try {
      String turl = UrlUtils.resolveUrl(webClient.getBaseUrl(), url);
      Page tp = webClient.getPage(turl);
      return HtmlPageBase.createPage(this, tp, expected);
    } catch (Exception ex) {
      throw new TpsbException(ex);
    }
  }

  /**
   * Load the given page.
   * 
   * @param <P>
   * @param expected
   * @return
   */
  public <P extends HtmlPageBase< ? >> P doLoadPage(Class<P> expected)
  {
    return doLoadPage(expected.getAnnotation(HtmlPageUrl.class).value(), expected);
  }

  public HtmlWebClient getWebClient()
  {
    return webClient;
  }

  public void setWebClient(HtmlWebClient webClient)
  {
    this.webClient = webClient;
  }
}
