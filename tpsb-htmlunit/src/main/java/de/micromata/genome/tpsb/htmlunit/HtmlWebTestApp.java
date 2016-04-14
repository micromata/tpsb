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

import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.util.UrlUtils;

import de.micromata.genome.tpsb.CommonTestBuilder;
import de.micromata.genome.tpsb.TpsbException;

public class HtmlWebTestApp<T extends HtmlWebTestApp< ? >> extends CommonTestBuilder<T>
{
  protected HtmlWebClient webClient;

  public HtmlWebTestApp()
  {
    webClient = new HtmlWebClient();
  }

  public T setBaseUrl(String url)
  {
    webClient.setBaseUrl(url);

    return getBuilder();
  }

  public T setJavascriptEnabled(boolean enabled)
  {
    webClient.setJavaScriptEnabled(enabled);
    return getBuilder();
  }

  public <P extends HtmlPageBase< ? >> P doLoadPage(final String url, Class<P> expected)
  {
    try {
      String turl = UrlUtils.resolveUrl(webClient.getBaseUrl(), url);
      Page tp = webClient.getPage(turl);
      return HtmlPageBase.createPage(this, tp, expected);
    } catch (Exception ex) {
      throw new TpsbException(ex);
    }
  }

  /**
   * Load the given page.
   * 
   * @param <P>
   * @param expected
   * @return
   */
  public <P extends HtmlPageBase< ? >> P doLoadPage(Class<P> expected)
  {
    return doLoadPage(expected.getAnnotation(HtmlPageUrl.class).value(), expected);
  }

  public HtmlWebClient getWebClient()
  {
    return webClient;
  }

  public void setWebClient(HtmlWebClient webClient)
  {
    this.webClient = webClient;
  }
}
