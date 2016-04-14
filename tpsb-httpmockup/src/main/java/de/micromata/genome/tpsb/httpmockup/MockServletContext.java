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

import de.micromata.genome.tpsb.httpmockup.MockFilterMapDef.FilterDispatchFlags;
import org.apache.log4j.Logger;

import javax.servlet.Filter;
import javax.servlet.FilterRegistration;
import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import javax.servlet.SessionCookieConfig;
import javax.servlet.SessionTrackingMode;
import javax.servlet.descriptor.JspConfigDescriptor;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.Enumeration;
import java.util.EventListener;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * A standalone servlet container.
 * 
 * @author Roger Rene Kommer (r.kommer.extern@micromata.de)
 * 
 */
public class MockServletContext implements ServletContext, RequestAcceptor
{
  private static final Logger log = Logger.getLogger(MockServletContext.class);

  private String contextName;

  private Map<String, String> initParameters = new HashMap<String, String>();

  private Map<String, Object> attributes = new HashMap<String, Object>();

  private MockServletsConfig servletsConfig = new MockServletsConfig();

  private MockFiltersConfig filtersConfig = new MockFiltersConfig();

  /** Simple constructor that creates a new mock ServletContext with the supplied context name. */
  public MockServletContext(String contextName)
  {
    this.contextName = contextName;
  }

  public MockServletContext addFilter(final String name, final Class< ? extends Filter> filterClass, final Map<String, String> initParams)
  {

    try {
      final Filter filter = filterClass.newInstance();
      final MockFilterConfig fc = new MockFilterConfig();
      fc.setFilterName(name);
      fc.setServletContext(this);
      if (initParams != null) {
        fc.addAllInitParameters(initParams);
      }
      filter.init(fc);
      this.filtersConfig.addFilter(name, filter);
      return this;
    } catch (final Exception ex) {
      throw new RuntimeException("Exception in initializing filter: " + name + "; " + filterClass.getName() + "; " + ex.getMessage(), ex);
    }
  }

  public MockServletContext addServlet(final String name, final Class< ? extends HttpServlet> servletClass,
      final Map<String, String> initParams)
  {

    try {
      final HttpServlet servlet = servletClass.newInstance();
      final MockServletConfig sc = new MockServletConfig();
      sc.setServletName(name);
      sc.setServletContext(this);
      if (initParams != null) {
        sc.setInitParameters(initParams);
      }

      servlet.init(sc);
      this.servletsConfig.addServlet(name, servlet);
      return this;
    } catch (final Exception ex) {
      throw new RuntimeException("Exception in initializing filter: " + name + "; " + servletClass.getName() + "; " + ex.getMessage(), ex);
    }
  }

  public MockServletContext addServlet(final String name, final HttpServlet servlet)
  {
    this.servletsConfig.addServlet(name, servlet);
    return this;
  }

  public MockServletContext addServletMapping(final String servletName, final String path)
  {
    this.servletsConfig.addServletMapping(servletName, path);
    return this;
  }

  public MockServletContext addFilterMapping(final String filterName, final String path, final int dispatcherFlags)
  {
    this.filtersConfig.addFilterMapping(filterName, path, dispatcherFlags);
    return this;
  }

  public void forward(final MockHttpServletRequest request, final MockHttpServletResponse response) throws Exception
  {
    final MockFilterChain fc = new MockFilterChain(this, FilterDispatchFlags.FORWARD);
    fc.doFilter(request, response);
  }

  @Override
  public void acceptRequest(final MockHttpServletRequest request, final MockHttpServletResponse response) throws IOException,
      ServletException
  {
    final MockFilterChain fc = new MockFilterChain(this, FilterDispatchFlags.REQUEST);
    fc.doFilter(request, response);
  }

  public void serveServlet(final HttpServletRequest req, final HttpServletResponse resp) throws IOException, ServletException
  {
    final String uri = req.getRequestURI();
    final String localUri = uri;

    final MockServletMapDef map = this.servletsConfig.getServletMappingByPath(localUri);
    if (map != null) {
      log.debug("Serve Servlet: " + map.getServletDef().getServlet().getClass().getName());
      map.getServletDef().getServlet().service(req, resp);
    } else {
      log.warn("No servlet found for request: " + req.getRequestURL().toString());
    }
  }

  /** If the url is within this servlet context, returns this. Otherwise returns null. */
  @Override
  public ServletContext getContext(String url)
  {
    if (url.startsWith("/" + this.contextName)) {
      return this;
    } else {
      return null;
    }
  }

  /** Always returns 2. */
  @Override
  public int getMajorVersion()
  {
    return 2;
  }

  /** Always returns 4. */
  @Override
  public int getMinorVersion()
  {
    return 4;
  }

  @Override
  public int getEffectiveMajorVersion()
  {
    return 0;
  }

  @Override
  public int getEffectiveMinorVersion()
  {
    return 0;
  }

  /** Always returns null (i.e. don't know). */
  @Override
  public String getMimeType(String file)
  {
    return null;
  }

  /** Always returns null (i.e. there are no resources under this path). */
  @Override
  public Set getResourcePaths(String path)
  {
    return null;
  }

  /** Uses the current classloader to fetch the resource if it can. */
  @Override
  public URL getResource(String name) throws MalformedURLException
  {
    return Thread.currentThread().getContextClassLoader().getResource(name);
  }

  /** Uses the current classloader to fetch the resource if it can. */
  @Override
  public InputStream getResourceAsStream(String name)
  {
    return Thread.currentThread().getContextClassLoader().getResourceAsStream(name);
  }

  /** Returns a MockRequestDispatcher for the url provided. */
  @Override
  public RequestDispatcher getRequestDispatcher(String url)
  {
    return new MockRequestDispatcher(url);
  }

  /** Returns a MockRequestDispatcher for the named servlet provided. */
  @Override
  public RequestDispatcher getNamedDispatcher(String name)
  {
    return new MockRequestDispatcher(name);
  }

  /** Deprecated method always returns null. */
  @Override
  public Servlet getServlet(String string) throws ServletException
  {
    return null;
  }

  /** Deprecated method always returns an empty enumeration. */
  @Override
  public Enumeration getServlets()
  {
    return Collections.enumeration(Collections.emptySet());
  }

  /** Deprecated method always returns an empty enumeration. */
  @Override
  public Enumeration getServletNames()
  {
    return Collections.enumeration(Collections.emptySet());
  }

  /** Logs the message to log4j. */
  @Override
  public void log(String message)
  {
    log.info(message);
  }

  /** Logs the message and exception to System.out. */
  @Override
  public void log(Exception exception, String message)
  {
    log(message, exception);
  }

  /** Logs the message and exception to log4j. */
  @Override
  public void log(String message, Throwable throwable)
  {
    log(message);
    log.warn(message, throwable);
  }

  /** Always returns null as this is standard behaviour for WAR resources. */
  @Override
  public String getRealPath(String string)
  {
    return null;
  }

  /** Returns a version string identifying the Mock implementation. */
  @Override
  public String getServerInfo()
  {
    return "Stripes Mock Servlet Environment, version 1.0.";
  }

  /** Adds an init parameter to the mock servlet context. */
  public void addInitParameter(String name, String value)
  {
    this.initParameters.put(name, value);
  }

  /** Adds all the values in the supplied Map to the set of init parameters. */
  public void addAllInitParameters(Map<String, String> parameters)
  {
    this.initParameters.putAll(parameters);
  }

  /** Gets the value of an init parameter with the specified name, if one exists. */
  @Override
  public String getInitParameter(String name)
  {
    return this.initParameters.get(name);
  }

  /** Returns an enumeration of all the initialization parameters in the context. */
  @Override
  public Enumeration getInitParameterNames()
  {
    return Collections.enumeration(this.initParameters.keySet());
  }

  @Override
  public boolean setInitParameter(final String name, final String value)
  {
    return false;
  }

  /** Gets an attribute that has been set on the context (i.e. application) scope. */
  @Override
  public Object getAttribute(String name)
  {
    return this.attributes.get(name);
  }

  /** Returns an enumeration of all the names of attributes in the context. */
  @Override
  public Enumeration getAttributeNames()
  {
    return Collections.enumeration(this.attributes.keySet());
  }

  /** Sets the supplied value for the attribute on the context. */
  @Override
  public void setAttribute(String name, Object value)
  {
    this.attributes.put(name, value);
  }

  /** Removes the named attribute from the context. */
  @Override
  public void removeAttribute(String name)
  {
    this.attributes.remove(name);
  }

  /** Returns the name of the mock context. */
  @Override
  public String getServletContextName()
  {
    return this.contextName;
  }

  @Override
  public ServletRegistration.Dynamic addServlet(final String servletName, final String className)
  {
    return null;
  }

  @Override
  public ServletRegistration.Dynamic addServlet(final String servletName, final Servlet servlet)
  {
    return null;
  }

  @Override
  public ServletRegistration.Dynamic addServlet(final String servletName, final Class<? extends Servlet> servletClass)
  {
    return null;
  }

  @Override
  public <T extends Servlet> T createServlet(final Class<T> clazz) throws ServletException
  {
    return null;
  }

  @Override
  public ServletRegistration getServletRegistration(final String servletName)
  {
    return null;
  }

  @Override
  public Map<String, ? extends ServletRegistration> getServletRegistrations()
  {
    return null;
  }

  @Override
  public FilterRegistration.Dynamic addFilter(final String filterName, final String className)
  {
    return null;
  }

  @Override
  public FilterRegistration.Dynamic addFilter(final String filterName, final Filter filter)
  {
    return null;
  }

  @Override
  public FilterRegistration.Dynamic addFilter(final String filterName, final Class<? extends Filter> filterClass)
  {
    return null;
  }

  @Override
  public <T extends Filter> T createFilter(final Class<T> clazz) throws ServletException
  {
    return null;
  }

  @Override
  public FilterRegistration getFilterRegistration(final String filterName)
  {
    return null;
  }

  @Override
  public Map<String, ? extends FilterRegistration> getFilterRegistrations()
  {
    return null;
  }

  @Override
  public SessionCookieConfig getSessionCookieConfig()
  {
    return null;
  }

  @Override
  public void setSessionTrackingModes(final Set<SessionTrackingMode> sessionTrackingModes)
  {

  }

  @Override
  public Set<SessionTrackingMode> getDefaultSessionTrackingModes()
  {
    return null;
  }

  @Override
  public Set<SessionTrackingMode> getEffectiveSessionTrackingModes()
  {
    return null;
  }

  @Override
  public void addListener(final String className)
  {

  }

  @Override
  public <T extends EventListener> void addListener(final T t)
  {

  }

  @Override
  public void addListener(final Class<? extends EventListener> listenerClass)
  {

  }

  @Override
  public <T extends EventListener> T createListener(final Class<T> clazz) throws ServletException
  {
    return null;
  }

  @Override
  public JspConfigDescriptor getJspConfigDescriptor()
  {
    return null;
  }

  @Override
  public ClassLoader getClassLoader()
  {
    return null;
  }

  @Override
  public void declareRoles(final String... roleNames)
  {

  }

  @Override
  public String getContextPath()
  {
    return "/";
  }

  public MockServletsConfig getServletsConfig()
  {
    return servletsConfig;
  }

  public void setServletsConfig(MockServletsConfig servletsConfig)
  {
    this.servletsConfig = servletsConfig;
  }

  public MockFiltersConfig getFiltersConfig()
  {
    return filtersConfig;
  }

  public void setFiltersConfig(MockFiltersConfig filtersConfig)
  {
    this.filtersConfig = filtersConfig;
  }
}