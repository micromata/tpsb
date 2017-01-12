package de.micromata.genome.tpsb.builder;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.micromata.genome.tpsb.TpsbComment;
import de.micromata.genome.util.bean.PrivateBeanUtils;

/**
 * Include and execute scenario.
 * 
 * @param <S>
 * @author Roger Rene Kommer (r.kommer.extern@micromata.de)
 */
@TpsbComment("Inkludiert ein anderes Scenario")
public class IncludeScenarioCommand<S extends CommandScenario<?>>extends AbstractScenarioCommand<S>
{
  private static final Logger log = Logger.getLogger(IncludeScenarioCommand.class);
  /**
   * Optional class name of the CommandScenario.
   * 
   * If not set, uses the current the class of the current scenario.
   */
  @TpsbComment("Klasse, die das Scenario ausführen soll.")
  private String scenarioClass;

  /**
   * Id of the included scenario. If null execute this file.
   */
  @TpsbComment("Id der Scenario Datei, die inkludiert werden soll. Wenn nicht angegeben, wird die aktuelle Scenariodatei verwendet.")
  private String fileId;
  /**
   * section to execute. If null, execute Action Section.
   */
  @TpsbComment("Actions-Sektion, die ausgeführt wird. Wenn nicht angegeben, ist das die Sektion [Actions].")
  private String section = "Actions";

  /**
   * If not null, a groovy expression, executed by TestBuilder. Only this expression is true, the scenario will be
   * executed.
   */
  @TpsbComment("Eine Groovy-Ausdruck, der vom dem TestBuilder ausgewertet wird."
      + " Nur wenn der Ausdruck true ist, wird das Scenario ausgeführt.")
  private String condition;
  /**
   * If not null, execution expect an exception with given class.
   */
  @TpsbComment("Wenn angeben, wird beim Ausführen des inkludierten Scenario eine Exception dieser Klasse erwartet")
  private String expectedException;

  protected CommandScenario<?> loadScenario(S scenario)
  {
    Class<?> clazz;
    if (StringUtils.isNotBlank(scenarioClass) == true) {
      try {
        clazz = Class.forName(scenarioClass);
      } catch (ClassNotFoundException e) {
        throw new IllegalArgumentException(e);
      }
    } else {
      clazz = scenario.getClass();
    }
    CommandScenario commandScenario;
    if (StringUtils.isEmpty(fileId) == false) {
      String text = scenario.getScenarioLoaderContext().loadScenarioTextFile(fileId);

      commandScenario = (CommandScenario) PrivateBeanUtils.createInstance(clazz, text,
          scenario.getBuilder(), scenario.getScenarioLoaderContext());
    } else {
      commandScenario = scenario;
    }
    return commandScenario;
  }

  @Override
  public void execute(S scenario)
  {
    if (checkCondition(scenario) == false) {
      return;
    }
    CommandScenario<?> commandScenario = loadScenario(scenario);
    Class<?> expex = null;
    if (StringUtils.isNotBlank(expectedException) == true) {
      try {
        expex = Class.forName(expectedException);
      } catch (ClassNotFoundException ex) {
        throw new IllegalArgumentException("Class not found: " + expectedException + ": " + ex.getMessage(), ex);
      }
    }
    try {
      commandScenario.executeScenario(section);
    } catch (RuntimeException ex) {
      if (expex != null) {
        if (expex.isAssignableFrom(ex.getClass()) == true) {
          log.info("Catched exception exception: " + ex.getClass().getName() + " ("
              + expectedException + ")");
          return;
        }
      }
      throw ex;
    }
    if (expex != null) {
      throw new IllegalArgumentException("Missing expected Exception: " + expectedException);
    }
  }

  private boolean checkCondition(S scenario)
  {
    if (StringUtils.isBlank(condition) == true) {
      return true;
    }
    Object o = scenario.getBuilder().eval(condition);
    if (o instanceof Boolean) {
      return ((Boolean) o).booleanValue();
    }
    return false;
  }

  @Override
  public void describe(S scenario, ScenarioDescriber out)
  {
    describeHeader(scenario, out);
    CommandScenario<?> inclsc = loadScenario(scenario);
    String file = fileId;
    if (file == null) {
      file = scenario.getScenarioLoaderContext().getScenarioShortFileName();
    }
    inclsc.describe(file, section, out);
    out.endStep();
  }
}
