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

/**
 * Assertion missing exception.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class AssertionMissingException extends TpsbException
{

  private static final long serialVersionUID = 3331646837334473854L;

  private Class< ? extends Throwable> missingException;

  public AssertionMissingException(final Class< ? extends Throwable> missingException)
  {
    super();
    this.missingException = missingException;
  }

  public AssertionMissingException(final Class< ? extends Throwable> missingException, TestBuilder< ? > testBuilder)
  {
    super(testBuilder);
    this.missingException = missingException;
  }

  public AssertionMissingException()
  {
    super();
  }

  public Class< ? extends Throwable> getMissingException()
  {
    return this.missingException;
  }

  public void setMissingException(final Class< ? extends Throwable> missingException)
  {
    this.missingException = missingException;
  }
}
