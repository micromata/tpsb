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
