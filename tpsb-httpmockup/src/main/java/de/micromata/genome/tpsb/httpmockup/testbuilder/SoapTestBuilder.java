package de.micromata.genome.tpsb.httpmockup.testbuilder;

/**
 * 
 * @author Roger Rene Kommer (r.kommer.extern@micromata.de)
 * 
 */
public class SoapTestBuilder<T extends SoapTestBuilder< ? >> extends ServletContextTestBuilder<T>
{

  public T loadScenario(String file)
  {
    return getBuilder();

  }
}
