package de.micromata.genome.junittools.wicket;

import de.micromata.genome.tpsb.TestBuilder;

/**
 * Failure in case of components are not found.
 * 
 * @author Roger Kommer (roger.kommer.extern@micromata.de)
 * 
 */
public class TpsbWicketComponentNotFoundException extends TpsbWicketComponentException
{

  private static final long serialVersionUID = 5908608246732344763L;

  public TpsbWicketComponentNotFoundException(String message, TestBuilder< ? > testBuilder, String matcherExpression)
  {
    super(message, testBuilder, matcherExpression);
  }

}
