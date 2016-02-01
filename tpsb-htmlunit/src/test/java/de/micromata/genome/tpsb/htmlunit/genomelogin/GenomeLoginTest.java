package de.micromata.genome.tpsb.htmlunit.genomelogin;

import org.junit.Test;

import de.micromata.genome.tpsb.htmlunit.HtmlPageException;
import de.micromata.genome.tpsb.htmlunit.genomelogin.pages.GenomeConsoleApp;
import de.micromata.genome.tpsb.htmlunit.genomelogin.pages.GenomeIndexPage;
import de.micromata.genome.tpsb.htmlunit.genomelogin.pages.GenomeLoginPage;

/**
 * Sample Test
 * 
 * @author roger
 * 
 */
public class GenomeLoginTest
{
  @Test
  public void testDummy()
  {

  }

  // @Test, URL currently not working!
  public void testStandardLogin()
  {
    GenomeConsoleApp app = new GenomeConsoleApp();
    try {
      app.setBaseUrl("http://secure.micromata.de/dhl-amsel/gw/genomeconsole/")//
          .setJavascriptEnabled(false) //
          .doGoLoginPage()//
          .setUserName("admin") //
          .setPasswort("xxx") //
          .doLogin(GenomeLoginPage.class) //
          .setUserName("admin") //
          .setPasswort("umata") //
          .doLogin(GenomeIndexPage.class) //
          .doLogout() //
      ;
    } catch (HtmlPageException ex) {
      System.out.println(ex.getFailureDump());
      throw ex;
    }
  }
}
