package de.micromata.genome.tpsb.httpmockup.testbuilder;

import java.io.File;

import de.micromata.genome.tpsb.annotations.TpsbBuilder;
import de.micromata.genome.tpsb.builder.ScenarioLoaderContext;

/**
 * Test Builder to deal with Http Testscenarios.
 * 
 * This Builder can load an validate Scenarios.
 * 
 * Please see also: https://team.micromata.de/confluence/display/genome/Modul+tpsb-httpmockup
 * 
 * @author Roger Rene Kommer (r.kommer.extern@micromata.de)
 * 
 * @param <T>
 */
@TpsbBuilder
public class HttpTestBuilder<T extends HttpTestBuilder< ? >> extends ServletContextTestBuilder<T>
{
  protected ScenarioLoaderContext scenarioLoaderContext = new ScenarioLoaderContext("dev/extrc/tests/httpmockup", ".txt");

  public T setScenarioBaseDir(String dir)
  {
    scenarioLoaderContext.setBaseDir(new File(dir));
    return getBuilder();
  }

  /**
   * Load a scenario execute it ans validate it validations.
   * 
   * @param name the name
   * @return the t
   */
  public T loadValidateScenario(String name)
  {
    String text = scenarioLoaderContext.loadScenarioTextFile(name);

    HttpScenario scenario = new HttpScenario(text);
    httpRequest = scenario.getRequest();
    executeServletRequest();
    try {
      scenario.checkExpected(this, httpResponse);
    } catch (RuntimeException ex) {
      System.err.println(httpResponse.toString());
      throw ex;
    }
    return getBuilder();
  }
}
