package de.micromata.genome.tpsb.httpmockup;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequestWrapper;

/**
 * Mock implementation of a RequesetDispatcher used for testing purposes. Note that the mock implementation does not support actually
 * forwarding the request, or including other resources. The methods are implemented to record that a forward/include took place and then
 * simply return.
 * 
 * @author Tim Fennell
 * @since Stripes 1.1.1
 */
public class MockRequestDispatcher implements RequestDispatcher
{
  private String url;

  /** Constructs a request dispatcher, giving it a handle to the creating request. */
  public MockRequestDispatcher(String url)
  {
    this.url = url;
  }

  /** Simply stores the URL that was requested for forward, and returns. */
  @Override
  public void forward(ServletRequest req, ServletResponse res) throws ServletException, IOException
  {
    getMockRequest(req).setForwardUrl(this.url);
  }

  /** Simply stores that the URL was included an then returns. */
  @Override
  public void include(ServletRequest req, ServletResponse res) throws ServletException, IOException
  {
    getMockRequest(req).addIncludedUrl(this.url);
  }

  /** Locates the MockHttpServletRequest in case it is wrapped. */
  public MockHttpServletRequest getMockRequest(ServletRequest request)
  {
    while (request != null & !(request instanceof MockHttpServletRequest)) {
      request = ((HttpServletRequestWrapper) request).getRequest();
    }

    return (MockHttpServletRequest) request;
  }
}