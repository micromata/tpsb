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

import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.util.tester.WicketTester;

/**
 * Wrapper for all the fancy multi filter wicket projects here. If you have the need to use several filters, like in the wickettestbuilder
 * 1.4 implementation, please use this class to do this.
 * 
 * @author Johannes Unterstein (j.unterstein@micromata.de)
 */
public class MultiFilterWicketTestBuilder<T extends WicketTestBuilder< ? >> extends WicketTestBuilder<T>
{
  public MultiFilterWicketTestBuilder(WebApplication application)
  {
    super(application);
  }

  @Override
  protected WicketTester _getWicketTester(WebApplication application)
  {
    return super._getWicketTester(application); // change the return value to your own special WicketTester implementation
  }
}
