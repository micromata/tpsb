/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   21.09.2008
// Copyright Micromata 21.09.2008
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.tpsb.htmlunit;

import java.util.List;
import java.util.NoSuchElementException;

import org.w3c.dom.Element;

public interface SearchContext
{
  List<Element> findElements(By by);

  /**
   * Find the first {@link WebElement} using the given method.
   * 
   * @param by The locating mechanism
   * @return The first matching element on the current context
   * @throws NoSuchElementException If no matching elements are found
   */
  Element findElement(By by);

}
