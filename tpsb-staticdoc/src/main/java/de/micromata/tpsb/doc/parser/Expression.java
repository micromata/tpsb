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

package de.micromata.tpsb.doc.parser;

import java.io.Serializable;

/**
 * Repräsentiert eine Expression die an einem Buildermethodenaufruf dokumentiert wird
 * 
 * @author lado
 * @author Roger Kommer (roger.kommer.extern@micromata.de)
 * 
 */
public class Expression implements Serializable
{

  private static final long serialVersionUID = -4232799859781685761L;

  protected String name;

  protected String expression;

  public Expression(String name, String expr)
  {
    this.name = name;
    this.expression = expr;
  }

  public String getName()
  {
    return name;
  }

  public void setName(String name)
  {
    this.name = name;
  }

  public String getExpression()
  {
    return expression;
  }

  public void setExpression(String expression)
  {
    this.expression = expression;
  }
}
