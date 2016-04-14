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

package de.micromata.genome.tpsb;

import java.util.Map;

/**
 * Base class of test builders
 * 
 * @author roger
 * 
 */
public interface TestBuilder<T extends TestBuilder< ? >>
{
  /**
   * Creates a new Testbuilder via default constructor and copies testcontext and populates the fields.
   * 
   * @param clazz
   * @return
   */
  public <X> X createBuilder(Class<X> clazz);

  /**
   * set a context variable.
   * 
   * @param varName
   * @param value
   * @return this
   */
  public T setTestContextVar(String varName, Object value);

  /**
   * get a context var
   * 
   * @param varName
   * @return the variable or null if not exists
   */
  public Object getTestContextVar(String varName);

  /**
   * Return the test context as map
   * 
   * @return
   */
  public Map<String, Object> getTestContext();
}
