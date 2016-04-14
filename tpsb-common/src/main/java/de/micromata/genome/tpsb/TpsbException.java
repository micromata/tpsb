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
 * Root of test exception.
 * 
 * @author roger
 * 
 */
public class TpsbException extends RuntimeException
{

  private static final long serialVersionUID = -802056617581452536L;

  private TestBuilder< ? > testBuilder;

  public TpsbException()
  {
    super();
  }

  public TpsbException(TestBuilder< ? > testBuilder)
  {
    super();
    this.testBuilder = testBuilder;
  }

  public TpsbException(String message)
  {
    super(message);
  }

  public TpsbException(String message, TestBuilder< ? > testBuilder)
  {
    this(message);
    this.testBuilder = testBuilder;
  }

  public TpsbException(String message, Throwable cause)
  {
    super(message, cause);
  }

  public TpsbException(String message, Throwable cause, TestBuilder< ? > testBuilder)
  {
    this(message, cause);
    this.testBuilder = testBuilder;
  }

  public TpsbException(Throwable cause)
  {
    super(cause);
  }

  public TpsbException(Throwable cause, TestBuilder< ? > testBuilder)
  {
    super(cause);
    this.testBuilder = testBuilder;
  }

  public TestBuilder< ? > getTestBuilder()
  {
    return testBuilder;
  }

  public void setTestBuilder(TestBuilder< ? > testBuilder)
  {
    this.testBuilder = testBuilder;
  }

}
