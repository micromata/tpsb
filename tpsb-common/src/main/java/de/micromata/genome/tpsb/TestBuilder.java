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
   * @param clazz the class of the builder to create
   * @param <X> the type of the builder
   * @return the builder instance
   */
  public <X> X createBuilder(Class<X> clazz);

  /**
   * set a context variable.
   * 
   * @param varName the name of the variable to set in the current context
   * @param value   the value of the variable to set in the current context
   * @return this
   */
  public T setTestContextVar(String varName, Object value);

  /**
   * get a context var
   * 
   * @param varName the name of the variable to get from the current contexz
   * @return the variable or null if not exists
   */
  public Object getTestContextVar(String varName);

  /**
   * Return the test context as map
   * 
   * @return the complete context as a map
   */
  public Map<String, Object> getTestContext();
}
