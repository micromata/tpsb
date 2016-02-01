package de.micromata.genome.tpsb.httpmockup;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.servlet.ServletException;

import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.ByteArrayRequestEntity;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import de.micromata.genome.tpsb.httpClient.HttpClientTestBuilder;

/**
 * RequestAcceptor implemented by HttpClient.
 * 
 * @author Roger Rene Kommer (r.kommer.extern@micromata.de)
 * 
 */
public class HttpClientRequestAcceptor implements RequestAcceptor
{
  static HttpClientTestBuilder httpClient = new HttpClientTestBuilder();

  private String baseUrl;

  public HttpClientRequestAcceptor(String baseUrl)
  {
    this.baseUrl = baseUrl;
  }

  private HttpMethod buildMethod(MockHttpServletRequest request)
  {
    HttpMethod ret;
    if (StringUtils.equals(request.getMethod(), "POST") == true) {
      PostMethod pm = new PostMethod(baseUrl + request.getPathInfo());
      pm.setRequestEntity(new ByteArrayRequestEntity(request.getRequestData()));
      ret = pm;
    } else {
      throw new UnsupportedOperationException("Currently only POST methods are supported");
    }
    return ret;
  }

  @Override
  public void acceptRequest(MockHttpServletRequest request, MockHttpServletResponse response) throws IOException, ServletException
  {
    HttpMethod method = buildMethod(request);
    httpClient.executeMethod(method);
    response.setStatus(method.getStatusCode());
    byte[] data = method.getResponseBody();
    if (data != null) {
      IOUtils.copy(new ByteArrayInputStream(data), response.getOutputStream());
    }
  }

  public String getBaseUrl()
  {
    return baseUrl;
  }

  public void setBaseUrl(String baseUrl)
  {
    this.baseUrl = baseUrl;
  }
}
