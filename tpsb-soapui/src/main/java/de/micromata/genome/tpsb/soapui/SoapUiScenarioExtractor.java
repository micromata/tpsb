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

package de.micromata.genome.tpsb.soapui;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.CharEncoding;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.log4j.Logger;

import com.eviware.soapui.config.TestAssertionConfig;
import com.eviware.soapui.impl.wsdl.AbstractWsdlModelItem;
import com.eviware.soapui.impl.wsdl.WsdlProject;
import com.eviware.soapui.impl.wsdl.teststeps.ManualTestStep;
import com.eviware.soapui.impl.wsdl.teststeps.PropertyTransfersTestStep;
import com.eviware.soapui.impl.wsdl.teststeps.WsdlMessageAssertion;
import com.eviware.soapui.impl.wsdl.teststeps.WsdlPropertiesTestStep;
import com.eviware.soapui.impl.wsdl.teststeps.WsdlTestRequest;
import com.eviware.soapui.impl.wsdl.teststeps.WsdlTestRequestStep;
import com.eviware.soapui.impl.wsdl.teststeps.assertions.basic.SchemaComplianceAssertion;
import com.eviware.soapui.impl.wsdl.teststeps.assertions.basic.SimpleContainsAssertion;
import com.eviware.soapui.impl.wsdl.teststeps.assertions.basic.SimpleNotContainsAssertion;
import com.eviware.soapui.impl.wsdl.teststeps.assertions.basic.XPathContainsAssertion;
import com.eviware.soapui.impl.wsdl.teststeps.assertions.soap.NotSoapFaultAssertion;
import com.eviware.soapui.impl.wsdl.teststeps.assertions.soap.SoapFaultAssertion;
import com.eviware.soapui.impl.wsdl.teststeps.assertions.soap.SoapResponseAssertion;
import com.eviware.soapui.model.support.XPathReference;
import com.eviware.soapui.model.testsuite.TestAssertion;
import com.eviware.soapui.model.testsuite.TestCase;
import com.eviware.soapui.model.testsuite.TestStep;
import com.eviware.soapui.model.testsuite.TestSuite;
import com.eviware.soapui.support.types.StringToStringsMap;

public class SoapUiScenarioExtractor
{
  private static final Logger log = Logger.getLogger(SoapUiScenarioExtractor.class);

  private File soapuiProject;

  private StringBuilder sb = new StringBuilder();

  private WsdlProject project;

  public SoapUiScenarioExtractor(File soapuiProject)
  {
    this.soapuiProject = soapuiProject;
  }

  public SoapUiScenarioExtractor(File soapuiProject, WsdlProject project)
  {
    this.soapuiProject = soapuiProject;
    this.project = project;
  }

  @Override
  public String toString()
  {
    return sb.toString();
  }

  public SoapUiScenarioExtractor extract()
  {
    WsdlProject project = loadProject();

    text("SOAP Ui projekt: " + soapuiProject.getName()).nl();
    description(project.getDescription());

    List<TestSuite> suites = project.getTestSuiteList();
    for (TestSuite suite : suites) {
      extract(suite);
    }
    return this;
  }

  public SoapUiScenarioExtractor store()
  {
    File soapuiTpProject = new File(soapuiProject.getAbsolutePath() + ".tpsbscen.html");
    try {
      FileUtils.write(soapuiTpProject, sb.toString(), CharEncoding.UTF_8);
    } catch (IOException ex) {
      log.error("Cannot find tpscenario: " + soapuiTpProject.getAbsolutePath() + ": " + ex.getMessage(), ex);
    }
    return this;
  }

  private WsdlProject loadProject()
  {
    if (project != null) {
      return project;
    }
    try {
      return project = new WsdlProject(soapuiProject.getAbsolutePath());
    } catch (Exception e) {
      throw new RuntimeException("Failure loading soapui project: " + soapuiProject.getAbsolutePath() + ": " + e.getMessage(), e);
    }
  }

  public SoapUiScenarioExtractor text(String s)
  {
    sb.append(StringEscapeUtils.escapeHtml4(s));
    return this;
  }

  public SoapUiScenarioExtractor code(String s)
  {
    sb.append(s);
    return this;
  }

  public SoapUiScenarioExtractor nl()
  {
    sb.append("\n");
    return this;
  }

  public SoapUiScenarioExtractor p(String text)
  {
    code("<p>").text(text).code("</p>").nl();
    return this;
  }

  public SoapUiScenarioExtractor pre(String text)
  {
    code("<pre>").text(text).code("</pre>").nl();
    return this;
  }

  public SoapUiScenarioExtractor h(int n, String cs, String line)
  {
    code("<H" + n + " class='" + cs + "'>").text(line).code("</H" + n + ">").nl();
    return this;
  }

  public SoapUiScenarioExtractor h(int n, String line)
  {
    code("<H" + n + ">").text(line).code("</H" + n + ">").nl();
    return this;
  }

  public SoapUiScenarioExtractor description(String desc)
  {
    if (StringUtils.isBlank(desc) == true) {
      return this;
    }
    code("<h4>").text("Beschreibung").code("</h4>").nl();
    return descText(desc);
  }

  public SoapUiScenarioExtractor nltobr(String desc)
  {
    String[] lines = StringUtils.split(desc, '\n');
    for (int i = 0; i < lines.length; ++i) {
      if (i > 0) {
        code("<br/>\n");
      }
      text(lines[i]);
    }
    return this;
  }

  public SoapUiScenarioExtractor descText(String desc)
  {
    if (desc == null) {
      return this;
    }

    code("<p>");
    nltobr(desc);
    code("</p>\n");
    return this;
  }

  private void extract(TestSuite suite)
  {
    h(2, "tpsbclassname", "TestSuite " + suite.getName());
    description(suite.getDescription());
    for (TestCase testCase : suite.getTestCaseList()) {
      extract(testCase);
    }
  }

  private void extract(TestCase testCase)
  {
    h(2, "tpsbclassname", "TestCase " + testCase.getName());

    description(testCase.getDescription());
    h(4, "Testablauf");
    code("<table class='tpsbteststeptable'>");

    int testStepNum = 1;
    for (TestStep testStep : testCase.getTestStepList()) {
      extract(testStepNum, testStep);
      ++testStepNum;
    }
    code("</table>\n");
  }

  private void extract(int testStepNum, TestStep testStep)
  {
    code("<tr>").nl();
    code("<td class='stepnumbertd'>").code("" + testStepNum).code("</td>").nl();
    code("<td><p><span class=\"tpsbstepname\">").text(testStep.getName()).code("</span></p>").nl();
    //    nl().text("Test Step: " + testStepNum + " " + testStep.getName()).nl();
    description(testStep.getDescription());
    if (testStep instanceof WsdlPropertiesTestStep) {
      extract((WsdlPropertiesTestStep) testStep);
    } else if (testStep instanceof WsdlTestRequestStep) {
      extract((WsdlTestRequestStep) testStep);
    } else if (testStep instanceof PropertyTransfersTestStep) {
      extract((PropertyTransfersTestStep) testStep);
    } else if (testStep instanceof ManualTestStep) {
      extract((ManualTestStep) testStep);
    } else if (testStep instanceof AbstractWsdlModelItem) {
      extractAbstractStep((AbstractWsdlModelItem) testStep);
    } else {

      text("unknown teststep").nl();
    }
    code("</td>\n</tr>\n");
  }

  private void extract(ManualTestStep step)
  {
    String expected = step.getExpectedResult();
    if (StringUtils.isBlank(expected) == true) {
      return;
    }
    code("<h4>").text("Erwartetes Ergebnis").code("</h4>").nl();
    descText(expected);
  }

  private void extract(WsdlPropertiesTestStep step)
  {
    extractAbstractStep(step);
  }

  private void extract(PropertyTransfersTestStep step)
  {
    extractAbstractStep(step);
  }

  private void extractRequest(WsdlTestRequest req)
  {
    h(4, "Request");
    code("<pre>\n");
    StringToStringsMap headers = req.getRequestHeaders();
    Set<Entry<String, List<String>>> s = headers.entrySet();
    for (Entry<String, List<String>> e : s) {
      for (String v : e.getValue()) {
        text(e.getKey() + ": " + v).nl();
      }
    }
    nl();
    text(req.getRequestContent()).nl();
    code("</pre>\n");
  }

  private void extract(TestAssertion ass)
  {
    h(5, "Assertion " + ass.getName());
    description(ass.getDescription());
    if (ass instanceof SimpleContainsAssertion) {
      SimpleContainsAssertion sca = (SimpleContainsAssertion) ass;
      p("Enhält: " + sca.getToken());
    } else if (ass instanceof SimpleNotContainsAssertion) {
      SimpleNotContainsAssertion sca = (SimpleNotContainsAssertion) ass;
      p("Enthält Nicht: " + sca.getToken());
    } else if (ass instanceof SoapResponseAssertion) {
      p("Ist ein gültiger SOAP Response");
    } else if (ass instanceof SoapFaultAssertion) {
      p("Ist ein gültiger SOAP Fault");
    } else if (ass instanceof NotSoapFaultAssertion) {
      p("Ist kein SOAP Fault");
    } else if (ass instanceof SchemaComplianceAssertion) {
      p("Schema ist gültig");
    } else if (ass instanceof XPathContainsAssertion) {
      XPathContainsAssertion xpe = (XPathContainsAssertion) ass;
      String content = xpe.getExpectedContent();
      XPathReference[] xrefs = xpe.getXPathReferences();
      String xPath = "";
      if (xrefs.length > 0) {
        xPath = xrefs[0].getXPath();
      }
      code("<p>XPath Contains");
      code("<ul><li><strong>XPath</strong>: ").nltobr(xPath).code("</li>\n");
      code("<li><strong>Wert</strong>: ").text(content).code("</li>\n</ul>\n");
      //      p("XPath '" + content + "' in " + xPath);
    } else if (ass instanceof WsdlMessageAssertion) {
      WsdlMessageAssertion sc = (WsdlMessageAssertion) ass;
      TestAssertionConfig cf = sc.getConfig();
      pre(cf.toString());
    }
  }

  private void extract(WsdlTestRequestStep step)
  {
    WsdlTestRequest req = step.getHttpRequest();

    extractRequest(req);
    if (step.getAssertionList().isEmpty() == false) {
      h(4, "Assertions");
    }
    for (TestAssertion ass : step.getAssertionList()) {
      extract(ass);
    }
    //    extractAbstractStep(step);
  }

  private void extractAbstractStep(AbstractWsdlModelItem step)
  {
    h(5, "SoapUiConfig").pre(step.getConfig().toString()).nl();
  }
}
