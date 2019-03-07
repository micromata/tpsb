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

package de.micromata.genome.tpsb.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Marks an method, which has argument with a scenario file.
 * 
 * In test documentation this will be used to print.
 * 
 * The file will be lookup in the project root pathes. (-pr in StaticTestDocGenerator).
 * 
 * @author Roger Rene Kommer (r.kommer.extern@micromata.de)
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface TpsbScenarioFile {

  /**
   * Pattern to build the file name.
   * 
   * ${0} to ${n} addresses the arguments.
   * 
   * @return the file pattern
   */
  String filePattern() default "${0}";

  /**
   * fully qualified class name of the renderer. Has to be implement ScenarioRenderer.
   * 
   * @return the name of the class
   */
  String scenarioRendererClass() default "de.micromata.genome.tpsb.builder.PreTextScenarioRenderer";
}
