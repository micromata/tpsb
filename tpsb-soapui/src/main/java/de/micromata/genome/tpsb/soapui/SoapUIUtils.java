package de.micromata.genome.tpsb.soapui;

import com.eviware.soapui.impl.wsdl.WsdlProject;
import com.eviware.soapui.impl.wsdl.teststeps.WsdlMessageAssertion;
import com.eviware.soapui.impl.wsdl.teststeps.WsdlTestRequestStep;
import com.eviware.soapui.model.testsuite.TestAssertion;
import com.eviware.soapui.model.testsuite.TestCase;
import com.eviware.soapui.model.testsuite.TestStep;
import com.eviware.soapui.model.testsuite.TestSuite;

/**
 * Utility to handle SoapUI Project
 * 
 * @author Roger Rene Kommer (r.kommer.extern@micromata.de)
 * 
 */
public class SoapUIUtils
{
  /**
   * Disable Assertions in the project.
   * 
   * @param project
   * @param msgassert
   */
  public static void disableSoapConformAssertions(WsdlProject project, Class< ? extends WsdlMessageAssertion> msgassert)
  {
    for (TestSuite testSuite : project.getTestSuiteList()) {
      for (TestCase testCase : testSuite.getTestCaseList()) {
        for (TestStep testStep : testCase.getTestStepList()) {
          if (testStep instanceof WsdlTestRequestStep) {
            WsdlTestRequestStep rts = (WsdlTestRequestStep) testStep;
            for (TestAssertion ass : rts.getAssertionList()) {
              if (ass instanceof WsdlMessageAssertion) {
                if (msgassert.isAssignableFrom(ass.getClass())) {
                  WsdlMessageAssertion wsdas = (WsdlMessageAssertion) ass;
                  wsdas.getConfig().setDisabled(true);
                }
              }

            }
          }
        }
      }
    }
  }
}
