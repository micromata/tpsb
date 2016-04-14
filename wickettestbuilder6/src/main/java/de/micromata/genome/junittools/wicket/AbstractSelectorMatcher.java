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

import org.apache.wicket.Component;

import de.micromata.genome.util.matcher.Matcher;
import de.micromata.genome.util.matcher.MatcherBase;

/**
 * Abstract delegating matcher, which implements TpsbWicketMatchSelector.
 * 
 * @author Roger Kommer (roger.kommer.extern@micromata.de)
 * 
 */
public abstract class AbstractSelectorMatcher extends MatcherBase<Component> implements TpsbWicketMatchSelector
{
  private final Matcher<Component> parentMatcher;

  public AbstractSelectorMatcher(Matcher<Component> parentMatcher)
  {
    this.parentMatcher = parentMatcher;
  }

  @Override
  public boolean match(Component object)
  {
    return parentMatcher.match(object);
  }

}
