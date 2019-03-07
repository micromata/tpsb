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
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;

import de.micromata.genome.tpsb.htmlunit.By;
import de.micromata.genome.tpsb.htmlunit.HtmlPageBase;
import de.micromata.genome.tpsb.htmlunit.HtmlPageUrl;

/**
 * The login page.
 * 
 * @author roger
 * 
 */
@HtmlPageUrl("Login.action")
public class GenomeLoginPage extends HtmlPageBase<GenomeLoginPage>
{
  public GenomeLoginPage(GenomeConsoleApp webApp, HtmlPage htmlPage)
  {
    super(webApp, htmlPage);
  }

  @Override
  public HtmlForm getStdForm()
  {
    return getFormById("loginForm");
  }

  /**
   * sets the user name
   * 
   * @param user the user to set
   * @return this page
   */
  public GenomeLoginPage setUserName(String user)
  {
    HtmlTextInput hinp = (HtmlTextInput) By.and(By.elementType(HtmlTextInput.class), //
        By.id("userName")).findElement(getHtmlPage());
    hinp.setValueAttribute(user);

    return getBuilder();
  }

  /**
   * sets the user password
   * 
   * @param pass the password for the user
   * @return this page
   */
  public GenomeLoginPage setPasswort(String pass)
  {
    By.id("userPass").findElement(htmlPage).setAttribute("value", pass);
    return getBuilder();
  }

  /**
   * Submit the page and do login
   * 
   * @param <X> the type of the {@link HtmlPageBase}
   * @param targetClazz expected page type
   * @return expected page or error
   */
  public <X extends HtmlPageBase< ? >> X doLogin(Class<X> targetClazz)
  {
    return doExecuteSubmit("onLogin", targetClazz);
  }
}
