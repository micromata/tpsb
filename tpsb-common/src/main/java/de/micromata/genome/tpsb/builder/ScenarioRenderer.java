package de.micromata.genome.tpsb.builder;

import java.io.File;

/**
 * 
 * @author Roger Kommer (roger.kommer.extern@micromata.de)
 * 
 */
public interface ScenarioRenderer
{
  void renderScenarioContent(File scenarioFile, String content, ScenarioDescriber out);

}
