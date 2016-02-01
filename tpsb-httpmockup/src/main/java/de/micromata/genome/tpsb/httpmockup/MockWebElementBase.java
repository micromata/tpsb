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

import java.util.HashMap;
import java.util.Map;

/**
 * web.xml config element
 * 
 * @author roger@micromata.de
 * 
 */
public class MockWebElementBase
{
  private String description;

  private String displayName;

  private String className;

  private String name;

  private Map<String, String> initParameters = new HashMap<String, String>();

  public String getDescription()
  {
    return description;
  }

  public void setDescription(String description)
  {
    this.description = description;
  }

  public String getDisplayName()
  {
    return displayName;
  }

  public void setDisplayName(String displayName)
  {
    this.displayName = displayName;
  }

  public String getClassName()
  {
    return className;
  }

  public void setClassName(String className)
  {
    this.className = className;
  }

  public String getName()
  {
    return name;
  }

  public void setName(String name)
  {
    this.name = name;
  }

  public void addInitParameter(String key, String value)
  {
    initParameters.put(key, value);
  }

  public Map<String, String> getInitParameters()
  {
    return initParameters;
  }

  public void setInitParameters(Map<String, String> initParameters)
  {
    this.initParameters = initParameters;
  }
}
