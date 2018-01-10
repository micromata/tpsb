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

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

/**
 * Overwrites the servlet request to patch contextPath and other information.
 * 
 * @author roger@micromata.de
 * 
 */
public class MockNestedHttpServletRequest extends HttpServletRequestWrapper implements HttpRequestMockupBase
{

  /**
   * The context path.
   */
  private String contextPath;

  /**
   * The servlet path.
   */
  private String servletPath;

  /**
   * The path info.
   */
  private String pathInfo;

  /**
   * The servlet context.
   */
  private ServletContext servletContext;

  /**
   * The parent request.
   */
  private HttpServletRequest parentRequest;

  /**
   * The request uri.
   */
  private String requestURI;

  /**
   * The query string.
   */
  private String queryString;

  /**
   * Instantiates a new nested http servlet request.
   *
   * @param servletContext the servlet context
   * @param parent the parent
   */
  public MockNestedHttpServletRequest(ServletContext servletContext, HttpServletRequest parent)
  {
    super(parent);
    this.servletContext = servletContext;
    this.parentRequest = parent;
  }

  @Override
  public String getContextPath()
  {
    if (contextPath != null) {
      return contextPath;
    }
    return super.getContextPath();
  }

  @Override
  public RequestDispatcher getRequestDispatcher(String path)
  {
    return servletContext.getRequestDispatcher(path);
  }

  public void setContextPath(String contextPath)
  {
    this.contextPath = contextPath;
  }

  @Override
  public String getServletPath()
  {
    if (servletPath != null) {
      return servletPath;
    }
    return super.getServletPath();
  }

  @Override
  public void setServletPath(String servletPath)
  {
    this.servletPath = servletPath;
  }

  @Override
  public String getPathInfo()
  {
    if (pathInfo != null) {
      return pathInfo;
    }
    return super.getPathInfo();
  }

  @Override
  public void setPathInfo(String pathInfo)
  {
    this.pathInfo = pathInfo;
  }

  @Override
  public ServletContext getServletContext()
  {
    return servletContext;
  }

  public void setServletContext(ServletContext servletContext)
  {
    this.servletContext = servletContext;
  }

  public HttpServletRequest getParentRequest()
  {
    return parentRequest;
  }

  public void setParentRequest(HttpServletRequest parentRequest)
  {
    this.parentRequest = parentRequest;
  }

  @Override
  public String getQueryString()
  {
    if (queryString != null) {
      return queryString;
    }
    return super.getQueryString();
  }

  @Override
  public void setQueryString(String queryString)
  {
    this.queryString = queryString;
  }

  @Override
  public String getRequestURI()
  {
    if (requestURI != null) {
      return requestURI;
    }
    return super.getRequestURI();
  }

  public void parseFromRequestURI()
  {

  }

  public void setRequestURI(String requestURI)
  {
    this.requestURI = requestURI;
  }
}