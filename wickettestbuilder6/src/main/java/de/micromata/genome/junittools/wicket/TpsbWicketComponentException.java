package de.micromata.genome.junittools.wicket;

import de.micromata.genome.tpsb.TestBuilder;

/**
 * Failure in case of components are not correct.
 * 
 * @author Roger Kommer (roger.kommer.extern@micromata.de)
 * 
 */
public class TpsbWicketComponentException extends TpsbWicketFailedException
{

  private static final long serialVersionUID = 4356339641126599117L;

  private String matcherExpression;

  public TpsbWicketComponentException(String message, TestBuilder< ? > testBuilder, String matcherExpression)
  {
    super(message + ": " + matcherExpression, testBuilder);
    this.matcherExpression = matcherExpression;
  }

  public String getMatcherExpression()
  {
    return matcherExpression;
  }

  public void setMatcherExpression(String matcherExpression)
  {
    this.matcherExpression = matcherExpression;
  }
}
