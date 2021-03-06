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
 * Falls eine Seite (lastrendered) erwartet wird, aber eine andere angezeigt wird.
 * 
 * @author Roger Kommer (roger.kommer.extern@micromata.de)
 * 
 */
public class TpsbWicketWrongPageException extends TpsbWicketFailedException
{

  private static final long serialVersionUID = 5979918781662741758L;

  public TpsbWicketWrongPageException(String message, TestBuilder< ? > testBuilder)
  {
    super(message, testBuilder);
  }

}
