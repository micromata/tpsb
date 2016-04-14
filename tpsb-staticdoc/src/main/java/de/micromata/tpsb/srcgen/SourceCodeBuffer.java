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

package de.micromata.tpsb.srcgen;

/**
 * Generates Sourcecode
 * 
 * @author roger
 * 
 */
public class SourceCodeBuffer
{
  StringBuilder sb = new StringBuilder();

  private int lineNo = 0;

  public String getCode()
  {
    return sb.toString();
  }

  private void handleNls(String text)
  {
    if (text == null){
      return;
    }
    for (int i = 0; i < text.length(); ++i) {
      if (text.charAt(i) == '\n') {
        ++lineNo;
      }
    }
  }

  public SourceCodeBuffer append(String text)
  {
    handleNls(text);
    sb.append(text);
    return this;
  }

  public StringBuilder getBuffer()
  {
    return sb;
  }

  @Override
  public String toString()
  {
    return sb.toString();
  }

  public SourceCodeBuffer nl()
  {
    ++lineNo;
    sb.append("\n");
    return this;
  }

  public int getCurrentLineNo()
  {
    return lineNo;// + 1;
  }

  public int getLineNo()
  {
    return lineNo;
  }

  public void setLineNo(int lineNo)
  {
    this.lineNo = lineNo;
  }
}
