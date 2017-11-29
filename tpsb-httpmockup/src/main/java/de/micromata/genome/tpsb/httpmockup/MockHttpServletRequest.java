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

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.servlet.AsyncContext;
import javax.servlet.DispatcherType;
import javax.servlet.ReadListener;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpUpgradeHandler;
import javax.servlet.http.Part;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.CharEncoding;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

/**
 * Extensions by r.kommer.
 * 
 * <p>
 * Mock implementation of an HttpServletRequest object. Allows for setting most values that are likely to be of interest
 * (and can always be subclassed to affect others). Of key interest and perhaps not completely obvious, the way to get
 * request parameters into an instance of MockHttpServletRequest is to fetch the parameter map using getParameterMap()
 * and use the put() and putAll() methods on it. Values must be String arrays. Examples follow:
 * </p>
 * 
 * <pre>
 * MockHttpServletRequest req = new MockHttpServletRequest(&quot;/foo&quot;, &quot;/bar.action&quot;);
 * req.getParameterMap().put(&quot;param1&quot;, new String[] { &quot;value&quot; });
 * req.getParameterMap().put(&quot;param2&quot;, new String[] { &quot;value1&quot;, &quot;value2&quot; });
 * </pre>
 * 
 * <p>
 * It should also be noted that unless you generate an instance of MockHttpSession (or another implementation of
 * HttpSession) and set it on the request, then your request will <i>never</i> have a session associated with it.
 * </p>
 * 
 * @author Tim Fennell
 * @since Stripes 1.1.1
 */
public class MockHttpServletRequest implements HttpServletRequest, HttpRequestMockupBase
{
  private final Logger log = Logger.getLogger(MockHttpServletRequest.class);

  private byte[] requestData;

  private String contentType = null;

  private String authType;

  private Cookie[] cookies;

  private Map<String, Object> headers = new HashMap<String, Object>();

  private Map<String, Object> attributes = new HashMap<String, Object>();

  private Map<String, String[]> parameters = new HashMap<String, String[]>();

  private String method = "POST";

  private String chacarcterEncoding = CharEncoding.UTF_8;

  private List<Locale> locales = new ArrayList<Locale>();

  private Principal userPrincipal;

  private Set<String> roles = new HashSet<String>();

  private String forwardUrl;

  private List<String> includedUrls = new ArrayList<String>();

  // All the bits of the URL
  private String protocol = "https";

  private String serverName = "localhost";

  private int serverPort = 8080;

  private String contextPath = "";

  private String servletPath = "";

  private String pathInfo = "";

  private String queryString = "";

  private MockServletContext servletContext;

  /**
   * Minimal constructor that makes sense. Requires a context path (should be the same as the name of the servlet
   * context, prepended with a '/') and a servlet path. E.g. new MockHttpServletRequest("/myapp",
   * "/actionType/foo.action").
   * 
   * @param contextPath
   * @param servletPath
   */
  public MockHttpServletRequest(String contextPath, String servletPath)
  {
    this.contextPath = contextPath;
    this.servletPath = servletPath;
  }

  public MockHttpServletRequest()
  {
    this("/", "/");
  }

  public MockHttpServletRequest(MockServletContext servletContext)
  {
    this(servletContext.getContextPath(), "/");
    this.servletContext = servletContext;

  }

  /** Sets the auth type that will be reported by this request. */
  public void setAuthType(String authType)
  {
    this.authType = authType;
  }

  /** Gets the auth type being used by this request. */
  @Override
  public String getAuthType()
  {
    return this.authType;
  }

  /** Sets the array of cookies that will be available from the request. */
  public void setCookies(Cookie[] cookies)
  {
    this.cookies = cookies;
  }

  /** Returns any cookies that are set on the request. */
  @Override
  public Cookie[] getCookies()
  {
    return this.cookies;
  }

  /**
   * Allows headers to be set on the request. These will be returned by the various getXxHeader() methods. If the header
   * is a date header it should be set with a Long. If the header is an Int header it should be set with an Integer.
   */
  public void addHeader(String name, Object value)
  {
    this.headers.put(name.toLowerCase(), value);
  }

  /** Gets the named header as a long. Must have been set as a long with addHeader(). */
  @Override
  public long getDateHeader(String name)
  {
    return (Long) this.headers.get(name);
  }

  /** Returns any header as a String if it exists. */
  @Override
  public String getHeader(String name)
  {
    if (name != null) {
      name = name.toLowerCase();
    }
    Object header = this.headers.get(name);
    if (header != null) {
      return header.toString();
    } else {
      return null;
    }
  }

  /** Returns an enumeration with single value of the named header, or an empty enum if no value. */
  @Override
  public Enumeration getHeaders(String name)
  {
    String header = getHeader(name);
    Collection<String> values = new ArrayList<String>();
    if (header != null) {
      values.add(header);
    }
    return Collections.enumeration(values);
  }

  /** Returns an enumeration containing all the names of headers supplied. */
  @Override
  public Enumeration getHeaderNames()
  {
    return Collections.enumeration(headers.keySet());
  }

  /** Gets the named header as an int. Must have been set as an Integer with addHeader(). */
  @Override
  public int getIntHeader(String name)
  {
    return (Integer) this.headers.get(name);
  }

  /** Sets the method used by the request. Defaults to POST. */
  public void setMethod(String method)
  {
    this.method = method;
  }

  /** Gets the method used by the request. Defaults to POST. */
  @Override
  public String getMethod()
  {
    return this.method;
  }

  /** Sets the path info. Defaults to the empty string. */
  @Override
  public void setPathInfo(String pathInfo)
  {
    this.pathInfo = pathInfo;
  }

  /** Returns the path info. Defaults to the empty string. */
  @Override
  public String getPathInfo()
  {
    return this.pathInfo;
  }

  /** Always returns the same as getPathInfo(). */
  @Override
  public String getPathTranslated()
  {
    return getPathInfo();
  }

  /** Sets the context path. Defaults to the empty string. */
  public void setContextPath(String contextPath)
  {
    this.contextPath = contextPath;
  }

  /** Returns the context path. Defaults to the empty string. */
  @Override
  public String getContextPath()
  {
    if (contextPath != null && "/".equals(contextPath) == false) {
      return contextPath;
    }
    if (servletContext != null) {
      return contextPath = servletContext.getContextPath();
    }
    return this.contextPath;
  }

  /** Sets the query string set on the request; this value is not parsed for anything. */
  @Override
  public void setQueryString(String queryString)
  {
    this.queryString = queryString;
  }

  /** Returns the query string set on the request. */
  @Override
  public String getQueryString()
  {
    return this.queryString;
  }

  /** Returns the name from the user principal if one exists, otherwise null. */
  @Override
  public String getRemoteUser()
  {
    Principal p = getUserPrincipal();
    return p == null ? null : p.getName();
  }

  /** Sets the set of roles that the user is deemed to be in for the request. */
  public void setRoles(Set<String> roles)
  {
    this.roles = roles;
  }

  /** Returns true if the set of roles contains the role specified, false otherwise. */
  @Override
  public boolean isUserInRole(String role)
  {
    return this.roles.contains(role);
  }

  /** Sets the Principal for the current request. */
  public void setUserPrincipal(Principal userPrincipal)
  {
    this.userPrincipal = userPrincipal;
  }

  /** Returns the Principal if one is set on the request. */
  @Override
  public Principal getUserPrincipal()
  {
    return this.userPrincipal;
  }

  /** Returns the ID of the session if one is attached to this request. Otherwise null. */
  @Override
  public String getRequestedSessionId()
  {
    MockHttpSession session = getMockServletContext().getSession();
    if (session == null) {
      return null;
    }
    return session.getId();
  }

  /** Returns the request URI as defined by the servlet spec. */
  @Override
  public String getRequestURI()
  {
    //    if (StringUtils.equals("/", this.contextPath) == true && StringUtils.startsWith(pathInfo, "/") == true) {
    //      return pathInfo;
    //    }

    return joinPath(joinPath(this.getContextPath(), this.servletPath), this.pathInfo);
  }

  String joinPath(String first, String second)
  {
    if (StringUtils.isEmpty(first) == true) {
      return second;
    }
    if (StringUtils.isEmpty(second) == true) {
      return first;
    }
    if ("/".equals(first) == true && second.startsWith("/") == true) {
      return second;
    }

    if (first.endsWith("/") == false && second.startsWith("/") == false) {
      return first + "/" + second;
    }
    return first + second;
  }

  /** Returns (an attempt at) a reconstructed URL based on it's constituent parts. */
  @Override
  public StringBuffer getRequestURL()
  {
    return new StringBuffer().append(this.protocol).append("://").append(this.serverName).append(":")
        .append(this.serverPort)
        .append(this.getContextPath()).append(this.servletPath).append(this.pathInfo);
  }

  /** Gets the part of the path which matched the servlet. */
  @Override
  public String getServletPath()
  {
    return this.servletPath;
  }

  @Override
  public void setServletPath(String servletPath)
  {
    this.servletPath = servletPath;
  }

  /** Gets the session object attached to this request. */
  @Override
  public HttpSession getSession(boolean b)
  {
    MockHttpSession session = getMockServletContext().getSession();
    if (b == false) {
      return session;
    }

    if (session == null) {
      session = new MockHttpSession(getServletContext());
      getMockServletContext().setSession(session);
    }
    return session;
  }

  /** Gets the session object attached to this request. */
  @Override
  public HttpSession getSession()
  {
    return getSession(true);
  }

  /** Always returns true. */
  @Override
  public boolean isRequestedSessionIdValid()
  {
    return true;
  }

  /** Always returns true. */
  @Override
  public boolean isRequestedSessionIdFromCookie()
  {
    return true;
  }

  /** Always returns false. */
  @Override
  public boolean isRequestedSessionIdFromURL()
  {
    return false;
  }

  /** Always returns false. */
  @Override
  public boolean isRequestedSessionIdFromUrl()
  {
    return false;
  }

  @Override
  public boolean authenticate(final HttpServletResponse response) throws IOException, ServletException
  {
    return false;
  }

  @Override
  public void login(final String username, final String password) throws ServletException
  {

  }

  @Override
  public void logout() throws ServletException
  {

  }

  @Override
  public Collection<Part> getParts() throws IOException, ServletException
  {
    return null;
  }

  @Override
  public Part getPart(final String name) throws IOException, ServletException
  {
    return null;
  }

  /** Gets an enumeration of all request attribute names. */
  @Override
  public Enumeration getAttributeNames()
  {
    return Collections.enumeration(this.attributes.keySet());
  }

  /** Gets the character encoding, defaults to UTF-8. */
  @Override
  public String getCharacterEncoding()
  {
    return this.chacarcterEncoding;
  }

  /** Sets the character encoding that will be returned by getCharacterEncoding(). */
  @Override
  public void setCharacterEncoding(String encoding)
  {
    this.chacarcterEncoding = encoding;
  }

  /** Gets the first value of the named parameter or null if a value does not exist. */
  @Override
  public String getParameter(String name)
  {
    String[] values = getParameterValues(name);
    if (values != null && values.length > 0) {
      return values[0];
    }

    return null;
  }

  /** Gets an enumeration containing all the parameter names present. */
  @Override
  public Enumeration getParameterNames()
  {
    return Collections.enumeration(this.parameters.keySet());
  }

  /** Returns an array of all values for a parameter, or null if the parameter does not exist. */
  @Override
  public String[] getParameterValues(String name)
  {
    return this.parameters.get(name);
  }

  /**
   * Provides access to the parameter map. Note that this returns a reference to the live, modifiable parameter map. As
   * a result it can be used to insert parameters when constructing the request.
   */
  @Override
  public Map<String, String[]> getParameterMap()
  {
    return this.parameters;
  }

  /** Sets the protocol for the request. Defaults to "https". */
  public void setProtocol(String protocol)
  {
    this.protocol = protocol;
  }

  /** Gets the protocol for the request. Defaults to "https". */
  @Override
  public String getProtocol()
  {
    return this.protocol;
  }

  /** Always returns the same as getProtocol. */
  @Override
  public String getScheme()
  {
    return getProtocol();
  }

  /** Sets the server name. Defaults to "localhost". */
  public void setServerName(String serverName)
  {
    this.serverName = serverName;
  }

  /** Gets the server name. Defaults to "localhost". */
  @Override
  public String getServerName()
  {
    return this.serverName;
  }

  /** Sets the server port. Defaults to 8080. */
  public void setServerPort(int serverPort)
  {
    this.serverPort = serverPort;
  }

  /** Returns the server port. Defaults to 8080. */
  @Override
  public int getServerPort()
  {
    return this.serverPort;
  }

  @Override
  public BufferedReader getReader() throws IOException
  {
    return new BufferedReader(new InputStreamReader(getInputStream(), CharEncoding.UTF_8));
  }

  /** Aways returns "127.0.0.1". */
  @Override
  public String getRemoteAddr()
  {
    return "127.0.0.1";
  }

  /** Always returns "localhost". */
  @Override
  public String getRemoteHost()
  {
    return "localhost";
  }

  /** Adds a Locale to the set of requested locales. */
  public void addLocale(Locale locale)
  {
    this.locales.add(locale);
  }

  /** Returns the preferred locale. Defaults to the system locale. */
  @Override
  public Locale getLocale()
  {
    return getLocales().nextElement();
  }

  /** Returns an enumeration of requested locales. Defaults to the system locale. */
  @Override
  public Enumeration<Locale> getLocales()
  {
    if (this.locales.size() == 0) {
      this.locales.add(Locale.getDefault());
    }

    return Collections.enumeration(this.locales);
  }

  /** Returns true if the protocol is set to https (default), false otherwise. */
  @Override
  public boolean isSecure()
  {
    return this.protocol.equalsIgnoreCase("https");
  }

  /**
   * Returns an instance of MockRequestDispatcher that just records what URLs are forwarded to or included. The results
   * can be examined later by calling getForwardUrl() and getIncludedUrls().
   */
  @Override
  public MockRequestDispatcher getRequestDispatcher(String url)
  {
    return new MockRequestDispatcher(url, servletContext);
  }

  /** Always returns the path passed in without any alteration. */
  @Override
  public String getRealPath(String path)
  {
    return path;
  }

  /** Always returns 1088 (and yes, that was picked arbitrarily). */
  @Override
  public int getRemotePort()
  {
    return 1088;
  }

  /** Always returns the same value as getServerName(). */
  @Override
  public String getLocalName()
  {
    return getServerName();
  }

  /** Always returns 127.0.0.1). */
  @Override
  public String getLocalAddr()
  {
    return "127.0.0.1";
  }

  /** Always returns the same value as getServerPort(). */
  @Override
  public int getLocalPort()
  {
    return getServerPort();
  }

  @Override
  public ServletContext getServletContext()
  {
    return servletContext;
  }

  public MockServletContext getMockServletContext()
  {
    return servletContext;
  }

  public void setServletContext(MockServletContext servletContext)
  {
    this.servletContext = servletContext;
  }

  @Override
  public AsyncContext startAsync() throws IllegalStateException
  {
    return null;
  }

  @Override
  public AsyncContext startAsync(final ServletRequest servletRequest, final ServletResponse servletResponse)
      throws IllegalStateException
  {
    return null;
  }

  @Override
  public boolean isAsyncStarted()
  {
    return false;
  }

  @Override
  public boolean isAsyncSupported()
  {
    return false;
  }

  @Override
  public AsyncContext getAsyncContext()
  {
    return null;
  }

  @Override
  public DispatcherType getDispatcherType()
  {
    return null;
  }

  /** Used by the request dispatcher to set the forward URL when a forward is invoked. */
  void setForwardUrl(String url)
  {
    this.forwardUrl = url;
  }

  /** Gets the URL that was forwarded to, if a forward was processed. Null otherwise. */
  public String getForwardUrl()
  {
    return this.forwardUrl;
  }

  /** Used by the request dispatcher to record that a URL was included. */
  void addIncludedUrl(String url)
  {
    this.includedUrls.add(url);
  }

  /** Gets the list (potentially empty) or URLs that were included during the request. */
  public List<String> getIncludedUrls()
  {
    return this.includedUrls;
  }

  @Override
  public Object getAttribute(String key)
  {
    Object ret = this.attributes.get(key);
    if (log.isDebugEnabled() == true) {
      log.debug("MockupHttpServletRequest.getAttribute(" + key + ") => " + ObjectUtils.toString(ret));
    }
    return ret;
  }

  @Override
  public void removeAttribute(String name)
  {

    this.attributes.remove(name);
    if (log.isDebugEnabled() == true) {
      log.debug("MockupHttpServletRequest.removeAttribute(" + name + ")");
    }
  }

  @Override
  public void setAttribute(String name, Object value)
  {
    this.attributes.put(name, value);
    if (log.isDebugEnabled() == true) {
      log.debug("MockupHttpServletRequest.setAttribute(" + name + ", " + ObjectUtils.toString(value) + ")");
    }
  }

  @Override
  public int getContentLength()
  {
    if (requestData != null) {
      return requestData.length;
    }
    return -1;
  }

  @Override
  public String getContentType()
  {
    return contentType;
  }

  @Override
  public ServletInputStream getInputStream() throws IOException
  {
    if (requestData == null) {
      return null;
    }
    final ByteArrayInputStream bis = new ByteArrayInputStream(requestData);
    return new ServletInputStream()
    {

      @Override
      public int read() throws IOException
      {
        return bis.read();
      }

      @Override
      public boolean isFinished()
      {
        return false;
      }

      @Override
      public boolean isReady()
      {
        return false;
      }

      @Override
      public void setReadListener(ReadListener readListener)
      {

      }
    };

  }

  private String joinpath(String first, String second)
  {
    if (StringUtils.isBlank(first) == true) {
      return second;
    }
    if (StringUtils.isBlank(second) == true) {
      return first;
    }
    return first + "/" + second;
  }

  @Override
  public String toString()
  {
    StringBuilder sb = new StringBuilder();
    String p = joinpath(joinpath(contextPath, servletPath), pathInfo) + StringUtils.defaultString(queryString);
    sb.append(method).append(" ").append(p).append(" HTTP/1.1\n");
    for (Map.Entry<String, Object> me : headers.entrySet()) {
      sb.append(me.getKey()).append(": ").append(me.getValue()).append("\n");
    }
    sb.append("\n");
    if (this.requestData != null) {

      sb.append(new String(this.requestData));
    }
    return sb.toString();
  }

  public byte[] getRequestData()
  {
    return requestData;
  }

  public void setRequestData(byte[] requestData)
  {
    this.requestData = requestData;
  }

  public void setContentType(String contentType)
  {
    this.contentType = contentType;
  }

  @Override
  public String changeSessionId()
  {
    return null;
  }

  @Override
  public <T extends HttpUpgradeHandler> T upgrade(Class<T> handlerClass) throws IOException, ServletException
  {
    return null;
  }

  @Override
  public long getContentLengthLong()
  {
    return 0;
  }

  public void addRequestParameter(String k, String v)
  {
    String[] params = parameters.get(k);
    if (params == null) {
      parameters.put(k, new String[] { v });
    } else {
      parameters.put(k, ArrayUtils.add(params, v));
    }
  }
}
