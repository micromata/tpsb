package de.micromata.genome.tpsb.builder;

import java.util.Map;

import de.micromata.genome.tpsb.builder.ScenarioDescriber;

/**
 * Command for the CommandSceanrio.
 * 
 * @param <S>
 * @author Roger Rene Kommer (r.kommer.extern@micromata.de)
 */
public interface ScenarioCommand<S extends CommandScenario<?>>
{

  /**
   * Execute.
   * 
   * @param scenario the scenario
   */
  void execute(S scenario);

  void describe(S scenario, ScenarioDescriber sb);

  Map<String, String> getArguments();

  public void setArguments(Map<String, String> arguments);
}
