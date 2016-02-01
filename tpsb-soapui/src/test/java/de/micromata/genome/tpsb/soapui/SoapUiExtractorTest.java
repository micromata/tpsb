package de.micromata.genome.tpsb.soapui;

import java.io.File;

import org.junit.Test;

/**
 * 
 * @author Roger Rene Kommer (r.kommer.extern@micromata.de)
 * 
 */
public class SoapUiExtractorTest
{
  @Test
  public void testExtract()
  {
    File f = new File("./dev/extrc/tests/soapui/createShipment-soapui-project.xml");
    SoapUiScenarioExtractor extr = new SoapUiScenarioExtractor(f);
    extr.extract();
    String res = extr.toString();
    System.out.println(res);
  }
}