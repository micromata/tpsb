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

/*
 * 
 */
package de.micromata.genome.tpsb.soapui;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.message.BasicHttpResponse;

import com.eviware.soapui.impl.wsdl.WsdlProject;
import com.eviware.soapui.impl.wsdl.submit.RequestTransportRegistry;
import com.eviware.soapui.impl.wsdl.submit.RequestTransportRegistry.MissingTransportException;
import com.eviware.soapui.impl.wsdl.submit.transports.http.HttpClientRequestTransport;
import com.eviware.soapui.impl.wsdl.teststeps.assertions.basic.SchemaComplianceAssertion;
import com.eviware.soapui.model.support.PropertiesMap;
import com.eviware.soapui.model.testsuite.TestCase;
import com.eviware.soapui.model.testsuite.TestCaseRunContext;
import com.eviware.soapui.model.testsuite.TestCaseRunner;
import com.eviware.soapui.model.testsuite.TestRunListener;
import com.eviware.soapui.model.testsuite.TestRunner;
import com.eviware.soapui.model.testsuite.TestRunner.Status;
import com.eviware.soapui.model.testsuite.TestStep;
import com.eviware.soapui.model.testsuite.TestStepResult;
import com.eviware.soapui.model.testsuite.TestStepResult.TestStepStatus;
import com.eviware.soapui.model.testsuite.TestSuite;

import de.micromata.genome.tpsb.annotations.TpsbBuilder;
import de.micromata.genome.tpsb.annotations.TpsbIgnore;
import de.micromata.genome.tpsb.builder.ScenarioLoaderContext;
import de.micromata.genome.tpsb.httpmockup.MockupHttpRequestUtils;
import de.micromata.genome.tpsb.httpmockup.testbuilder.ServletContextTestBuilder;
import de.micromata.genome.util.types.Holder;

/**
 * This testbuilder executes a SOAP-UI test, delegating calls to a local servlet.
 * 
 * 
 * @param <T> the generic type
 * @author Roger Rene Kommer (r.kommer.extern@micromata.de)
 */
@TpsbBuilder
public class SoapUiTestBuilder<T extends SoapUiTestBuilder<?>> extends ServletContextTestBuilder<T>
{
  private ScenarioLoaderContext scenarioLoader = new ScenarioLoaderContext("dev/extrc/test/soapui", ".xml");

  private boolean disableLocalDispatch = false;

  protected TestStep currentSoapUITestStep = null;

  protected TestCaseRunContext currentSoapUIRunContext = null;

  private byte[] lastRequest;

  private byte[] lastResponse;
  private String baseUrl;
  private String servletPrefix;

  /**
   * Instantiates a new soap ui test builder.
   */
  public SoapUiTestBuilder()
  {

  }

  public T initWithUri(String uri)
  {
    MockupHttpRequestUtils.initWithUri(httpRequest, baseUrl, servletPrefix, uri);
    return getBuilder();
  }

  public T setBaseUrl(String baseUrl)
  {
    this.baseUrl = baseUrl;
    return getBuilder();
  }

  public T setServletPrefix(String servletPrefix)
  {
    this.servletPrefix = servletPrefix;
    return getBuilder();
  }

  /**
   * if disable dispatching to local servlet container, the test will be executed to the http target defined in the
   * SoapUI-Project.
   * 
   * @param disable the disable
   * @return the builder
   */
  public T setDisableLocalDispatch(boolean disable)
  {
    disableLocalDispatch = disable;
    return getBuilder();
  }

  /**
   * Sets the directory where to find the scenario files.
   * 
   * @param dir the dir
   * @return the builder
   */
  public T setScenarioBaseDir(String dir)
  {
    File f = new File(dir);
    scenarioLoader.setBaseDir(f);
    return getBuilder();
  }

  /**
   * Execute soap ui project.
   * 
   * @param scenario should be a soap ui project containing testsuites.
   * @return the t
   */
  public T executeSoapUI(String scenario)
  {
    return executeSoapUI(scenario, null, null);
  }

  @TpsbIgnore
  protected byte[] filterRequestData(byte[] data)
  {
    lastRequest = data;
    return data;
  }

  @TpsbIgnore
  protected byte[] filterResponseData(byte[] data)
  {
    lastResponse = data;
    return data;
  }

  /**
   * Gets the current fq test step name.
   * 
   * @return "" if no currentSoapUITestStep is registerd
   */
  protected String getCurrentFqTestStepName()
  {
    if (currentSoapUITestStep == null) {
      return "";
    }
    return currentSoapUITestStep.getTestCase().getTestSuite().getProject().getName()
        + "."
        + currentSoapUITestStep.getTestCase().getTestSuite().getName()
        + "."
        + currentSoapUITestStep.getTestCase().getName()
        + currentSoapUITestStep.getName()
        + ".";
  }

  @Override
  public T createNewPostRequest(String... urlParams)
  {
    if (currentSoapUITestStep != null) {
      String target = "target/" + currentSoapUITestStep.getTestCase().getTestSuite().getProject().getName() + "/"
          + currentSoapUITestStep.getTestCase().getTestSuite().getName() + "/"
          + currentSoapUITestStep.getTestCase().getName();
      setRequestResponseLogBaseDir(target);
      setRequestResponseLogBaseName(currentSoapUITestStep.getName());
    } else {
      LOG.warn("No currentSoapUITestStep set");
      String target = "target/unkown/unkown";
      setRequestResponseLogBaseDir(target);
      setRequestResponseLogBaseName("");
    }
    return super.createNewPostRequest(urlParams);
  }

  @TpsbIgnore
  protected BasicHttpResponse filterBasicHttpResponse(BasicHttpResponse httpresponse)
  {
    return httpresponse;
  }

  protected HttpClientRequestTransport createHttpClientRequestTransport(HttpClientRequestTransport previous)
  {
    DelegateToSoapUiTestBuilderHttpClientRequestTransport ret = new DelegateToSoapUiTestBuilderHttpClientRequestTransport(
        previous, this)
    {

      @Override
      protected byte[] filterRequestData(byte[] data)
      {
        return testBuilder.filterRequestData(data);
      }

      @Override
      protected byte[] filterResponseData(byte[] data)
      {
        return testBuilder.filterResponseData(data);
      }

      @Override
      protected BasicHttpResponse filterBasicHttpResponse(BasicHttpResponse httpresponse)
      {
        return testBuilder.filterBasicHttpResponse(httpresponse);
      }

    };

    return ret;
  }

  protected String getLastRequestReponseString()
  {

    String reqData = "";
    if (lastRequest != null) {
      reqData = org.apache.commons.codec.binary.StringUtils.newStringUtf8(lastRequest);
    }
    String respData = "";
    if (lastResponse != null) {
      respData = org.apache.commons.codec.binary.StringUtils.newStringUtf8(lastResponse);
    }
    StringBuilder sb = new StringBuilder();
    sb.append("\nLast Request:\n").append(reqData).append("\n");
    sb.append("\nLast Reponse:\n").append(respData).append("\n");
    return sb.toString();
  }

  /**
   * Dumps the last request and response to system out.
   * 
   * @return builder
   */
  private T dumpLastRequestResponse()
  {
    System.out.println(getLastRequestReponseString());
    return getBuilder();
  }

  @TpsbIgnore
  public void setTransport()
  {
    HttpClientRequestTransport previousHttpTransport = null;
    HttpClientRequestTransport previousHttpsTransport = null;
    try {
      previousHttpTransport = (HttpClientRequestTransport) RequestTransportRegistry.getTransport("http");
      previousHttpsTransport = (HttpClientRequestTransport) RequestTransportRegistry.getTransport("https");
      if ((previousHttpTransport instanceof DelegateToSoapUiTestBuilderHttpClientRequestTransport) == false) {
        if (disableLocalDispatch == false) {
          HttpClientRequestTransport newHttpTransport = createHttpClientRequestTransport(previousHttpTransport);
          RequestTransportRegistry.addTransport("http", newHttpTransport);

          HttpClientRequestTransport newHttpsTransport = createHttpClientRequestTransport(previousHttpsTransport);

          RequestTransportRegistry.addTransport("https", newHttpsTransport);
        }
      } else {
        if (disableLocalDispatch == false) {
          DelegateToSoapUiTestBuilderHttpClientRequestTransport dlc = (DelegateToSoapUiTestBuilderHttpClientRequestTransport) previousHttpTransport;
          dlc.setTestBuilder(this);
          DelegateToSoapUiTestBuilderHttpClientRequestTransport dlch = (DelegateToSoapUiTestBuilderHttpClientRequestTransport) previousHttpsTransport;
          dlch.setTestBuilder(this);
        }

      }
    } catch (MissingTransportException ex) {
      throw new IllegalArgumentException("Cannot get http transport from soapui transport registry: " + ex.getMessage(),
          ex);
    }
  }

  /**
   * Load a SoapUI WsdlProject.
   * 
   * @param fqName the fq name
   * @return the wsdl project
   * @throws RuntimeException if a project cannot be laoded
   */
  public static WsdlProject loadSoapUIProject(String fqName)
  {
    try {
      return new WsdlProject(fqName);
    } catch (RuntimeException ex) {
      throw ex;
    } catch (Exception ex) {
      throw new RuntimeException(ex);
    }
  }

  /**
   * Execute soap ui project.
   * 
   * @param scenario should be a soap ui project containing testsuites.
   * @param testSuiteName the test suite name
   * @param testCaseName the test case name
   * @return the t
   */
  public T executeSoapUI(String scenario, String testSuiteName, String testCaseName)
  {

    setTransport();

    File f = scenarioLoader.getScenarioFile(scenario);
    WsdlProject project = loadSoapUIProject(f.getAbsolutePath());
    SoapUiScenarioExtractor extractor = new SoapUiScenarioExtractor(f, project);
    extractor.extract().store();
    disableSchemaComplianceAssertions(project);
    executeSoapUI(project, testSuiteName, testCaseName);
    return getBuilder();
  }

  public static void disableSchemaComplianceAssertions(WsdlProject project)
  {
    SoapUIUtils.disableSoapConformAssertions(project, SchemaComplianceAssertion.class);
  }

  public void executeSoapUI(WsdlProject project, String testSuiteName, String testCaseName)
  {
    final StringBuilder errorOut = new StringBuilder();
    // SchemaComplianceAssertion causes npes

    //        ProjectConfig config = project.getConfig();
    List<TestSuite> tslist = project.getTestSuiteList();
    boolean failedOne = false;
    final Holder<Integer> failureCount = new Holder<Integer>(0);
    for (TestSuite testSuite : tslist) {
      if (testSuiteName != null && testSuiteName.equals(testSuite.getName()) == false) {
        continue;
      }
      for (TestCase testCase : testSuite.getTestCaseList()) {
        if (testCaseName != null && testCaseName.equals(testCase.getName()) == false) {
          continue;
        }
        testCase.addTestRunListener(new TestRunListener()
        {

          @Override
          public void beforeRun(TestCaseRunner testRunner, TestCaseRunContext runContext)
          {
            currentSoapUIRunContext = runContext;
          }

          @Override
          public void afterRun(TestCaseRunner testRunner, TestCaseRunContext runContext)
          {
            currentSoapUIRunContext = null;
          }

          @Override
          public void beforeStep(TestCaseRunner testRunner, TestCaseRunContext runContext)
          {
            currentSoapUIRunContext = runContext;
          }

          @Override
          public void beforeStep(TestCaseRunner testRunner, TestCaseRunContext runContext, TestStep testStep)
          {
            currentSoapUITestStep = testStep;
          }

          @Override
          public void afterStep(TestCaseRunner testRunner, TestCaseRunContext runContext, TestStepResult result)
          {
            TestStepStatus status = result.getStatus();
            String desc = result.getTestStep().getName();

            if (status == TestStepStatus.OK) {

              String[] msg = result.getMessages();

              if (msg != null) {
                for (String ms : msg) {
                  //errorOut.append(desc + ": " + ms).append("\n");
                  System.out.println(desc + ": " + ms);
                }
              }

            } else {

              String[] msg = result.getMessages();
              /**
               * org.xmlsoap.schemas.soap.envelope.impl.EnvelopeImpl throws a IncompatibleClassChangeError.
               * Unfortunatelly soapui throws away the stacktrace, so it is difficult to track down.
               */
              boolean isClassLoaderProblem = false;
              if (msg != null) {
                for (String ms : msg) {
                  if (ms.endsWith("Implementing class") == true) {
                    isClassLoaderProblem = true;
                  } else {
                    errorOut.append(desc + ": " + ms).append("\n");
                  }
                  System.err.println(desc + ": " + ms);
                }
                if (msg.length != 1 || isClassLoaderProblem == false) {
                  failureCount.set(failureCount.get() + 1);
                  errorOut.append(getLastRequestReponseString());
                  //                      dumpLastRequestResponse();
                } else {
                }
              }
            }
            currentSoapUITestStep = null;
          }

        });
        PropertiesMap map = new PropertiesMap();
        final TestRunner runner = testCase.run(map, false);

        Status status = runner.getStatus();
        if (status != Status.FINISHED && failureCount.get() > 0) {
          failedOne = true;
        }
      }

    }
    if (failedOne == true) {
      fail(errorOut.toString());
    }

  }

  @TpsbIgnore
  public List<String> getAllTestCasesForScenario(String scenario)
  {
    List<String> ret = new ArrayList<String>();
    try {
      File f = scenarioLoader.getScenarioFile(scenario);
      WsdlProject project = new WsdlProject(f.getAbsolutePath());
      List<TestSuite> tslist = project.getTestSuiteList();
      for (TestSuite testSuite : tslist) {
        for (TestCase testCase : testSuite.getTestCaseList()) {
          ret.add(testSuite.getName() + "." + testCase.getName());
        }
      }
      return ret;
    } catch (RuntimeException ex) {
      throw ex;
    } catch (Exception ex) {
      throw new RuntimeException(ex);
    }

  }

}
