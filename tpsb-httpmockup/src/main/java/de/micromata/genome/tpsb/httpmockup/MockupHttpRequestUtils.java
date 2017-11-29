package de.micromata.genome.tpsb.httpmockup;

import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * 
 * @author Roger Rene Kommer (r.kommer.extern@micromata.de)
 *
 */
public class MockupHttpRequestUtils
{
  public static void initWithUri(HttpRequestMockupBase httpRequest, String baseUrl, String servletPrefix, String uri)
  {
    String urip = uri;
    int par = urip.indexOf('?');
    if (par != -1) {
      String requeststr = urip.substring(par);
      urip = urip.substring(0, par);
      httpRequest.setQueryString(requeststr);
      parseQueryStringToParameters(httpRequest);
    }

    if (urip.startsWith(baseUrl) == true) {
      String spath = urip.substring(baseUrl.length());

      String ctxpath = httpRequest.getContextPath();
      if (spath.startsWith(ctxpath) == true) {
        httpRequest.setServletPath(servletPrefix);
        String pathInfo = spath;
        if (ctxpath.length() > 1) {
          pathInfo = spath.substring(ctxpath.length());
        }
        if (servletPrefix.equals("") == false && pathInfo.startsWith(servletPrefix) == true) {
          pathInfo = pathInfo.substring(servletPrefix.length());
        }
        httpRequest.setPathInfo(pathInfo);
      } else {
        httpRequest.setServletPath(spath);
      }

    }
  }

  public static void parseRequestUrlToRequest(HttpRequestMockupBase httpRequest, String url)
  {
    String rest = url;
    if (rest.startsWith("http://") == true) {
      rest = rest.substring("http://".length());
    } else if (rest.startsWith("https://") == true) {
      rest = rest.substring("https://".length());
    }
    String servletPath = "/";
    int startpath = rest.indexOf('/');
    if (startpath != -1) {
      rest = rest.substring(startpath);
      servletPath = rest;
    }
    if (servletPath.startsWith(httpRequest.getContextPath()) == true) {
      servletPath = servletPath.substring(httpRequest.getContextPath().length());
    }
    int queryidx = servletPath.indexOf('?');
    if (queryidx != -1) {
      String qs = servletPath.substring(queryidx);
      servletPath = servletPath.substring(0, queryidx);
      httpRequest.setQueryString(qs);
      MockupHttpRequestUtils.parseQueryStringToParameters(httpRequest);
    }
    httpRequest.setServletPath(servletPath);
  }

  public static void parseQueryStringToParameters(HttpRequestMockupBase httpRequest)
  {
    String queryString = httpRequest.getQueryString();
    if (StringUtils.isBlank(queryString) == true) {
      return;
    }
    String rest = queryString;
    if (rest.startsWith("?") == true) {
      rest = rest.substring(1);
    }
    int nextTk = rest.indexOf('&');
    while (nextTk != -1) {
      String kv = rest.substring(0, nextTk);
      parseParamsKeyValue(httpRequest, kv);
      rest = rest.substring(nextTk + 1);
      nextTk = rest.indexOf('&');
    }
    parseParamsKeyValue(httpRequest, rest);
  }

  private static void parseParamsKeyValue(HttpRequestMockupBase httpRequest, String kv)
  {
    if (StringUtils.isBlank(kv) == true) {
      return;
    }
    int as = kv.indexOf('=');
    String k = kv;
    String v = "";
    if (as != -1) {
      k = kv.substring(0, as);
      v = kv.substring(as + 1);
    }
    addParameter(httpRequest, k, v);
  }

  public static void addParameter(HttpRequestMockupBase httpRequest, String k, String v)
  {
    Map<String, String[]> parameters = httpRequest.getParameterMap();
    String[] va = parameters.get(k);
    if (va == null) {
      parameters.put(k, new String[] { v });
    } else {
      parameters.put(k, ArrayUtils.add(va, v));
    }
  }

}
