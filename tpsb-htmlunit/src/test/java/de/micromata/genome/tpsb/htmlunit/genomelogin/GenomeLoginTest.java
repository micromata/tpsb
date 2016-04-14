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

package de.micromata.genome.tpsb.htmlunit.genomelogin;

import org.junit.Test;

import de.micromata.genome.tpsb.htmlunit.HtmlPageException;
import de.micromata.genome.tpsb.htmlunit.genomelogin.pages.GenomeConsoleApp;
import de.micromata.genome.tpsb.htmlunit.genomelogin.pages.GenomeIndexPage;
import de.micromata.genome.tpsb.htmlunit.genomelogin.pages.GenomeLoginPage;

/**
 * Sample Test
 * 
 * @author roger
 * 
 */
public class GenomeLoginTest
{
  @Test
  public void testDummy()
  {

  }

  // @Test, URL currently not working!
  public void testStandardLogin()
  {
    GenomeConsoleApp app = new GenomeConsoleApp();
    try {
      app.setBaseUrl("http://secure.sample.de/sample/")//
          .setJavascriptEnabled(false) //
          .doGoLoginPage()//
          .setUserName("admin") //
          .setPasswort("xxx") //
          .doLogin(GenomeLoginPage.class) //
          .setUserName("admin") //
          .setPasswort("umata") //
          .doLogin(GenomeIndexPage.class) //
          .doLogout() //
      ;
    } catch (HtmlPageException ex) {
      System.out.println(ex.getFailureDump());
      throw ex;
    }
  }
}
