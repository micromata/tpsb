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

package de.micromata.genome.tpsb.soapui;

import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolVersion;
import org.apache.http.client.CookieStore;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicStatusLine;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import com.eviware.soapui.SoapUI;
import com.eviware.soapui.impl.rest.RestRequestInterface.RequestMethod;
import com.eviware.soapui.impl.rest.support.RestParamProperty;
import com.eviware.soapui.impl.support.AbstractHttpRequestInterface;
import com.eviware.soapui.impl.wsdl.AbstractWsdlModelItem;
import com.eviware.soapui.impl.wsdl.WsdlProject;
import com.eviware.soapui.impl.wsdl.submit.RequestFilter;
import com.eviware.soapui.impl.wsdl.submit.transports.http.BaseHttpRequestTransport;
import com.eviware.soapui.impl.wsdl.submit.transports.http.ExtendedHttpMethod;
import com.eviware.soapui.impl.wsdl.submit.transports.http.HttpClientRequestTransport;
import com.eviware.soapui.impl.wsdl.submit.transports.http.SinglePartHttpResponse;
import com.eviware.soapui.impl.wsdl.submit.transports.http.support.attachments.MimeMessageResponse;
import com.eviware.soapui.impl.wsdl.submit.transports.http.support.methods.ExtendedGetMethod;
import com.eviware.soapui.impl.wsdl.submit.transports.http.support.methods.ExtendedPostMethod;
import com.eviware.soapui.impl.wsdl.support.PathUtils;
import com.eviware.soapui.impl.wsdl.support.http.HttpClientSupport;
import com.eviware.soapui.impl.wsdl.support.http.SoapUIHttpRoute;
import com.eviware.soapui.impl.wsdl.support.wss.WssCrypto;
import com.eviware.soapui.impl.wsdl.teststeps.HttpTestRequest;
import com.eviware.soapui.model.iface.Request;
import com.eviware.soapui.model.iface.Response;
import com.eviware.soapui.model.iface.SubmitContext;
import com.eviware.soapui.model.propertyexpansion.PropertyExpander;
import com.eviware.soapui.model.settings.Settings;
import com.eviware.soapui.model.support.ModelSupport;
import com.eviware.soapui.settings.HttpSettings;
import com.eviware.soapui.support.types.StringToStringMap;
import com.eviware.soapui.support.types.StringToStringsMap;

import de.micromata.genome.tpsb.httpmockup.MockHttpServletResponse;
import de.micromata.genome.util.bean.PrivateBeanUtils;

/**
 * SoapUI adapter to call a local servlet.
 * 
 * @author Roger Rene Kommer (r.kommer.extern@micromata.de)
 * 
 */
public class DelegateToSoapUiTestBuilderHttpClientRequestTransport extends DelegateHttpClientRequestTransport
{
  protected SoapUiTestBuilder<?> testBuilder;

  public DelegateToSoapUiTestBuilderHttpClientRequestTransport(HttpClientRequestTransport target,
      SoapUiTestBuilder<?> testBuilder)
  {
    super(target);
    this.testBuilder = testBuilder;
  }

  /**
   * Fitlers the request data before sending to target.
   * 
   * Default returns unmodified.
   * 
   * @param data the data
   * @return the byte[]
   */
  protected byte[] filterRequestData(byte[] data)
  {
    return data;
  }

  /**
   * Filters the reponse data.
   * 
   * Default returns unmodified.
   * 
   * @param data the data
   * @return the byte[]
   */
  protected byte[] filterResponseData(byte[] data)
  {
    return data;
  }

  /**
   * Filters the http response.
   * 
   * Default returns unmodified.
   * 
   * @param httpresponse the httpresponse
   * @return the basic http response
   */
  protected BasicHttpResponse filterBasicHttpResponse(BasicHttpResponse httpresponse)
  {
    return httpresponse;

  }

  private HttpResponse execute(ExtendedHttpMethod method, HttpContext httpContext,
      Map<String, String> httpRequestParameter) throws Exception
  {
    boolean passtoremote = false;
    if (passtoremote == true) {
      return HttpClientSupport.execute(method, httpContext);

    }
    byte[] reqData = null;
    if (method.getRequestEntity() != null && method.getRequestEntity().getContent() != null) {
      reqData = filterRequestData(IOUtils.toByteArray(method.getRequestEntity().getContent()));
    }
    Header[] soaphaedera = method.getHeaders("SOAPAction");
    String soapAction = "";
    if (soaphaedera != null && soaphaedera.length > 0) {
      soapAction = method.getHeaders("SOAPAction")[0].getValue();
    }
    String uri = method.getURI().toString();
    //    testBuilder.initWithUri(uri);
    testBuilder//
        .createNewPostRequest() //
        .initWithUri(uri)
        .setRequestMethod(method.getMethod())
        .setRequestData(reqData);
    if (StringUtils.isNotBlank(soapAction) == true) {
      testBuilder.addRequestHeader("SOAPAction", soapAction);
    }
    Header[] allHeaders = method.getAllHeaders();
    for (Header h : allHeaders) {
      testBuilder.addRequestHeader(h.getName(), h.getValue());
    }
    httpRequestParameter.forEach((k, v) -> testBuilder.getHttpRequest().addRequestParameter(k, v));
    MockHttpServletResponse httpr = testBuilder.executeServletRequest() //
        .getHttpResponse();

    byte[] respData = filterResponseData(httpr.getOutputBytes());
    //    String outp = httpr.getOutputString();
    BasicStatusLine statusLine = new BasicStatusLine(new ProtocolVersion("http", 1, 1), httpr.getStatus(), null);
    BasicHttpResponse httpResponse = new BasicHttpResponse(statusLine);
    httpResponse.setEntity(new ByteArrayEntity(respData));
    httpResponse = filterBasicHttpResponse(httpResponse);
    //        WsdlSinglePartHttpResponse wsdls = new WsdlSinglePartHttpResponse();
    method.setHttpResponse(httpResponse);
    try {
      method.setURI(new URI("http://localhost/dummy"));
    } catch (URISyntaxException ex) {
      throw new RuntimeException(ex);
    }
    return httpResponse;
  }

  @Override
  public Response sendRequest(SubmitContext submitContext, Request request) throws Exception
  {
    boolean useSuper = false;

    if (useSuper == true) {
      return super.sendRequest(submitContext, request);
    }
    AbstractHttpRequestInterface<?> httpRequest = (AbstractHttpRequestInterface<?>) request;
    RequestMethod rm = httpRequest.getMethod();

    ExtendedHttpMethod httpMethod;
    switch (rm) {
      case POST: {
        ExtendedPostMethod extendedPostMethod = new ExtendedPostMethod();
        extendedPostMethod.setAfterRequestInjection(httpRequest.getAfterRequestInjection());
        httpMethod = extendedPostMethod;
        break;
      }
      case GET: {
        ExtendedGetMethod extendedGetMethod = new ExtendedGetMethod();
        //        extendedGetMethod.setAfterRequestInjection(httpRequest.getAfterRequestInjection());
        httpMethod = extendedGetMethod;
        break;
      }
      default:
        throw new IllegalArgumentException("Unsupported HTTP methd: " + rm);
    }

    HttpClientSupport.SoapUIHttpClient httpClient = HttpClientSupport.getHttpClient();

    boolean createdContext = false;
    HttpContext httpContext = (HttpContext) submitContext.getProperty(SubmitContext.HTTP_STATE_PROPERTY);
    if (httpContext == null) {
      httpContext = new BasicHttpContext();
      submitContext.setProperty(SubmitContext.HTTP_STATE_PROPERTY, httpContext);
      createdContext = true;

      // always use local cookie store so we don't share cookies with other threads/executions/requests
      CookieStore cookieStore = new BasicCookieStore();
      httpContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
    }

    String localAddress = System.getProperty("soapui.bind.address",
        httpRequest.getBindAddress());
    if (localAddress == null || localAddress.trim().length() == 0) {
      localAddress = SoapUI.getSettings().getString(HttpSettings.BIND_ADDRESS, null);
    }

    org.apache.http.HttpResponse httpResponse = null;
    if (localAddress != null && localAddress.trim().length() > 0) {
      try {
        httpMethod.getParams().setParameter(ConnRoutePNames.LOCAL_ADDRESS, InetAddress.getByName(localAddress));
      } catch (Exception e) {
        SoapUI.logError(e, "Failed to set localAddress to [" + localAddress + "]");
      }
    }
    Map<String, String> httpRequestParameter = new HashMap<>();
    if (httpRequest instanceof HttpTestRequest) {
      HttpTestRequest tr = (HttpTestRequest) httpRequest;
      for (String key : tr.getParams().keySet()) {
        RestParamProperty val = tr.getParams().get(key);
        String sval = val.getValue();
        sval = PropertyExpander.expandProperties(submitContext, sval);
        httpRequestParameter.put(key, sval);
      }
    }

    submitContext.removeProperty(RESPONSE);
    submitContext.setProperty(HTTP_METHOD, httpMethod);
    submitContext.setProperty(POST_METHOD, httpMethod);
    submitContext.setProperty(HTTP_CLIENT, httpClient);
    submitContext.setProperty(REQUEST_CONTENT, httpRequest.getRequestContent());
    submitContext.setProperty(WSDL_REQUEST, httpRequest);
    submitContext.setProperty(RESPONSE_PROPERTIES, new StringToStringMap());
    List<RequestFilter> filters = getFilters();
    for (RequestFilter filter : filters) {
      filter.filterRequest(submitContext, httpRequest);
    }

    try {
      Settings settings = httpRequest.getSettings();

      // custom http headers last so they can be overridden
      StringToStringsMap headers = httpRequest.getRequestHeaders();

      // first remove so we don't get any unwanted duplicates
      for (String header : headers.keySet()) {
        httpMethod.removeHeaders(header);
      }

      // now add
      for (String header : headers.keySet()) {
        for (String headerValue : headers.get(header)) {
          headerValue = PropertyExpander.expandProperties(submitContext, headerValue);
          httpMethod.addHeader(header, headerValue);
        }
      }

      // do request
      WsdlProject project = (WsdlProject) ModelSupport.getModelItemProject(httpRequest);
      WssCrypto crypto = null;
      if (project != null && project.getWssContainer() != null) {
        crypto = project.getWssContainer()
            .getCryptoByName(PropertyExpander.expandProperties(submitContext, httpRequest.getSslKeystore()));
      }

      if (crypto != null && WssCrypto.STATUS_OK.equals(crypto.getStatus())) {
        httpMethod.getParams().setParameter(SoapUIHttpRoute.SOAPUI_SSL_CONFIG,
            crypto.getSource() + " " + crypto.getPassword());
      }

      // dump file?
      httpMethod.setDumpFile(
          PathUtils.expandPath(httpRequest.getDumpFile(), (AbstractWsdlModelItem<?>) httpRequest, submitContext));

      // include request time?
      if (settings.getBoolean(HttpSettings.INCLUDE_REQUEST_IN_TIME_TAKEN)) {
        httpMethod.initStartTime();
      }

      if (httpMethod.getMetrics() != null) {
        httpMethod.getMetrics().setHttpMethod(httpMethod.getMethod());
        PrivateBeanUtils.invokeMethod(this, "captureMetrics", httpMethod, httpClient);
        httpMethod.getMetrics().getTotalTimer().start();
      }

      // submit!
      httpResponse = execute(httpMethod, httpContext, httpRequestParameter);

      if (httpMethod.getMetrics() != null) {
        httpMethod.getMetrics().getReadTimer().stop();
        httpMethod.getMetrics().getTotalTimer().stop();
      }

      //          if (isRedirectResponse(httpResponse.getStatusLine().getStatusCode()) && httpRequest.isFollowRedirects()) {
      //            if (httpResponse.getEntity() != null) {
      //              EntityUtils.consume(httpResponse.getEntity());
      //            }
      //
      //            ExtendedGetMethod returnMethod = followRedirects(httpClient, 0, httpMethod, httpResponse, httpContext);
      //            httpMethod = returnMethod;
      //            submitContext.setProperty(HTTP_METHOD, httpMethod);
      //          }
    } catch (Throwable t) { // NOSONAR "Illegal Catch" framework
      httpMethod.setFailed(t);

      if (t instanceof Exception) {
        throw (Exception) t;
      }

      SoapUI.logError(t);
      throw new Exception(t);
    } finally {
      if (!httpMethod.isFailed()) {
        if (httpMethod.getMetrics() != null) {
          if (httpMethod.getMetrics().getReadTimer().getStop() == 0) {
            httpMethod.getMetrics().getReadTimer().stop();
          }
          if (httpMethod.getMetrics().getTotalTimer().getStop() == 0) {
            httpMethod.getMetrics().getTotalTimer().stop();
          }
        }
      } else {
        httpMethod.getMetrics().reset();
        httpMethod.getMetrics().setTimestamp(System.currentTimeMillis());
        //            captureMetrics(httpMethod, httpClient);
      }
      for (int c = filters.size() - 1; c >= 0; c--) {
        RequestFilter filter = filters.get(c);
        filter.afterRequest(submitContext, httpRequest);
      }

      if (!submitContext.hasProperty(RESPONSE)) {
        createDefaultResponse(submitContext, httpRequest, httpMethod);
      }

      Response response = (Response) submitContext.getProperty(BaseHttpRequestTransport.RESPONSE);
      StringToStringMap responseProperties = (StringToStringMap) submitContext
          .getProperty(BaseHttpRequestTransport.RESPONSE_PROPERTIES);

      for (String key : responseProperties.keySet()) {
        response.setProperty(key, responseProperties.get(key));
      }

      if (createdContext) {
        submitContext.setProperty(SubmitContext.HTTP_STATE_PROPERTY, null);
      }
    }

    return (Response) submitContext.getProperty(BaseHttpRequestTransport.RESPONSE);
  }

  private void createDefaultResponse(SubmitContext submitContext, AbstractHttpRequestInterface<?> httpRequest,
      ExtendedHttpMethod httpMethod)
  {
    String requestContent = (String) submitContext.getProperty(BaseHttpRequestTransport.REQUEST_CONTENT);

    // check content-type for multiplart
    String responseContentTypeHeader = null;
    if (httpMethod.hasHttpResponse() && httpMethod.getHttpResponse().getEntity() != null) {
      Header h = httpMethod.getHttpResponse().getEntity().getContentType();
      responseContentTypeHeader = h.toString();
    }

    Response response = null;

    if (responseContentTypeHeader != null && responseContentTypeHeader.toUpperCase().startsWith("MULTIPART")) {
      response = new MimeMessageResponse(httpRequest, httpMethod, requestContent, submitContext);
    } else {
      response = new SinglePartHttpResponse(httpRequest, httpMethod, requestContent, submitContext);
    }

    submitContext.setProperty(BaseHttpRequestTransport.RESPONSE, response);
  }

  public SoapUiTestBuilder<?> getTestBuilder()
  {
    return testBuilder;
  }

  public void setTestBuilder(SoapUiTestBuilder<?> testBuilder)
  {
    this.testBuilder = testBuilder;
  }

}
