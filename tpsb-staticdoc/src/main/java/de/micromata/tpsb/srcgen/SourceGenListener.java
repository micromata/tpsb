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

package de.micromata.tpsb.srcgen;

import de.micromata.tpsb.doc.parser.MethodInfo;
import de.micromata.tpsb.doc.parser.TestStepInfo;

/**
 * Will be called while generating source via SourceGenerator.
 * 
 * @author Roger Rene Kommer (r.kommer.extern@micromata.de)
 * 
 */
public class SourceGenListener
{
  public void beforeGenerateTestStep(SourceGenerator gen, int currentStepCount, MethodInfo mi, TestStepInfo ts, String previusBuilder)
  {

  }

  public void afterGenerateTestStep(SourceGenerator gen, int currentStepCount, MethodInfo mi, TestStepInfo ts, String previusBuilder)
  {

  }

  public void beforeGenerateTestMethod(SourceGenerator gen, MethodInfo mi)
  {

  }

  public void afterGenerateTestMethod(SourceGenerator gen, MethodInfo mi)
  {

  }
}
