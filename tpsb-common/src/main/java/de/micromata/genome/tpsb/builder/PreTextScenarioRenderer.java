package de.micromata.genome.tpsb.builder;

import java.io.File;

/**
 * Renders content as in pre text.
 * 
 * @author Roger Kommer (roger.kommer.extern@micromata.de)
 * 
 */
public class PreTextScenarioRenderer implements ScenarioRenderer
{

  @Override
  public void renderScenarioContent(File scenarioFile, String content, ScenarioDescriber out)
  {
    out.code("<pre>\n");
    out.text(content);
    out.code("\n</pre>\n");
  }

}
