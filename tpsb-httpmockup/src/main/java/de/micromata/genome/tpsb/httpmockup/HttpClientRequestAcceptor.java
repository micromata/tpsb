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

import de.micromata.genome.tpsb.httpClient.HttpClientTestBuilder;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import javax.servlet.ServletException;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;

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

  private HttpPost buildMethod(MockHttpServletRequest request)
  {
    HttpPost ret;
    if (StringUtils.equals(request.getMethod(), "POST") == true) {
      HttpPost pm = new HttpPost(baseUrl + request.getPathInfo());
      pm.setEntity(new ByteArrayEntity(request.getRequestData()));
      ret = pm;
    } else {
      throw new UnsupportedOperationException("Currently only POST methods are supported");
    }
    return ret;
  }

  @Override
  public void acceptRequest(MockHttpServletRequest request, MockHttpServletResponse response) throws IOException, ServletException
  {
    HttpPost method = buildMethod(request);
    httpClient.executeMethod(method);
    response.setStatus(httpClient.getLastHttpStatus());
    byte[] data = httpClient.getLastResponseBody();
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
