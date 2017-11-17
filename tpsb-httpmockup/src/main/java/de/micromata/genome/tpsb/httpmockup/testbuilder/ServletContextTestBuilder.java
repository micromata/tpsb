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

package de.micromata.genome.tpsb.httpmockup.testbuilder;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.http.HttpServlet;

import org.apache.commons.codec.binary.StringUtils;
import org.apache.commons.lang3.CharEncoding;

import de.micromata.genome.tpsb.CommonTestBuilder;
import de.micromata.genome.tpsb.annotations.TpsbBuilder;
import de.micromata.genome.tpsb.annotations.TpsbIgnore;
import de.micromata.genome.tpsb.httpmockup.HttpClientRequestAcceptor;
import de.micromata.genome.tpsb.httpmockup.MockFilterMapDef.FilterDispatchFlags;
import de.micromata.genome.tpsb.httpmockup.MockHttpServletRequest;
import de.micromata.genome.tpsb.httpmockup.MockHttpServletResponse;
import de.micromata.genome.tpsb.httpmockup.MockServletContext;
import de.micromata.genome.tpsb.httpmockup.RequestAcceptor;
import de.micromata.genome.util.types.Pair;

/**
 * Creates an execution framework for servlets.
 * 
 * One servlet context currently can only have one servlet registered, but multiple filterfs.
 * 
 * See also https://team.micromata.de/confluence/display/genome/Modul+tpsb-httpmockup
 * 
 * @author Roger Rene Kommer (r.kommer.extern@micromata.de)
 * 
 */
@TpsbBuilder
public class ServletContextTestBuilder<T extends ServletContextTestBuilder<?>> extends CommonTestBuilder<T>
{
  protected MockServletContext servletContext = new MockServletContext("unittest");

  protected RequestAcceptor acceptor = servletContext;

  protected MockHttpServletRequest httpRequest = new MockHttpServletRequest(servletContext);

  protected MockHttpServletResponse httpResponse;

  protected List<Pair<String, String>> keyValuesToPairList(String... initParams)
  {
    if ((initParams.length % 2) != 0) {
      throw new IllegalArgumentException("initParams has to be even (key, values)");
    }
    List<Pair<String, String>> ret = new ArrayList<Pair<String, String>>();
    for (int i = 0; i < initParams.length; ++i) {
      ret.add(Pair.make(initParams[i], initParams[i + 1]));
      ++i;
    }
    return ret;
  }

  /**
   * Register the servlet all requests will dispatched.
   * 
   * @param servletClass
   * @param name
   * @param initParams
   * @return
   */
  public T registerServlet(String name, String path, Class<? extends HttpServlet> servletClass, String... initParams)
  {
    Map<String, String> ips = new HashMap<String, String>();

    for (Pair<String, String> me : keyValuesToPairList(initParams)) {
      ips.put(me.getFirst(), me.getSecond());
    }
    servletContext.addServlet(name, servletClass, ips);
    servletContext.addServletMapping(name, path);
    return getBuilder();
  }

  public T keepSession(boolean keep)
  {
    servletContext.keepSession(keep);
    return getBuilder();
  }

  /**
   * Register a servlet, all requests will passed through.
   * 
   * @param filterClass
   * @param name
   * @param initParams
   * @return
   */
  public T registerFilter(String name, String path, Class<? extends Filter> filterClass, String... initParams)
  {
    Map<String, String> ips = new HashMap<String, String>();
    for (Pair<String, String> me : keyValuesToPairList(initParams)) {
      ips.put(me.getFirst(), me.getSecond());
    }

    servletContext.addFilter(name, filterClass, ips);
    servletContext.addFilterMapping(name, path, FilterDispatchFlags.REQUEST.getFlags());
    return getBuilder();
  }

  /**
   * Creates an fresh POST request.
   * 
   * @param urlParams the url params as key value list.
   * @return the builder
   */
  public T createNewPostRequest(String... urlParams)
  {

    httpRequest = new MockHttpServletRequest(servletContext);
    StringBuilder sb = new StringBuilder();
    for (Pair<String, String> me : keyValuesToPairList(urlParams)) {
      if (sb.length() > 0) {
        sb.append("&");
      }
      try {
        sb.append(URLEncoder.encode(me.getFirst(), CharEncoding.UTF_8)).append("=")
            .append(URLEncoder.encode(me.getSecond(), CharEncoding.UTF_8));
      } catch (UnsupportedEncodingException e) {
        throw new RuntimeException(e);
      }
    }
    httpRequest.setQueryString(sb.toString());
    return getBuilder();
  }

  /**
   * If remote url is set, HttpClient will be used to send requests.
   * 
   * @param baseUrl base url without servlet path.
   * @return
   */
  public T setRemoteUrl(String baseUrl)
  {
    acceptor = new HttpClientRequestAcceptor(baseUrl);
    return getBuilder();
  }

  /**
   * sets the HTTP method.
   * 
   * @param method
   * @return
   */
  public T setRequestMethod(String method)
  {
    httpRequest.setMethod(method);
    return getBuilder();
  }

  public T dumpResponse()
  {
    System.out.println(httpResponse.toString());
    return getBuilder();
  }

  /**
   * Add a request header.
   * 
   * @param key
   * @param value
   * @return
   */
  public T addRequestHeader(String key, String value)
  {
    httpRequest.addHeader(key, value);
    return getBuilder();
  }

  /**
   * Sets the request data as utf-8 bytes.
   * 
   * @param data
   * @return
   */
  public T setRequestData(String data)
  {
    httpRequest.setRequestData(StringUtils.getBytesUtf8(data));
    return getBuilder();
  }

  /**
   * Sets the request data as utf-8 bytes.
   * 
   * @param data
   * @return
   */
  @TpsbIgnore
  public T setRequestData(byte[] data)
  {
    httpRequest.setRequestData(data);
    return getBuilder();
  }

  public T setContextPath(String contextPath)
  {
    servletContext.setContextPath(contextPath);
    return getBuilder();
  }

  /**
   * Executes the Http request and store response in httpResponse.
   * 
   * @return
   */
  public T executeServletRequest()
  {
    try {
      httpResponse = new MockHttpServletResponse();

      acceptor.acceptRequest(httpRequest, httpResponse);
      return getBuilder();
    } catch (RuntimeException ex) {
      ex.printStackTrace();
      throw ex;
    } catch (Exception ex) {
      throw new RuntimeException(ex);
    }
  }

  /**
   * Check if http status in response is given.
   * 
   * @param status
   * @return
   */
  public T validateResponseStatus(int status)
  {
    if (httpResponse.getStatus() != status) {
      fail("Expect http status " + status + "; got " + httpResponse.getStatus());
    }
    return getBuilder();
  }

  /**
   * Delete session associated.
   * 
   * @return
   */
  public T destroySession()
  {
    servletContext.setSession(null);
    return getBuilder();
  }

  @TpsbIgnore
  public byte[] getResponseData()
  {
    return httpResponse.getOutputBytes();
  }

  public MockServletContext getServletContext()
  {
    return servletContext;
  }

  public void setServletContext(MockServletContext servletContext)
  {
    this.servletContext = servletContext;
  }

  public MockHttpServletRequest getHttpRequest()
  {
    return httpRequest;
  }

  public void setHttpRequest(MockHttpServletRequest httpRequest)
  {
    this.httpRequest = httpRequest;
  }

  public MockHttpServletResponse getHttpResponse()
  {
    return httpResponse;
  }

  public void setHttpResponse(MockHttpServletResponse httpResponse)
  {
    this.httpResponse = httpResponse;
  }
}
