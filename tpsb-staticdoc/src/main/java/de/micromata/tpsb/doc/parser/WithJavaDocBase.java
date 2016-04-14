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
 * 
 * @author roger
 * 
 */
public abstract class WithJavaDocBase implements WithJavaDoc, Serializable
{

  private static final long serialVersionUID = -4680456236687996022L;

  private JavaDocInfo javaDocInfo;

  public WithJavaDocBase()
  {

  }

  public WithJavaDocBase(WithJavaDocBase other)
  {
    this.javaDocInfo = other.javaDocInfo;
  }

  @Override
  public JavaDocInfo getJavaDocInfo()
  {
    return javaDocInfo;
  }

  public void setJavaDocInfo(JavaDocInfo javaDocInfo)
  {
    this.javaDocInfo = javaDocInfo;
  }
}
