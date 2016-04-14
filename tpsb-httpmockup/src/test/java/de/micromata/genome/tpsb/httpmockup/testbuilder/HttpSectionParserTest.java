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

package de.micromata.genome.tpsb.httpmockup.testbuilder;

import java.io.File;
import java.io.StringWriter;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

import de.micromata.genome.tpsb.httpmockup.MockHttpServletRequest;

/**
 * 
 * @author Roger Rene Kommer (r.kommer.extern@micromata.de)
 * 
 */
public class HttpSectionParserTest
{
  @Test
  public void testParseHttpScenario()
  {
    try {
      String content = FileUtils.readFileToString(new File("./dev/extrc/tests/httpmockup/HttpScenario1.txt"), "UTF-8");
      HttpScenario scen = new HttpScenario(content);
      MockHttpServletRequest req = scen.getRequest();
      final StringWriter sw = new StringWriter();
      IOUtils.copy(req.getReader(), sw);
      String s = sw.toString();
      s.length();
    } catch (Exception ex) {
      throw new RuntimeException(ex);
    }

  }
}
