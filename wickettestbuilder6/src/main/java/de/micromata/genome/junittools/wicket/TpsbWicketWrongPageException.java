package de.micromata.genome.junittools.wicket;

import de.micromata.genome.tpsb.TestBuilder;

/**
 * Falls eine Seite (lastrendered) erwartet wird, aber eine andere angezeigt wird.
 * 
 * @author Roger Kommer (roger.kommer.extern@micromata.de)
 * 
 */
public class TpsbWicketWrongPageException extends TpsbWicketFailedException
{

  private static final long serialVersionUID = 5979918781662741758L;

  public TpsbWicketWrongPageException(String message, TestBuilder< ? > testBuilder)
  {
    super(message, testBuilder);
  }

}
