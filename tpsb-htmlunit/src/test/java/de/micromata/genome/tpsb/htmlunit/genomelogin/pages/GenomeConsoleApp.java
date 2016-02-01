package de.micromata.genome.tpsb.htmlunit.genomelogin.pages;

import de.micromata.genome.tpsb.annotations.TpsbApplication;
import de.micromata.genome.tpsb.htmlunit.HtmlWebTestApp;

/**
 * Html Unit Test app for genome console.
 * 
 * @author roger
 * 
 */
@TpsbApplication
public class GenomeConsoleApp extends HtmlWebTestApp<GenomeConsoleApp>
{
  /**
   * Open the genome console
   * 
   * @return
   */
  public GenomeLoginPage doGoLoginPage()
  {
    return doLoadPage(GenomeLoginPage.class);
  }
}
