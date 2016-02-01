package de.micromata.genome.tpsb.builder;

import java.io.File;

/**
 * The scenario file is a HTML file, which can be embedded into documentation.
 * 
 * @author Roger Kommer (roger.kommer.extern@micromata.de)
 * 
 */
public class HtmlIncludeScenarioRenderer implements ScenarioRenderer
{

  @Override
  public void renderScenarioContent(File scenarioFile, String content, ScenarioDescriber out)
  {
    out.code(content);
  }

}
