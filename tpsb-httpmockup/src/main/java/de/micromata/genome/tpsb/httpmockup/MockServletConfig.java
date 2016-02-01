/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   31.12.2006
// Copyright Micromata 31.12.2006
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.tpsb.httpmockup;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;

import org.apache.commons.collections15.iterators.IteratorEnumeration;

/**
 * Configuration of the servlets of a gwar
 * 
 * @author roger@micromata.de
 * 
 */
public class MockServletConfig implements ServletConfig
{
  private Map<String, String> initParameters = new HashMap<String, String>();

  private MockServletContext servletContext;

  private String servletName;

  public MockServletConfig()
  {
  }

  public MockServletConfig(MockServletContext servletContext, String servletName, Map<String, String> config)
  {
    this.servletContext = servletContext;
    this.servletName = servletName;
    if (config != null) {
      this.initParameters = config;
    }
  }

  @Override
  public String getInitParameter(String arg0)
  {
    return initParameters.get(arg0);
  }

  @Override
  @SuppressWarnings("unchecked")
  public Enumeration getInitParameterNames()
  {
    return new IteratorEnumeration(initParameters.keySet().iterator());
  }

  @Override
  public ServletContext getServletContext()
  {
    return servletContext;
  }

  @Override
  public String getServletName()
  {
    return servletName;
  }

  public Map<String, String> getInitParameters()
  {
    return initParameters;
  }

  public void setInitParameters(Map<String, String> initParameters)
  {
    this.initParameters = initParameters;
  }

  public void setServletContext(MockServletContext servletContext)
  {
    this.servletContext = servletContext;
  }

  public void setServletName(String servletName)
  {
    this.servletName = servletName;
  }

}
