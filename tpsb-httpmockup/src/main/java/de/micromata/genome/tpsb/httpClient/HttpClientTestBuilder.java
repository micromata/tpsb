package de.micromata.genome.tpsb.httpClient;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;

import de.micromata.genome.tpsb.CommonTestBuilder;

/**
 * Http Client builder.
 * 
 * @author Roger Rene Kommer (r.kommer.extern@micromata.de)
 * 
 */
public class HttpClientTestBuilder extends CommonTestBuilder<HttpClientTestBuilder>
{
  private HttpClient httpClient;

  private int connectionTimeout = 1000;

  private int readTimeout = 30000;

  private int maxTotalConnection = 2000;

  private int maxPerHostConnection = 2000;

  private int lastHttpStatus;

  public HttpClientTestBuilder createHttpClient()
  {
    MultiThreadedHttpConnectionManager conManager = new MultiThreadedHttpConnectionManager();
    HttpConnectionManagerParams params = conManager.getParams();
    params.setConnectionTimeout(connectionTimeout);
    params.setSoTimeout(readTimeout);
    params.setMaxTotalConnections(maxTotalConnection);

    httpClient = new HttpClient(conManager);

    params.setMaxConnectionsPerHost(httpClient.getHostConfiguration(), maxPerHostConnection);
    return getBuilder();

  }

  private void initHttpClient()
  {
    if (httpClient != null) {
      return;
    }
    createHttpClient();
  }

  public HttpClientTestBuilder executeMethod(HttpMethod method)
  {
    initHttpClient();
    try {
      lastHttpStatus = httpClient.executeMethod(method);
    } catch (RuntimeException ex) {
      throw ex;
    } catch (Exception ex) {
      throw new RuntimeException(ex);
    }
    return getBuilder();
  }

  public HttpClient getHttpClient()
  {
    return httpClient;
  }

  public HttpClientTestBuilder setHttpClient(HttpClient httpClient)
  {
    this.httpClient = httpClient;
    return getBuilder();
  }

  public int getConnectionTimeout()
  {
    return connectionTimeout;
  }

  public HttpClientTestBuilder setConnectionTimeout(int connectionTimeout)
  {
    this.connectionTimeout = connectionTimeout;
    return getBuilder();
  }

  public int getReadTimeout()
  {
    return readTimeout;
  }

  public HttpClientTestBuilder setReadTimeout(int readTimeout)
  {
    this.readTimeout = readTimeout;
    return getBuilder();
  }

  public int getMaxTotalConnection()
  {
    return maxTotalConnection;
  }

  public HttpClientTestBuilder setMaxTotalConnection(int maxTotalConnection)
  {
    this.maxTotalConnection = maxTotalConnection;
    return getBuilder();
  }

  public int getMaxPerHostConnection()
  {
    return maxPerHostConnection;
  }

  public HttpClientTestBuilder setMaxPerHostConnection(int maxPerHostConnection)
  {
    this.maxPerHostConnection = maxPerHostConnection;
    return getBuilder();
  }
}
