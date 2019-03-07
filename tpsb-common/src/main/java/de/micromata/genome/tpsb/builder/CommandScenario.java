package de.micromata.genome.tpsb.builder;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import de.micromata.genome.tpsb.CommonTestBuilder;
import de.micromata.genome.util.bean.PrivateBeanUtils;
import de.micromata.genome.util.text.PipeValueList;

/**
 * Scenario, which executes registered commands.
 * 
 * @author Roger Rene Kommer (r.kommer.extern@micromata.de)
 *
 * @param <T> the type of the {@link CommonTestBuilder}
 */
public class CommandScenario<T extends CommonTestBuilder<?>>extends IniLikeScenario
{
  private static final Logger log = Logger.getLogger(CommandScenario.class);

  protected T builder;

  protected ScenarioLoaderContext scenarioLoaderContext;

  protected Map<String, Class<? extends ScenarioCommand<? extends CommandScenario<?>>>> commands = new HashMap<String, Class<? extends ScenarioCommand<? extends CommandScenario<?>>>>();

  private String actionSectionName = "Actions";

  public CommandScenario(String text, T builder, ScenarioLoaderContext scenarioLoaderContext)
  {
    super(text);
    this.builder = builder;
    this.scenarioLoaderContext = scenarioLoaderContext;
  }

  /**
   * Register command under a name.
   * 
   * @param <C> the generic type
   * @param name the name
   * @param commandClass the command class
   */
  public <C extends ScenarioCommand<?>> void registerCommand(String name, Class<C> commandClass)
  {
    commands.put(name, (Class<? extends ScenarioCommand<? extends CommandScenario<?>>>) commandClass);
  }

  private ScenarioCommand<? extends CommandScenario<?>> parseToCommand(String line, boolean populate,
      boolean expandDollarVars)
  {

    String command = line;
    int idx = line.indexOf(':');
    Map<String, String> argmap = null;
    if (idx != -1) {
      command = line.substring(0, idx);
      String args = StringUtils.trimToEmpty(line.substring(idx + 1));
      if (StringUtils.isBlank(args) == false) {
        argmap = PipeValueList.decode(args);
      }
    }
    Class<? extends ScenarioCommand<? extends CommandScenario<?>>> scenclass = commands.get(command);
    if (scenclass == null) {
      throw new IllegalStateException("Action Command is unknown: " + command);
    }
    ScenarioCommand<? extends CommandScenario<?>> sc = PrivateBeanUtils.createInstance(scenclass);
    if (argmap != null) {
      sc.setArguments(argmap);
      if (populate == true) {
        for (Map.Entry<String, String> me : argmap.entrySet()) {
          String value = me.getValue();
          if (expandDollarVars == true) {
            if (StringUtils.startsWith(value, "${") == true && StringUtils.endsWith(value, "}") == true) {
              String expr = value.substring(2, value.length() - 1);
              Object evaluated = builder.eval(expr);
              String sevaluated = evaluated == null ? StringUtils.EMPTY : evaluated.toString();
              me.setValue(sevaluated);
            }
          }
        }
        PrivateBeanUtils.populate2(sc, argmap);
      }
    }
    return sc;
  }

  public T executeScenario()
  {
    return executeScenario(actionSectionName);
  }

  public T executeScenario(String actionSectionName)
  {
    log.info("Execute Scenario: " + scenarioLoaderContext.getScenarioFileName());
    String actions = getSectionContent(actionSectionName);
    if (StringUtils.isBlank(actions) == true) {
      throw new IllegalStateException("Actions section of scenario is empty");
    }
    String[] actionLines = StringUtils.split(actions, "\n");
    for (String line : actionLines) {
      line = StringUtils.trimToEmpty(line);
      if (StringUtils.isEmpty(line) == true) {
        continue;
      }
      if (line.startsWith("#") == true) {
        continue;
      }
      log.info("Execute command: " + line);
      ScenarioCommand<CommandScenario<T>> command = (ScenarioCommand<CommandScenario<T>>) parseToCommand(line, true,
          true);
      command.execute(this);
    }
    return builder;
  }

  public void describe(String fileName, String sectionName, ScenarioDescriber out)
  {
    out.startFlow(fileName, "");
    if (StringUtils.isBlank(sectionName) == true) {
      sectionName = actionSectionName;
    }
    String comment = getSectionComment(sectionName);

    out.startFlow(sectionName, comment);

    String actions = getSectionContent(sectionName);
    if (StringUtils.isBlank(actions) == true) {
      throw new IllegalStateException("Actions section of scenario is empty");
    }
    String[] actionLines = StringUtils.split(actions, "\n");
    for (String line : actionLines) {
      line = StringUtils.trimToEmpty(line);
      if (StringUtils.isEmpty(line) == true) {
        continue;
      }
      if (line.startsWith("#") == true) {
        String lc = line.substring(1);
        out.linebr(lc);
        continue;
      }

      ScenarioCommand<CommandScenario<T>> command = (ScenarioCommand<CommandScenario<T>>) parseToCommand(line, true,
          false);
      command.describe(this, out);
    }
    out.endFlow();
  }

  public ScenarioLoaderContext getScenarioLoaderContext()
  {
    return scenarioLoaderContext;
  }

  public void setScenarioLoaderContext(ScenarioLoaderContext scenarioLoaderContext)
  {
    this.scenarioLoaderContext = scenarioLoaderContext;
  }

  public T getBuilder()
  {
    return builder;
  }

}