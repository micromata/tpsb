package de.micromata.genome.junittools.wicket;

import org.apache.wicket.Component;

import de.micromata.genome.util.matcher.Matcher;
import de.micromata.genome.util.matcher.MatcherBase;

/**
 * Abstract delegating matcher, which implements TpsbWicketMatchSelector.
 * 
 * @author Roger Kommer (roger.kommer.extern@micromata.de)
 * 
 */
public abstract class AbstractSelectorMatcher extends MatcherBase<Component> implements TpsbWicketMatchSelector
{
  private final Matcher<Component> parentMatcher;

  public AbstractSelectorMatcher(Matcher<Component> parentMatcher)
  {
    this.parentMatcher = parentMatcher;
  }

  @Override
  public boolean match(Component object)
  {
    return parentMatcher.match(object);
  }

}
