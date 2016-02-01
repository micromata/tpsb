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

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.gargoylesoftware.htmlunit.Page;

public class HtmlPageHelper
{
  public static void loadImages(HtmlPageBase< ? extends HtmlPageBase< ? >> page) throws Exception
  {

    NodeList nl = page.getHtmlPage().getElementsByTagName("img");
    for (int i = 0; i < nl.getLength(); ++i) {
      Node node = nl.item(i);
      Node el = node.getAttributes().getNamedItem("src");
      String val = el.getNodeValue();
      loadReferencedContent(page, val);
    }
  }

  public static void loadReferencedContent(HtmlPageBase< ? extends HtmlPageBase< ? >> page, String ref) throws Exception
  {
    HtmlWebClient client = page.getHtmlWebClient();
    String url;
    if (ref.startsWith("http") == true && client.isLoadExternalContent() == false)
      return;
    if (ref.startsWith("/") == true) {
      url = page.getWebClient().getRootUrl() + ref;
    } else {
      throw new RuntimeException("unsupported link: " + ref);
    }
    Page tpage = client.getPage(url);
  }
}
