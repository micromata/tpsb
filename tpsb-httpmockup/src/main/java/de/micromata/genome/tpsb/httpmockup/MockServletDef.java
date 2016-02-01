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

import javax.servlet.http.HttpServlet;

import org.apache.commons.lang.StringUtils;

/**
 * web.xml config part
 * 
 * @author roger@micromata.de
 * 
 */
public class MockServletDef extends MockWebElementBase
{
  private boolean loadOnStartup;

  private HttpServlet servlet;

  public MockServletDef()
  {

  }

  public MockServletDef(HttpServlet servlet)
  {
    this.servlet = servlet;
  }

  public void setLoadOnStartupString(String l)
  {
    loadOnStartup = StringUtils.equals(l, "true");
  }

  public boolean isLoadOnStartup()
  {
    return loadOnStartup;
  }

  public void setLoadOnStartup(boolean loadOnStartup)
  {
    this.loadOnStartup = loadOnStartup;
  }

  public HttpServlet getServlet()
  {
    return servlet;
  }

  public void setServlet(HttpServlet servlet)
  {
    this.servlet = servlet;
  }

}
