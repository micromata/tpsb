package de.micromata.genome.tpsb.soapui;

import java.util.List;

import com.eviware.soapui.impl.wsdl.submit.RequestFilter;
import com.eviware.soapui.impl.wsdl.submit.transports.http.HttpClientRequestTransport;
import com.eviware.soapui.model.iface.Request;
import com.eviware.soapui.model.iface.Response;
import com.eviware.soapui.model.iface.SubmitContext;

import de.micromata.genome.util.bean.PrivateBeanUtils;

/**
 * Delegate SoapUI transport.
 * 
 * @author Roger Rene Kommer (r.kommer.extern@micromata.de)
 * 
 */
public class DelegateHttpClientRequestTransport extends HttpClientRequestTransport
{
  HttpClientRequestTransport target;

  public DelegateHttpClientRequestTransport(HttpClientRequestTransport target)
  {
    this.target = target;
  }

  @Override
  public int hashCode()
  {
    return target.hashCode();
  }

  @Override
  public boolean equals(Object obj)
  {
    return target.equals(obj);
  }

  @Override
  public void addRequestFilter(RequestFilter filter)
  {
    target.addRequestFilter(filter);
  }

  @Override
  public void removeRequestFilter(RequestFilter filter)
  {
    target.removeRequestFilter(filter);
  }

  public List<RequestFilter> getFilters()
  {
    return (List<RequestFilter>) PrivateBeanUtils.readField(target, "filters");
  }

  @Override
  public <T> void removeRequestFilter(Class<T> filterClass)
  {
    target.removeRequestFilter(filterClass);
  }

  @Override
  public <T> void replaceRequestFilter(Class<T> filterClass, RequestFilter newFilter)
  {
    target.replaceRequestFilter(filterClass, newFilter);
  }

  @Override
  public <T> RequestFilter findFilterByType(Class<T> filterType)
  {
    return target.findFilterByType(filterType);
  }

  @Override
  public void abortRequest(SubmitContext submitContext)
  {
    target.abortRequest(submitContext);
  }

  @Override
  public Response sendRequest(SubmitContext submitContext, Request request) throws Exception
  {
    return target.sendRequest(submitContext, request);
  }

  @Override
  public String toString()
  {
    return target.toString();
  }

}
