package de.micromata.genome.junittools.wicket;

import org.apache.wicket.Component;

import de.micromata.genome.util.matcher.BooleanListRulesFactory;
import de.micromata.genome.util.matcher.EqualsMatcher;
import de.micromata.genome.util.matcher.EveryMatcher;
import de.micromata.genome.util.matcher.Matcher;
import de.micromata.genome.util.matcher.MatcherBase;
import de.micromata.genome.util.matcher.StringMatchers;
import de.micromata.genome.util.matcher.string.EndsWithMatcher;

/**
 * Matcher for components.
 * 
 * @author Roger Rene Kommer (r.kommer.extern@micromata.de)
 * 
 */
public class TpsbWicketMatchers extends StringMatchers
{
  protected static BooleanListRulesFactory<String> matcherFactory = new BooleanListRulesFactory<String>();

  /**
   * Creates an exact matcher to wicket id.
   * 
   * @param id the id
   * @return the matcher
   */
  public static Matcher<Component> exactWicketId(String id)
  {
    return new WicketIdMatcher(new EqualsMatcher<String>(id));
  }

  /**
   * Matched to all.
   * 
   * @return matcher.
   */
  public static Matcher<Component> all()
  {
    return new EveryMatcher<Component>();
  }

  /**
   * Matches against end of (normalized) wicket path.
   * 
   * @param path the path
   * @return the matcher
   */
  public static Matcher<Component> endsWithWicketPath(String path)
  {
    return new WicketPathMatcher(new EndsWithMatcher<String>(TpsbWicketTools.normalizeWicketId(path)));
  }

  public static Matcher<Component> matchWicketPath(String matcherRule)
  {
    return new WicketPathMatcher(matcherFactory.createMatcher(TpsbWicketTools.normalizeWicketId(matcherRule)));
  }

  public static Matcher<Component> matchParent(Matcher<Component> matcher)
  {
    return new AbstractSelectorMatcher(matcher)
    {
      @Override
      public Component selectMatched(Component found)
      {
        return found.getParent();
      }

      @Override
      public String toString()
      {
        return "ParentOf(" + super.toString() + ")";
      }

    };
  }

  public static Matcher<Component> matchComponentClass(final Class< ? > clazz)
  {
    return new MatcherBase<Component>()
    {

      @Override
      public boolean match(Component object)
      {
        return clazz.isAssignableFrom(object.getClass());
      }

      @Override
      public String toString()
      {
        return "AssignableFrom(" + clazz.getName() + ")";
      }
    };
  }

  /**
   * Create a matcher with BooleanListRulesFactory of the pattern.
   * 
   * @param matcherRule the matcher rule
   * @return the matcher
   */
  public static Matcher<Component> matchWicketId(String matcherRule)
  {
    matcherRule = TpsbWicketTools.normalizeWicketId(matcherRule);
    return new WicketIdMatcher(matcherFactory.createMatcher(matcherRule));
  }

  public static class WicketIdMatcher extends MatcherBase<Component>
  {

    private static final long serialVersionUID = -8174262499134720095L;

    private Matcher<String> stringMatcher;

    public WicketIdMatcher(Matcher<String> stringMatcher)
    {
      this.stringMatcher = stringMatcher;
    }

    @Override
    public boolean match(Component object)
    {
      return stringMatcher.match(object.getId());
    }

    @Override
    public String toString()
    {
      return "WicketIdMatcher: " + stringMatcher.toString();
    }
  }

  public static class WicketPathMatcher extends MatcherBase<Component>
  {

    private static final long serialVersionUID = 7231026529128918484L;

    private Matcher<String> stringMatcher;

    public WicketPathMatcher(Matcher<String> stringMatcher)
    {
      this.stringMatcher = stringMatcher;
    }

    @Override
    public boolean match(Component object)
    {
      String path = object.getPath();
      String normalized = TpsbWicketTools.normalizeWicketId(path);
      return stringMatcher.match(normalized);
    }

    @Override
    public String toString()
    {
      return "WicketPathMatcher: " + stringMatcher.toString();
    }
  }

}
