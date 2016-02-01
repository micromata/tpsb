/////////////////////////////////////////////////////////////////////////////
//
// Project   DHL-ParcelOnlinePostage
//
// Author    roger@micromata.de
// Created   16.09.2008
// Copyright Micromata 16.09.2008
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.tpsb.htmlunit;

import de.micromata.genome.tpsb.TpsbException;

public class HtmlPageException extends TpsbException
{

  private static final long serialVersionUID = 6859424038268631311L;

  private HtmlPageBase< ? > page;

  public HtmlPageException(String message, HtmlPageBase< ? > page)
  {
    super(message, page);
    this.page = page;
  }

  public HtmlPageException(String message, HtmlPageBase< ? > page, Throwable cause)
  {
    super(message, cause, page);
    this.page = page;
  }

  public String getFailureDump()
  {
    StringBuilder sb = new StringBuilder();

    sb.append(getMessage()).append(":\n");
    sb.append("Request:\n").append(page.getRequestDump()).append("\n\nResponse:\n").append(page.getResponseDump());
    return sb.toString();
  }

  public HtmlPageBase< ? > getPage()
  {
    return page;
  }

  public void setPage(HtmlPageBase< ? > page)
  {
    this.page = page;
  }
}
