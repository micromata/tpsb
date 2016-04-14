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

package de.micromata.genome.tpsb.executor;

import org.junit.Test;

public class GroovyShellExecutorTest
{

  @Test
  public void testExecuteGroovy()
  {
    GroovyShellExecutor gse = new GroovyShellExecutor();

    String code = "import de.micromata.genome.tpsb.annotations.TpsbTestSuite;\n"
        + "import de.micromata.genome.tpsb.StartTestBuilder;\n"
        + "import org.junit.Test;\r\n"
        + "\r\n"
        + "/**\r\n"
        + " * A Test\r\n"
        + " *\r\n"
        + " * @author Roger Rene Kommer (r.kommer.extern@micromata.de)\r\n"
        + " */\r\n"
        + "@TpsbTestSuite\r\n"
        + "public class ProductServiceTest\r\n"
        + "{\r\n"
        + "  /**\r\n"
        + "   * SampleDoc \r\n"
        + "   *\r\n"
        + "   */\r\n"
        + "  @Test\r\n"
        + "  public void testAutselect()\r\n"
        + "  {\r\n"
        + "   System.out.println(\"TPSBSTARTMETHOD: ProductServiceTest.testAutselect\");\r\n"
        + "    StartTestBuilder commonTestBuilder = new StartTestBuilder();\r\n"
        + "   System.out.println(\"TPSBTESTSTEPNUM: 1\");\r\n"
        + "    ;\r\n"
        + " //if (true) throw new RuntimeException(\"oops\");\n"
        + "   System.out.println(\"TPSBENDMETHOD: ProductServiceTest.testAutselect\");\r\n"
        + "  }\r\n"
        + "}";
    try {
      gse.executeCode(code, null);
    } catch (Exception ex) {
      throw new RuntimeException(ex);
    }
  }

}
