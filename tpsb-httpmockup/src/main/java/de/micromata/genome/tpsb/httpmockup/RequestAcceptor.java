package de.micromata.genome.tpsb.httpmockup;

import java.io.IOException;

import javax.servlet.ServletException;

/**
 * Accepts a servlet request.
 * 
 * @author Roger Rene Kommer (r.kommer.extern@micromata.de)
 * 
 */
public interface RequestAcceptor
{
  /**
   * Accept
   * 
   * @param request
   * @param response
   * @throws Exception
   */
  public void acceptRequest(final MockHttpServletRequest request, final MockHttpServletResponse response) throws IOException,
      ServletException;
}
