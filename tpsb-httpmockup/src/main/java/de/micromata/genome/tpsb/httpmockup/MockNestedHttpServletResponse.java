/////////////////////////////////////////////////////////////////////////////
//
// Project   Micromata Genome Core
//
// Author    roger@micromata.de
// Created   22.01.2008
// Copyright Micromata 22.01.2008
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.tpsb.httpmockup;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

/**
 * The Class NestedHttpServletResponse.
 *
 * @author Roger Rene Kommer (r.kommer.extern@micromata.de)
 */
public class MockNestedHttpServletResponse extends HttpServletResponseWrapper
{

  /**
   * The http status.
   */
  private int httpStatus = -1;

  /**
   * The message.
   */
  private String message = null;

  /**
   * The servlet context.
   */
  private MockServletContext servletContext;

  /**
   * The parent response.
   */
  private HttpServletResponse parentResponse;

  /**
   * Instantiates a new nested http servlet response.
   *
   * @param response the response
   * @param servletContext the servlet context
   */
  public MockNestedHttpServletResponse(HttpServletResponse response, MockServletContext servletContext)
  {
    super(response);
    this.parentResponse = response;
    this.servletContext = servletContext;
  }

  @Override
  public void flushBuffer() throws IOException
  {
  }

  /**
   * {@inheritDoc}
   *
   */

  @Override
  public void sendError(int sc, String msg) throws IOException
  {
    httpStatus = sc;
    message = msg;
    // super.sendError(sc, msg);
  }

  /**
   * {@inheritDoc}
   *
   */

  @Override
  public void sendError(int sc) throws IOException
  {
    httpStatus = sc;
    // super.sendError(sc);
  }

  /**
   * {@inheritDoc}
   *
   */

  @Override
  public void setStatus(int sc, String sm)
  {
    super.setStatus(sc, sm);
  }

  /**
   * {@inheritDoc}
   *
   */

  @Override
  public void setStatus(int sc)
  {
    super.setStatus(sc);
  }

  /**
   * Gets the http status.
   *
   * @return the http status
   */
  public int getHttpStatus()
  {
    return httpStatus;
  }

  /**
   * Sets the http status.
   *
   * @param httpStatus the new http status
   */
  public void setHttpStatus(int httpStatus)
  {
    this.httpStatus = httpStatus;
  }

  /**
   * Gets the servlet context.
   *
   * @return the servlet context
   */
  public MockServletContext getServletContext()
  {
    return servletContext;
  }

  /**
   * Sets the servlet context.
   *
   * @param servletContext the new servlet context
   */
  public void setServletContext(MockServletContext servletContext)
  {
    this.servletContext = servletContext;
  }

  /**
   * Gets the message.
   *
   * @return the message
   */
  public String getMessage()
  {
    return message;
  }

  /**
   * Sets the message.
   *
   * @param message the new message
   */
  public void setMessage(String message)
  {
    this.message = message;
  }

  /**
   * Gets the parent response.
   *
   * @return the parent response
   */
  public HttpServletResponse getParentResponse()
  {
    return parentResponse;
  }

  /**
   * Sets the parent response.
   *
   * @param parentResponse the new parent response
   */
  public void setParentResponse(HttpServletResponse parentResponse)
  {
    this.parentResponse = parentResponse;
  }

}
