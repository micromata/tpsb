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

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import de.micromata.genome.tpsb.httpmockup.MockFilterMapDef.FilterDispatchFlags;

/**
 * Derived from GRequestDispatherImpl
 * 
 * @author roger
 */
public class MockRequestDispatcher implements RequestDispatcher
{
  /**
   * The request attribute under which the request URI of the included servlet is stored on an included dispatcher
   * request.
   */
  public static final String INCLUDE_REQUEST_URI_ATTR = "javax.servlet.include.request_uri";

  /**
   * The request attribute under which the context path of the included servlet is stored on an included dispatcher
   * request.
   */
  public static final String INCLUDE_CONTEXT_PATH_ATTR = "javax.servlet.include.context_path";

  /**
   * The request attribute under which the path info of the included servlet is stored on an included dispatcher
   * request.
   */
  public static final String INCLUDE_PATH_INFO_ATTR = "javax.servlet.include.path_info";

  /**
   * The request attribute under which the servlet path of the included servlet is stored on an included dispatcher
   * request.
   */
  public static final String INCLUDE_SERVLET_PATH_ATTR = "javax.servlet.include.servlet_path";

  /**
   * The request attribute under which the query string of the included servlet is stored on an included dispatcher
   * request.
   */
  public static final String INCLUDE_QUERY_STRING_ATTR = "javax.servlet.include.query_string";

  /**
   * The request attribute under which the original request URI is stored on an forwarded dispatcher request.
   */
  public static final String FORWARD_REQUEST_URI_ATTR = "javax.servlet.forward.request_uri";

  /**
   * The request attribute under which the original context path is stored on an forwarded dispatcher request.
   */
  public static final String FORWARD_CONTEXT_PATH_ATTR = "javax.servlet.forward.context_path";

  /**
   * The request attribute under which the original path info is stored on an forwarded dispatcher request.
   */
  public static final String FORWARD_PATH_INFO_ATTR = "javax.servlet.forward.path_info";

  /**
   * The request attribute under which the original servlet path is stored on an forwarded dispatcher request.
   */
  public static final String FORWARD_SERVLET_PATH_ATTR = "javax.servlet.forward.servlet_path";

  /**
   * The request attribute under which the original query string is stored on an forwarded dispatcher request.
   */
  public static final String FORWARD_QUERY_STRING_ATTR = "javax.servlet.forward.query_string";

  /**
   * The request attribute under which we forward a servlet name to an error page.
   */
  public static final String SERVLET_NAME_ATTR = "javax.servlet.error.servlet_name";

  /**
   * The request attribute under which we forward a Java exception (as an object of type Throwable) to an error page.
   */
  public static final String EXCEPTION_ATTR = "javax.servlet.error.exception";

  /**
   * The request attribute under which we forward the request URI (as an object of type String) of the page on which an
   * error occurred.
   */
  public static final String EXCEPTION_PAGE_ATTR = "javax.servlet.error.request_uri";

  /**
   * The request attribute under which we forward a Java exception type (as an object of type Class) to an error page.
   */
  public static final String EXCEPTION_TYPE_ATTR = "javax.servlet.error.exception_type";

  /**
   * The request attribute under which we forward an HTTP status message (as an object of type STring) to an error page.
   */
  public static final String ERROR_MESSAGE_ATTR = "javax.servlet.error.message";

  /**
   * The request attribute under which we forward an HTTP status code (as an object of type Integer) to an error page.
   */
  public static final String STATUS_CODE_ATTR = "javax.servlet.error.status_code";
  private String url;
  private MockServletContext servletContext;

  /** Constructs a request dispatcher, giving it a handle to the creating request. */
  public MockRequestDispatcher(String url, MockServletContext servletContext)
  {
    this.url = url;
    this.servletContext = servletContext;
  }

  /** Simply stores the URL that was requested for forward, and returns. */
  @Override
  public void forward(ServletRequest req, ServletResponse res) throws ServletException, IOException
  {
    HttpServletRequest hreq = (HttpServletRequest) req;
    getMockRequest(req).setForwardUrl(this.url);
    if (servletContext.isExecuteDispatchRequest() == true) {
      MockNestedHttpServletRequest nreq = new MockNestedHttpServletRequest(servletContext, hreq);
      setForwardAttributes(hreq);
      nreq.setServletPath(this.url);
      execute(nreq, res, FilterDispatchFlags.FORWARD);

    }

  }

  /** Simply stores that the URL was included an then returns. */
  @Override
  public void include(ServletRequest req, ServletResponse res) throws ServletException, IOException
  {
    getMockRequest(req).addIncludedUrl(this.url);
    if (servletContext.isExecuteDispatchRequest() == true) {
      HttpServletRequest hreq = (HttpServletRequest) req;
      setIncludeAttributes(hreq);
      MockNestedHttpServletRequest nreq = new MockNestedHttpServletRequest(servletContext, hreq);
      execute(nreq, res, FilterDispatchFlags.INCLUDE);
    }
  }

  /**
   * Execute.
   *
   * @param nreq the nreq
   * @param resp the resp
   * @param flags the flags
   * @throws ServletException the servlet exception
   * @throws IOException Signals that an I/O exception has occurred.
   */
  protected void execute(MockNestedHttpServletRequest nreq, ServletResponse resp, FilterDispatchFlags flags)
      throws ServletException,
      IOException
  {
    nreq.setRequestURI(this.url);
    MockupHttpRequestUtils.parseRequestUrlToRequest(nreq, this.url);
    final MockFilterChain fc = new MockFilterChain(servletContext, flags);
    fc.doFilter(nreq, resp);
  }

  /** Locates the MockHttpServletRequest in case it is wrapped. */
  public MockHttpServletRequest getMockRequest(ServletRequest request)
  {
    while (request != null & !(request instanceof MockHttpServletRequest)) {
      request = ((HttpServletRequestWrapper) request).getRequest();
    }

    return (MockHttpServletRequest) request;
  }

  protected void setForwardAttributes(HttpServletRequest req)
  {
    req.setAttribute(FORWARD_REQUEST_URI_ATTR, req.getRequestURI());
    req.setAttribute(FORWARD_CONTEXT_PATH_ATTR, req.getContextPath());
    req.setAttribute(FORWARD_PATH_INFO_ATTR, req.getPathInfo());
    req.setAttribute(FORWARD_QUERY_STRING_ATTR, req.getQueryString());
    req.setAttribute(FORWARD_SERVLET_PATH_ATTR, req.getServletPath());
  }

  protected void setIncludeAttributes(HttpServletRequest req)
  {
    req.setAttribute(INCLUDE_REQUEST_URI_ATTR, req.getRequestURI());
    req.setAttribute(INCLUDE_CONTEXT_PATH_ATTR, req.getContextPath());
    req.setAttribute(INCLUDE_PATH_INFO_ATTR, req.getPathInfo());
    req.setAttribute(INCLUDE_QUERY_STRING_ATTR, req.getQueryString());
    req.setAttribute(INCLUDE_SERVLET_PATH_ATTR, req.getServletPath());

  }
}