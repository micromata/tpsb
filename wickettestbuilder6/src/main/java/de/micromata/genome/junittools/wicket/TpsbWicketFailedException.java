package de.micromata.genome.junittools.wicket;

import de.micromata.genome.tpsb.AssertionFailedException;
import de.micromata.genome.tpsb.TestBuilder;

/**
 * Wicket related test failures.
 * 
 * @author Roger Kommer (roger.kommer.extern@micromata.de)
 * 
 */
public class TpsbWicketFailedException extends AssertionFailedException
{

  private static final long serialVersionUID = 22105086830558999L;

  public TpsbWicketFailedException(String message, TestBuilder< ? > testBuilder)
  {
    super(message, testBuilder);
  }

}
