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

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;

/**
 * Wrapper for WebClient with some ajustments
 * 
 * @author roger@micromata.de
 * 
 */
public class HtmlWebClient extends WebClient
{

  private static final long serialVersionUID = 7341210499923247162L;

  // public static String defaultHost = "http://localhost:8080";
  //
  // public static String defaultAppDir = "/genome";
  //
  // public static String defaultBaseUrl = defaultHost + defaultAppDir;

  // public static String overwriteUrl = null; // "https://secure.micromata.de/popweb";

  // static String overwriteUrl = "http://localhost:8080/popweb";//"https://secure.micromata.de/popweb";

  public String baseUrl;

  public boolean loadImages = false;

  public boolean loadExternalContent = false;

  public HtmlWebClient()
  {
    super(BrowserVersion.FIREFOX_3);
  }

  public HtmlWebClient(BrowserVersion browserVersion, String proxyHost, int proxyPort)
  {
    super(browserVersion, proxyHost, proxyPort);
  }

  public HtmlWebClient(BrowserVersion browserVersion)
  {
    super(browserVersion);
  }

  public String getRootUrl()
  {
    String bu = getBaseUrl();
    int idx = bu.lastIndexOf('/');
    if (idx == -1)
      return bu;
    return bu.substring(0, idx);
  }

  public boolean isLoadImages()
  {
    return loadImages;
  }

  public void setLoadImages(boolean loadImages)
  {
    this.loadImages = loadImages;
  }

  public boolean isLoadExternalContent()
  {
    return loadExternalContent;
  }

  public void setLoadExternalContent(boolean loadExternalContent)
  {
    this.loadExternalContent = loadExternalContent;
  }

  public String getBaseUrl()
  {
    return baseUrl;
  }

  public void setBaseUrl(String baseUrl)
  {
    this.baseUrl = baseUrl;
    this.setHomePage(baseUrl);
  }
}
