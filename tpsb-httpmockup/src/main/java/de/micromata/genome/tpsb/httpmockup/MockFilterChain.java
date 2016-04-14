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

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import de.micromata.genome.tpsb.httpmockup.MockFilterMapDef.FilterDispatchFlags;

/**
 * Adopted from genome servlet.
 * 
 * @author Roger Rene Kommer (r.kommer.extern@micromata.de)
 * 
 */
public class MockFilterChain implements FilterChain
{
  private static final Logger log = Logger.getLogger(MockFilterChain.class);

  private int filterIndex = 0;

  private final MockServletContext mockupServletContext;

  private final static MockServletMapDef servletMapDef = null;

  private FilterDispatchFlags dispatcherFlag = FilterDispatchFlags.REQUEST;

  public MockFilterChain(final MockServletContext mockupServletContext, final FilterDispatchFlags dispatcherFlag)
  {
    this.mockupServletContext = mockupServletContext;
    this.dispatcherFlag = dispatcherFlag;
  }

  @Override
  public void doFilter(final ServletRequest req, final ServletResponse resp) throws IOException, ServletException
  {
    final HttpServletRequest hreq = (HttpServletRequest) req;
    final String uri = hreq.getRequestURI();
    final String localUri = uri;
    final MockFiltersConfig fc = this.mockupServletContext.getFiltersConfig();

    this.filterIndex = fc.getNextFilterMapDef(localUri, this.filterIndex, this.dispatcherFlag);
    if (this.filterIndex == -1) {
      this.mockupServletContext.serveServlet((HttpServletRequest) req, (HttpServletResponse) resp);
      return;
    }
    final Filter f = fc.getFilterMapping().get(this.filterIndex).getFilterDef().getFilter();
    ++this.filterIndex;
    if (log.isDebugEnabled() == true) {
      log.debug("Filter filter: " + f.getClass().getName());
    }
    f.doFilter(req, resp, this);

  }

  public FilterDispatchFlags getDispatcherFlag()
  {
    return this.dispatcherFlag;
  }

  public void setDispatcherFlag(final FilterDispatchFlags dispatcherFlag)
  {
    this.dispatcherFlag = dispatcherFlag;
  }
}
