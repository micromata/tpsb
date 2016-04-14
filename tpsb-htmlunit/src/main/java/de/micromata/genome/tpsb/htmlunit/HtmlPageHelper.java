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
