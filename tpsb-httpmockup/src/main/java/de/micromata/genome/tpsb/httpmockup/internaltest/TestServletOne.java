package de.micromata.genome.tpsb.httpmockup.internaltest;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

/**
 * Test servlet.
 * 
 * @author Roger Rene Kommer (r.kommer.extern@micromata.de)
 * 
 */
public class TestServletOne extends HttpServlet
{

  private static final long serialVersionUID = 7557997035658983354L;

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
  {
    String data = req.getParameter("data");
    if (StringUtils.contains(data, "OK") == true) {
      resp.setStatus(200);
      resp.getWriter().println("Ja, Kammer ist auch OK");
    } else {
      resp.setStatus(500);
      resp.getWriter().println("Ojeoje");
    }
    resp.getWriter().flush();
  }

}
