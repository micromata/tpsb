/*
 * 
 */
package de.micromata.genome.tpsb.builder;

import java.io.IOException;

import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import de.micromata.genome.tpsb.TpsbComment;
import de.micromata.genome.util.runtime.RuntimeIOException;
import de.micromata.genome.util.types.Pair;

/**
 * Execute Scenario.
 * 
 * 
 * Variables are set in the Testcontext
 * 
 * The scenario itself is available by variable 'scenario'.
 * 
 * Is also a section and a file given, both are executed (at first the content of the file, after that the section).
 * 
 * @param <S> type of the {@link CommandScenario}
 * @author Roger Rene Kommer (r.kommer.extern@micromata.de)
 */
@TpsbComment("Execute Scenario."
    + " Dabei sind alle Klassen (auch Testbuilder) von VLS verfügbar."
    + "  Variables are set in the Testcontext."
    + " Is also a section and a file given, both are executed (at first the content of the file, after that the section).")
public class GroovyScenarioCommand<S extends CommandScenario<?>>extends AbstractScenarioCommand<S>
{
  /**
   * Datei, die als Groovy-Script eingebunden wird.
   */
  @TpsbComment("File of a groovy script.")
  private String file;

  /**
   * Sektion, Groovy-Script eingebunden wird.
   */
  @TpsbComment("Sektion, containing a groovy scripp.")
  private String section;

  /**
   * internal
   */
  private Class<?>[] importingClasses = new Class<?>[] {

  };

  /**
   * 
   * @param scenario the scenario to use
   * @return Comment, Code
   */
  private Pair<String, String> getCode(S scenario)
  {
    try {
      String comment = "";
      StringBuilder code = new StringBuilder(1024);
      addImports(code);
      if (StringUtils.isNotBlank(file) == true) {
        code.append("\n");
        for (String line : IOUtils.readLines(scenario.scenarioLoaderContext.openInputStream(file), Charsets.UTF_8)) {
          code.append(line).append("\n");
        }
      }
      if (StringUtils.isNotBlank(section) == true) {
        code.append("\n");
        code.append(scenario.getSectionContent(section));
        comment = scenario.getSectionComment(section);
      }
      return Pair.make(comment, code.toString());
    } catch (IOException ex) {
      throw new RuntimeIOException(ex);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void execute(S scenario)
  {
    String code = getCode(scenario).getValue();
    executeGroovy(scenario, code);
  }

  /**
   * Internal.
   * 
   * @param scenario the scenario
   * @param code the code
   * @param  <S> the {@link CommandScenario}
   */
  public static <S extends CommandScenario<?>> void executeGroovy(S scenario, String code)
  {
    scenario.getBuilder().setTestContextVar("scenario", scenario);
    scenario.getBuilder().setTestContextVar("scenarioLoaderContext", scenario.getScenarioLoaderContext());

    scenario.getBuilder().executeExpression(code);
  }

  /**
   * Internal.
   * 
   * Adds the imports.
   * 
   * @param sb the sb
   */
  private void addImports(StringBuilder sb)
  {
    for (Class<?> clazz : importingClasses) {
      sb.append("import ").append(clazz.getName()).append(";\n");
    }
  }

  @Override
  public void describe(S scenario, ScenarioDescriber out)
  {
    describeHeader(scenario, out);
    Pair<String, String> commentAndCode = getCode(scenario);
    out.printCodeSection("Groovy Code", commentAndCode.getFirst(), commentAndCode.getSecond());
    out.endStep();
  }
}
