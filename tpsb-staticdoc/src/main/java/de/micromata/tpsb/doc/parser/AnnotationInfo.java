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
import java.util.HashMap;
import java.util.Map;

/**
 * Holds Annotation information.
 * 
 * @author roger
 * 
 */
public class AnnotationInfo implements Serializable
{

  private static final long serialVersionUID = 3502633322355646665L;

  private String name;

  private Map<String, Object> params = new HashMap<String, Object>();

  public String getName()
  {
    return name;
  }

  public Object getDefaultAnnotationValue()
  {
    return params.get("default");
  }

  public void setDefaultAnnotationValue(Object value)
  {
    params.put("default", value);
  }

  public void setName(String name)
  {
    this.name = name;
  }

  public Map<String, Object> getParams()
  {
    return params;
  }

  public void setParams(Map<String, Object> params)
  {
    this.params = params;
  }

}
