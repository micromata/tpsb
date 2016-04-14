//
// Copyright (C) 2010-2016 Micromata GmbH
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//  http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//

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
