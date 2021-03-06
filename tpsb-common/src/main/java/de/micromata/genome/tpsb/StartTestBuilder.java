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

import de.micromata.genome.tpsb.annotations.TpsbBuilder;

/**
 * Just a plain TestBuilder with no specific functions.
 * 
 * To start a Test flow, use this class and create your specific testbuilder with createBuilder(YourTestBuilder.class).
 * 
 * @author Roger Rene Kommer (r.kommer.extern@micromata.de)
 * 
 */
@TpsbBuilder
public class StartTestBuilder extends CommonTestBuilder<StartTestBuilder>
{
  public StartTestBuilder()
  {

  }

  public StartTestBuilder(CommonTestBuilder< ? > other)
  {
    super(other);
  }

}
