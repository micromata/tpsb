package de.micromata.genome.tpsb.httpmockup;

import java.util.Map;

/**
 * Writable request.
 * 
 * @author Roger Rene Kommer (r.kommer.extern@micromata.de)
 *
 */
public interface HttpRequestMockupBase
{
  String getContextPath();

  void setServletPath(String servletPath);

  void setQueryString(String queryString);

  void setPathInfo(String pathInfo);

  String getQueryString();

  Map<String, String[]> getParameterMap();
}
