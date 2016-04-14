//
// Copyright (C) 2010-2016 Micromata GmbH
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//  http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//

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
