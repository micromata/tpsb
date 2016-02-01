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
