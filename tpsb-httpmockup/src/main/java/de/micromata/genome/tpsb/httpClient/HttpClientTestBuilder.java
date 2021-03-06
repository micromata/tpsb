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

package de.micromata.genome.tpsb.httpClient;


import de.micromata.genome.tpsb.CommonTestBuilder;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;

/**
 * Http Client builder.
 * 
 * @author Roger Rene Kommer (r.kommer.extern@micromata.de)
 * 
 */
public class HttpClientTestBuilder extends CommonTestBuilder<HttpClientTestBuilder>
{
  private CloseableHttpClient httpClient;

  private int connectionTimeout = 1000;

  private int readTimeout = 30000;

  private int maxTotalConnection = 2000;

  private int maxPerHostConnection = 2000;

  private int lastHttpStatus;

  private byte[] lastResponseBody;

  public HttpClientTestBuilder createHttpClient() {
    PoolingHttpClientConnectionManager conManager = new PoolingHttpClientConnectionManager();

    conManager.setMaxTotal(maxTotalConnection);
    conManager.setDefaultMaxPerRoute(maxPerHostConnection);

    final RequestConfig requestConfig = RequestConfig.custom()
        .setConnectTimeout(connectionTimeout)
        .setSocketTimeout(readTimeout).build();
    final HttpClientBuilder httpClientBuilder = HttpClients.custom().setConnectionManager(conManager) //
        .setDefaultRequestConfig(requestConfig);

    httpClient = httpClientBuilder.build();

    return getBuilder();

  }

  private void initHttpClient()
  {
    if (httpClient != null) {
      return;
    }
    createHttpClient();
  }

  public HttpClientTestBuilder executeMethod(HttpRequestBase method)
  {
    initHttpClient();
    try {
      final CloseableHttpResponse response = httpClient.execute(method);
      lastResponseBody = EntityUtils.toByteArray(response.getEntity());
      lastHttpStatus = response.getStatusLine().getStatusCode();
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

  public HttpClientTestBuilder setHttpClient(CloseableHttpClient httpClient)
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

  public int getLastHttpStatus() {
    return lastHttpStatus;
  }

  public byte[] getLastResponseBody() {
    return lastResponseBody;
  }
}
