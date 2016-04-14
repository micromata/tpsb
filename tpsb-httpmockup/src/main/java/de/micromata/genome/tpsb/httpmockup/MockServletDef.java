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
