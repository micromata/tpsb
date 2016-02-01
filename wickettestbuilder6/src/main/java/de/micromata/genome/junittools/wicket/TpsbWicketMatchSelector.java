package de.micromata.genome.junittools.wicket;

import org.apache.wicket.Component;

/**
 * Used inside the fin method, not to add the found component, but relatet component.
 * 
 * @author Roger Kommer (roger.kommer.extern@micromata.de)
 * 
 */
public interface TpsbWicketMatchSelector
{
  /**
   * Select on match an component.
   * 
   * @param found matched
   * @return relativ to found.
   */
  Component selectMatched(Component found);
}
