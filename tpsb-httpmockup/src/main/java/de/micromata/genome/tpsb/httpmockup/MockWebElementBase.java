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

package de.micromata.genome.tpsb.httpmockup;

import java.util.HashMap;
import java.util.Map;

/**
 * web.xml config element
 * 
 * @author roger@micromata.de
 * 
 */
public class MockWebElementBase
{
  private String description;

  private String displayName;

  private String className;

  private String name;

  private Map<String, String> initParameters = new HashMap<String, String>();

  public String getDescription()
  {
    return description;
  }

  public void setDescription(String description)
  {
    this.description = description;
  }

  public String getDisplayName()
  {
    return displayName;
  }

  public void setDisplayName(String displayName)
  {
    this.displayName = displayName;
  }

  public String getClassName()
  {
    return className;
  }

  public void setClassName(String className)
  {
    this.className = className;
  }

  public String getName()
  {
    return name;
  }

  public void setName(String name)
  {
    this.name = name;
  }

  public void addInitParameter(String key, String value)
  {
    initParameters.put(key, value);
  }

  public Map<String, String> getInitParameters()
  {
    return initParameters;
  }

  public void setInitParameters(Map<String, String> initParameters)
  {
    this.initParameters = initParameters;
  }
}
