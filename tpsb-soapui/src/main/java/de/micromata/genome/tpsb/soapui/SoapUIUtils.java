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
