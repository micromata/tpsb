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

package de.micromata.genome.tpsb.builder;

import org.junit.Test;

/**
 * 
 * @author Roger Rene Kommer (r.kommer.extern@micromata.de)
 * 
 */
public class SectionParserTest
{
  @Test
  public void testParse()
  {
    ScenarioLoaderContext scenctx = new ScenarioLoaderContext("dev/extrc/tests", ".txt");
    String text = scenctx.loadScenarioTextFile("SampleIniScenario");
    IniLikeScenario scen = new IniLikeScenario(text);
    String value = scen.getSectionAsProperties("Section 2").get("Key2");
    value.length();

  }

}
