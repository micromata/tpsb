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

package de.micromata.genome.tpsb.builder;

import java.util.Stack;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;

public class ScenarioDescriber
{
  private StringBuilder sb = new StringBuilder();

  private static class StepScope
  {
    int step = 0;

  }

  Stack<StepScope> stepStack = new Stack<StepScope>();

  public ScenarioDescriber startFlow(String title, String description)
  {
    stepStack.push(new StepScope());
    h2(title);
    p(description);
    code("<table class=\"tpsbteststeptable\">\n");
    code(" <tr><th<th class=\"stepnumbertd\" width=\"10px\">#</th>\r\n" +
        "<th>Lauf</th></tr>\n");
    return this;
  }

  public ScenarioDescriber endFlow()
  {
    code("</table>\n");
    stepStack.pop();
    return this;
  }

  public ScenarioDescriber startStep(String title, String description)
  {

    StepScope curscope = stepStack.peek();
    ++curscope.step;
    code("<tr><td class=\"stepnumbertd\">").code(Integer.toString(curscope.step)).code("</td><td>");
    code("<p><span class=\"tpsbstepname\">").text(title).code("</span></p>\n");
    p(description);
    return this;
  }

  public ScenarioDescriber startArgTable()
  {
    String tstart = "<table class=\"tpsbstepargstable\">\r\n" +
        "<tr>\r\n" +
        "<th style=\"width:10%;\">Name</th>\r\n" +
        "<th style=\"width:40%;\">Beschreibung</th>\r\n" +
        "<th style=\"width:10%;\">Datentyp</th>\r\n" +
        "<th style=\"width:40%;\">Wert</th>\r\n" +
        "</tr>";
    code(tstart);
    return this;
  }

  public ScenarioDescriber argLine(String name, String description, String type, String value)
  {
    code("<tr><td>").text(name).code("</td><td>").text(description).code("</td><td>").text(type).code("</td><td>").text(value)
        .code("</td></tr>\n");
    return this;
  }

  public ScenarioDescriber endArgTable()
  {
    code("</table>\n");
    return this;
  }

  public ScenarioDescriber endStep()
  {
    code("</td></tr>\n");
    return this;
  }

  public ScenarioDescriber printCodeSection(String title, String description, String code)
  {
    h4(title);
    if (StringUtils.isNotBlank(description) == true) {
      p(description);
    }
    code("<pre>").text(code).code("</pre>");
    return this;
  }

  public ScenarioDescriber printCodeSection(String title, String code)
  {
    return printCodeSection(title, "", code);
  }

  public ScenarioDescriber code(String code)
  {
    sb.append(code);
    return this;
  }

  public ScenarioDescriber text(String text)
  {
    sb.append(StringEscapeUtils.escapeHtml(text));
    return this;
  }

  public ScenarioDescriber h2(String text)
  {
    return code("<h2>").text(text).code("</h2>\n");
  }

  public ScenarioDescriber h3(String text)
  {
    return code("<h3>").text(text).code("</h3>\n");
  }

  public ScenarioDescriber h4(String text)
  {
    return code("<h4>").text(text).code("</h4>\n");
  }

  public ScenarioDescriber p(String text)
  {
    return code("<p>").text(text).code("</p>\n");
  }

  public ScenarioDescriber linebr(String text)
  {
    return text(text).code("<br/>\n");
  }

  @Override
  public String toString()
  {
    return sb.toString();
  }
}
