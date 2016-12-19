package de.micromata.genome.tpsb.builder;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.micromata.genome.tpsb.TpsbComment;
import de.micromata.genome.util.bean.PrivateBeanUtils;
import de.micromata.genome.util.types.Pair;

/**
 * Abstract for the CommandSceanrio.
 * 
 * @param <S>
 * @author Roger Rene Kommer (r.kommer.extern@micromata.de)
 */
public abstract class AbstractScenarioCommand<S extends CommandScenario<?>> implements ScenarioCommand<S>
{
  private Map<String, String> arguments = new HashMap<String, String>();

  public AbstractScenarioCommand()
  {
  }

  @Override
  public void describe(S scenario, ScenarioDescriber out)
  {
    describeHeader(scenario, out);
    out.endStep();
  }

  protected void describeHeader(S scenario, ScenarioDescriber out)
  {
    TpsbComment anot = getClass().getAnnotation(TpsbComment.class);
    String comment = "";
    if (anot != null) {
      comment = anot.value();
    }
    out.startStep(getClass().getSimpleName(), comment);
    describeArguments(out);
  }

  protected void describeArguments(ScenarioDescriber out)
  {
    List<Pair<Field, ? extends Annotation>> res = new ArrayList<Pair<Field, ? extends Annotation>>();
    PrivateBeanUtils.findFieldsWithAnnotation(getClass(), TpsbComment.class, res);
    out.startArgTable();
    for (Pair<Field, ? extends Annotation> me : res) {
      String value = arguments.get(me.getFirst().getName());
      if (value == null) {
        continue;
      }
      TpsbComment tcomment = (TpsbComment) me.getValue();
      out.argLine(me.getFirst().getName(), tcomment.value(), me.getFirst().getType().getSimpleName(), value);
    }
    out.endArgTable();
  }

  @Override
  public Map<String, String> getArguments()
  {
    return arguments;
  }

  @Override
  public void setArguments(Map<String, String> arguments)
  {
    this.arguments = arguments;
  }

}
