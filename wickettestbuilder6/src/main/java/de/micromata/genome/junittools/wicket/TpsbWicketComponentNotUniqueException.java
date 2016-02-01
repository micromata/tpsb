package de.micromata.genome.junittools.wicket;

import de.micromata.genome.tpsb.TestBuilder;

/**
 * Failure in case of to many components are found.
 * 
 * @author Roger Kommer (roger.kommer.extern@micromata.de)
 * 
 */
public class TpsbWicketComponentNotUniqueException extends TpsbWicketComponentException
{

  private static final long serialVersionUID = 7198470489790564439L;

  public TpsbWicketComponentNotUniqueException(String message, TestBuilder< ? > testBuilder, String matcherExpression)
  {
    super(message, testBuilder, matcherExpression);
  }

}
