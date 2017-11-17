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

import org.apache.commons.lang3.StringUtils;

/**
 * An test assertion failed.
 * 
 * @author roger
 * 
 */
public class AssertionFailedException extends TpsbException
{

  private static final long serialVersionUID = -7365059191838753147L;

  /**
   * Line number in tpsb source code test.
   */
  private int lineNo;

  /**
   * Class name of the tpsb test.
   */
  private String className;

  /**
   * method name of the tpsb test.
   */
  private String methodName;

  /**
   * source code line.
   */
  private String codeLine;

  public AssertionFailedException()
  {
    super();
  }

  public AssertionFailedException(String message, TestBuilder< ? > testBuilder)
  {
    super(message, testBuilder);
  }

  public AssertionFailedException(String message, Throwable cause, TestBuilder< ? > testBuilder)
  {
    super(message, cause, testBuilder);
  }

  public AssertionFailedException(String message, Throwable cause)
  {
    super(message, cause);
  }

  public AssertionFailedException(String message)
  {
    super(message);
  }

  public AssertionFailedException(TestBuilder< ? > testBuilder)
  {
    super(testBuilder);
  }

  public AssertionFailedException(Throwable cause)
  {
    super(cause);
  }

  @Override
  public String getMessage()
  {

    String msg = super.getMessage();
    if (StringUtils.isNotEmpty(className) == true) {
      msg = msg + "; " + className + "." + methodName + "(" + lineNo + "): " + codeLine;
    }
    return msg;
  }

  public int getLineNo()
  {
    return lineNo;
  }

  public void setLineNo(int lineNo)
  {
    this.lineNo = lineNo;
  }

  public String getClassName()
  {
    return className;
  }

  public void setClassName(String className)
  {
    this.className = className;
  }

  public String getMethodName()
  {
    return methodName;
  }

  public void setMethodName(String methodName)
  {
    this.methodName = methodName;
  }

  public String getCodeLine()
  {
    return codeLine;
  }

  public void setCodeLine(String codeLine)
  {
    this.codeLine = codeLine;
  }

}
