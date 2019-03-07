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

import de.micromata.genome.tpsb.annotations.TpsbApplication;
import de.micromata.genome.tpsb.htmlunit.HtmlWebTestApp;

/**
 * Html Unit Test app for genome console.
 * 
 * @author roger
 * 
 */
@TpsbApplication
public class GenomeConsoleApp extends HtmlWebTestApp<GenomeConsoleApp>
{
  /**
   * Open the genome console
   * 
   * @return the {@link GenomeLoginPage}
   */
  public GenomeLoginPage doGoLoginPage()
  {
    return doLoadPage(GenomeLoginPage.class);
  }
}
