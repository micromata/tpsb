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

package de.micromata.genome.junittools.wicket;

import de.micromata.genome.tpsb.TestBuilder;

/**
 * Failure in case of components are not correct.
 * 
 * @author Roger Kommer (roger.kommer.extern@micromata.de)
 * 
 */
public class TpsbWicketComponentException extends TpsbWicketFailedException
{

  private static final long serialVersionUID = 4356339641126599117L;

  private String matcherExpression;

  public TpsbWicketComponentException(String message, TestBuilder< ? > testBuilder, String matcherExpression)
  {
    super(message + ": " + matcherExpression, testBuilder);
    this.matcherExpression = matcherExpression;
  }

  public String getMatcherExpression()
  {
    return matcherExpression;
  }

  public void setMatcherExpression(String matcherExpression)
  {
    this.matcherExpression = matcherExpression;
  }
}
