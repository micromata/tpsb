package de.micromata.genome.tpsb.htmlunit.genomelogin.pages;

import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import de.micromata.genome.tpsb.htmlunit.HtmlPageBase;
import de.micromata.genome.tpsb.htmlunit.HtmlPageUrl;

/**
 * The Logout page. This only redirect Login.action after calling.
 * 
 * @author roger
 * 
 */
@HtmlPageUrl("Logout.action")
public class GenomeLogoutPage extends HtmlPageBase<GenomeLogoutPage>
{
  public GenomeLogoutPage(GenomeConsoleApp webApp, HtmlPage htmlPage)
  {
    super(webApp, htmlPage);
  }

  @Override
  public HtmlForm getStdForm()
  {
    return null;
  }
}