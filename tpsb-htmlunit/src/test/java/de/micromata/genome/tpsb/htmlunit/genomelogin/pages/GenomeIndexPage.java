package de.micromata.genome.tpsb.htmlunit.genomelogin.pages;

import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import de.micromata.genome.tpsb.htmlunit.HtmlPageBase;
import de.micromata.genome.tpsb.htmlunit.HtmlPageUrl;

@HtmlPageUrl("Index.action")
public class GenomeIndexPage extends HtmlPageBase<GenomeLoginPage>
{
  public GenomeIndexPage(GenomeConsoleApp app, HtmlPage htmlPage)
  {
    super(app, htmlPage);
  }

  @Override
  public HtmlForm getStdForm()
  {
    // may return set test mode subwindow
    return null;
  }

  public GenomeLoginPage doLogout()
  {
    return getWebApp().doLoadPage(GenomeLogoutPage.class.getAnnotation(HtmlPageUrl.class).value(), GenomeLoginPage.class);
  }
}
