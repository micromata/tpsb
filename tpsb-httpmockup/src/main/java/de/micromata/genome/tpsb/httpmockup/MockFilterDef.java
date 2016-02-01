/////////////////////////////////////////////////////////////////////////////
//
// Project   Micromata Genome Core
//
// Author    roger@micromata.de
// Created   19.01.2008
// Copyright Micromata 19.01.2008
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.tpsb.httpmockup;

import javax.servlet.Filter;

/**
 * web.xml config part
 * 
 * @author roger@micromata.de
 * 
 */
public class MockFilterDef extends MockWebElementBase
{
  private Filter filter;

  public MockFilterDef()
  {

  }

  public MockFilterDef(Filter filter)
  {
    this.filter = filter;
  }

  public Filter getFilter()
  {
    return filter;
  }

  public void setFilter(Filter filter)
  {
    this.filter = filter;
  }
}
