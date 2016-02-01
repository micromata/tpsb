/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   26.11.2006
// Copyright Micromata 26.11.2006
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.tpsb.httpmockup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServlet;

/**
 * Configuration for Servlets
 * 
 * @author roger@micromata.de
 * 
 */
public class MockServletsConfig extends MockWebElementConfig
{
  private Map<String, MockServletDef> servlets = new HashMap<String, MockServletDef>();

  private List<MockServletMapDef> servletMapping = new ArrayList<MockServletMapDef>();

  @Override
  protected List< ? extends MockMapDef> getMappingDefs()
  {
    return servletMapping;
  }

  public void addServlet(String name, HttpServlet servlet)
  {
    servlets.put(name, new MockServletDef(servlet));
  }

  public void addServletMapping(String servletName, String path)
  {
    if (servlets.containsKey(servletName) == false) {
      throw new RuntimeException("Servlet with name: " + servletName + " not found");
    }
    servletMapping.add(new MockServletMapDef(path, servlets.get(servletName)));
  }

  public MockServletMapDef getServletMappingByPath(String path)
  {
    int idx = getNextMapDef(path, 0);
    if (idx != -1) {
      return servletMapping.get(idx);
    }
    // TODO try welcome files
    return null;
  }

  public HttpServlet findServletByPath(String path)
  {
    MockServletMapDef p = getServletMappingByPath(path);
    if (p == null) {
      return null;
    }
    return p.getServletDef().getServlet();
  }

  public Map<String, MockServletDef> getServlets()
  {
    return servlets;
  }

  public void setServlets(Map<String, MockServletDef> servlets)
  {
    this.servlets = servlets;
  }

  public List<MockServletMapDef> getServletMapping()
  {
    return servletMapping;
  }

  public void setServletMapping(List<MockServletMapDef> servletMapping)
  {
    this.servletMapping = servletMapping;
  }

}
