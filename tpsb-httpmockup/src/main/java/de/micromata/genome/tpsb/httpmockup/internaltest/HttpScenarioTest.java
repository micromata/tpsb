package de.micromata.genome.tpsb.httpmockup.internaltest;

import org.junit.Test;

/**
 * tests the HttpTestBuilder.
 * 
 * @author Roger Rene Kommer (r.kommer.extern@micromata.de)
 * 
 */
public class HttpScenarioTest
{
  @Test
  public void testFirst()
  {
    new InternalHttpTestBuilder() //
        .loadValidateScenario("HttpScenario1") //
        .dumpResponse() //
        .loadValidateScenario("HttpScenario2") //
        .dumpResponse() //
    ;
  }
}
