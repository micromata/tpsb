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
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.CharEncoding;
import org.apache.log4j.Logger;

/**
 * Mockup servlet response.
 * 
 * Adopted from stripes.
 * 
 * @author Roger Rene Kommer (r.kommer.extern@micromata.de)
 * 
 */
public class MockHttpServletResponse implements HttpServletResponse
{
  private static Logger log = Logger.getLogger(MockHttpServletResponse.class);

  private MockServletOutputStream out = new MockServletOutputStream();

  private PrintWriter writer = new PrintWriter(out, true);

  private Locale locale = Locale.getDefault();

  private Map<String, List<Object>> headers = new HashMap<String, List<Object>>();

  private List<Cookie> cookies = new ArrayList<Cookie>();

  private int status = 200;

  private String errorMessage;

  private String characterEncoding = CharEncoding.UTF_8;

  private int contentLength;

  private String contentType = "text/html";

  private String redirectUrl;

  private String getStandardStatusMessage()
  {
    if (errorMessage != null) {
      return errorMessage;
    }
    switch (status) {
      case 200:
        return "OK";
      case 302:
        return "Moved Temporarly";
      default:
        return "OK";
    }
  }

  @Override
  public String toString()
  {
    StringBuilder sb = new StringBuilder();
    sb.append("HTTP/1.1 ").append(status).append(" ").append(getStandardStatusMessage()).append("\n");
    for (Map.Entry<String, List<Object>> me : headers.entrySet()) {
      for (Object o : me.getValue()) {
        sb.append(me.getKey()).append(": ").append(o).append("\n");
      }
    }
    sb.append("\n");
    sb.append(getOutputString());
    return sb.toString();
  }

  /** Adds a cookie to the set of cookies in the response. */
  @Override
  public void addCookie(Cookie cookie)
  {
    this.cookies.add(cookie);
  }

  /** Gets the set of cookies stored in the response. */
  public Cookie[] getCookies()
  {
    return this.cookies.toArray(new Cookie[this.cookies.size()]);
  }

  /** Returns true if the specified header was placed in the response. */
  @Override
  public boolean containsHeader(String name)
  {
    return this.headers.containsKey(name);
  }

  /** Returns the URL unchanged. */
  @Override
  public String encodeURL(String url)
  {
    return url;
  }

  /** Returns the URL unchanged. */
  @Override
  public String encodeRedirectURL(String url)
  {
    return url;
  }

  /** Returns the URL unchanged. */
  @Override
  public String encodeUrl(String url)
  {
    return url;
  }

  /** Returns the URL unchanged. */
  @Override
  public String encodeRedirectUrl(String url)
  {
    return url;
  }

  /** Sets the status code and saves the message so it can be retrieved later. */
  @Override
  public void sendError(int status, String errorMessage) throws IOException
  {
    this.status = status;
    this.errorMessage = errorMessage;
  }

  /** Sets that status code to the error code provided. */
  @Override
  public void sendError(int status) throws IOException
  {
    this.status = status;
  }

  /** Simply stores the URL that was supplied, so that it can be examined later with getRedirectUrl. */
  @Override
  public void sendRedirect(String url) throws IOException
  {
    log.debug("sendRedirect: " + url);
    this.redirectUrl = url;
    this.status = 302;
    addHeader("Location", url);
  }

  /**
   * If a call was made to sendRedirect() this method will return the URL that was supplied. Otherwise it will return
   * null.
   */
  public String getRedirectUrl()
  {
    return this.redirectUrl;
  }

  /** Stores the value in a Long and saves it as a header. */
  @Override
  public void setDateHeader(String name, long value)
  {
    this.headers.remove(name);
    addDateHeader(name, value);
  }

  /** Adds the specified value for the named header (does not remove/replace existing values). */
  @Override
  public void addDateHeader(String name, long value)
  {
    List<Object> values = this.headers.get(name);
    if (values == null) {
      this.headers.put(name, values = new ArrayList<Object>());
    }
    values.add(value);
  }

  /** Sets the value of the specified header to the single value provided. */
  @Override
  public void setHeader(String name, String value)
  {
    this.headers.remove(name);
    addHeader(name, value);
  }

  /** Adds the specified value for the named header (does not remove/replace existing values). */
  @Override
  public void addHeader(String name, String value)
  {
    List<Object> values = this.headers.get(name);
    if (values == null) {
      this.headers.put(name, values = new ArrayList<Object>());
    }
    values.add(value);
  }

  /** Stores the value in an Integer and saves it as a header. */
  @Override
  public void setIntHeader(String name, int value)
  {
    this.headers.remove(name);
    addIntHeader(name, value);
  }

  /** Adds the specified value for the named header (does not remove/replace existing values). */
  @Override
  public void addIntHeader(String name, int value)
  {
    List<Object> values = this.headers.get(name);
    if (values == null) {
      this.headers.put(name, values = new ArrayList<Object>());
    }
    values.add(value);
  }

  /**
   * Provides access to all headers that were set. The format is a Map which uses the header name as the key, and stores
   * a List of Objects, one per header value. The Objects will be either Strings (if setHeader() was used), Integers (if
   * setIntHeader() was used) or Longs (if setDateHeader() was used).
   */
  public Map<String, List<Object>> getHeaderMap()
  {
    return this.headers;
  }

  /** Sets the HTTP Status code of the response. */
  @Override
  public void setStatus(int statusCode)
  {
    this.status = statusCode;
  }

  /** Saves the HTTP status code and the message provided. */
  @Override
  public void setStatus(int status, String errorMessage)
  {
    this.status = status;
    this.errorMessage = errorMessage;
  }

  /** Gets the status (or error) code if one was set. Defaults to 200 (HTTP OK). */
  @Override
  public int getStatus()
  {
    return this.status;
  }

  @Override
  public String getHeader(final String name)
  {
    return null;
  }

  @Override
  public Collection<String> getHeaders(final String name)
  {
    return null;
  }

  @Override
  public Collection<String> getHeaderNames()
  {
    return null;
  }

  /** Gets the error message if one was set with setStatus() or sendError(). */
  public String getErrorMessage()
  {
    return this.errorMessage;
  }

  /** Sets the character encoding on the request. */
  @Override
  public void setCharacterEncoding(String encoding)
  {
    this.characterEncoding = encoding;
  }

  /** Gets the character encoding (defaults to UTF-8). */
  @Override
  public String getCharacterEncoding()
  {
    return this.characterEncoding;
  }

  /** Sets the content type for the response. */
  @Override
  public void setContentType(String contentType)
  {
    this.contentType = contentType;
  }

  /** Gets the content type for the response. Defaults to text/html. */
  @Override
  public String getContentType()
  {
    return this.contentType;
  }

  /**
   * Returns a reference to a ServletOutputStream to be used for output. The output is captured and can be examined at
   * the end of a test run by calling getOutputBytes() or getOutputString().
   */
  @Override
  public ServletOutputStream getOutputStream() throws IOException
  {
    return this.out;
  }

  /**
   * Returns a reference to a PrintWriter to be used for character output. The output is captured and can be examined at
   * the end of a test run by calling getOutputBytes() or getOutputString().
   */
  @Override
  public PrintWriter getWriter() throws IOException
  {
    return this.writer;
  }

  /** Gets the output that was written to the output stream, as a byte[]. */
  public byte[] getOutputBytes()
  {
    this.writer.flush();
    return this.out.getBytes();
  }

  /** Gets the output that was written to the output stream, as a character String. */
  public String getOutputString()
  {
    this.writer.flush();
    return this.out.getString();
  }

  /** Sets a custom content length on the response. */
  @Override
  public void setContentLength(int contentLength)
  {
    this.contentLength = contentLength;
  }

  /** Returns the content length if one was set on the response by calling setContentLength(). */
  public int getContentLength()
  {
    return this.contentLength;
  }

  /** Has no effect. */
  @Override
  public void setBufferSize(int i)
  {
  }

  /** Always returns 0. */
  @Override
  public int getBufferSize()
  {
    return 0;
  }

  /** Has no effect. */
  @Override
  public void flushBuffer() throws IOException
  {
  }

  /** Always throws IllegalStateException. */
  @Override
  public void resetBuffer()
  {
    throw new IllegalStateException("reset() is not supported");
  }

  /** Always returns true. */
  @Override
  public boolean isCommitted()
  {
    return true;
  }

  /** Always throws an IllegalStateException. */
  @Override
  public void reset()
  {
    throw new IllegalStateException("reset() is not supported");
  }

  /** Sets the response locale to the one specified. */
  @Override
  public void setLocale(Locale locale)
  {
    this.locale = locale;
  }

  /** Gets the response locale. Default to the system default locale. */
  @Override
  public Locale getLocale()
  {
    return this.locale;
  }

}
