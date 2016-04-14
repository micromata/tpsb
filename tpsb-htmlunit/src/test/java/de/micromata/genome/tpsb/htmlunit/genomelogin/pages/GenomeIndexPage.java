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

package de.micromata.genome.tpsb.htmlunit.genomelogin.pages;

import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import de.micromata.genome.tpsb.htmlunit.HtmlPageBase;
import de.micromata.genome.tpsb.htmlunit.HtmlPageUrl;

@HtmlPageUrl("Index.action")
public class GenomeIndexPage extends HtmlPageBase<GenomeLoginPage>
{
  public GenomeIndexPage(GenomeConsoleApp app, HtmlPage htmlPage)
  {
    super(app, htmlPage);
  }

  @Override
  public HtmlForm getStdForm()
  {
    // may return set test mode subwindow
    return null;
  }

  public GenomeLoginPage doLogout()
  {
    return getWebApp().doLoadPage(GenomeLogoutPage.class.getAnnotation(HtmlPageUrl.class).value(), GenomeLoginPage.class);
  }
}
