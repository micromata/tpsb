package de.micromata.genome.tpsb.httpmockup.testbuilder;

import org.junit.Assert;
import org.junit.Test;

public class ServletContextTBTest
{
  @Test
  public void testUrlPathEmtpy()
  {
    ServletContextTestBuilder<?> tb = new ServletContextTestBuilder<>();
    tb.setContextPath("/");
    tb.createNewGetRequest("http://localhost:8080/");
    Assert.assertEquals("/", tb.httpRequest.getContextPath());
    Assert.assertEquals("", tb.httpRequest.getServletPath());

  }

  @Test
  public void testUrlPathServlet()
  {
    ServletContextTestBuilder<?> tb = new ServletContextTestBuilder<>();
    tb.setContextPath("/a");
    tb.createNewGetRequest("http://localhost:8080/a/b?x=1&y=2");
    Assert.assertEquals("/a", tb.httpRequest.getContextPath());
    Assert.assertEquals("/b", tb.httpRequest.getServletPath());

    Assert.assertEquals("1", tb.httpRequest.getParameter("x"));
    Assert.assertEquals("2", tb.httpRequest.getParameter("y"));

  }
}
