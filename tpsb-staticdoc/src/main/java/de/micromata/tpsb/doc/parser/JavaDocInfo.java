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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import de.micromata.genome.util.types.Pair;

/**
 * Repräsentiert einen JavaDoc-Kommentar.
 * 
 * @author Roger Kommer (roger.kommer.extern@micromata.de)
 * @author Stefan Stützer
 */
public class JavaDocInfo implements Serializable
{
  private static final long serialVersionUID = -1997564866617134662L;

  /** 1-Zeiliger Titel */
  private String title;

  /** Mehrzeilige Beschreibung */
  private String description;

  /** Map aller im Kommentar enthaltenen Tags (@...) */
  private Map<String, List<Pair<String, String>>> tags = new HashMap<String, List<Pair<String, String>>>();

  public JavaDocInfo()
  {

  }

  public JavaDocInfo(String line)
  {
    title = line;
  }

  public String getTitle()
  {
    return title;
  }

  public void setTitle(String title)
  {
    this.title = title;
  }

  public String getDescription()
  {
    return description;
  }

  public void setDescription(String description)
  {
    this.description = description;
  }

  public void setTags(Map<String, List<Pair<String, String>>> tags)
  {
    this.tags = tags;
  }

  public Map<String, List<Pair<String, String>>> getTags()
  {
    return tags;
  }

  public String getParamDoc(String paramName)
  {
    if (tags.get("@param") == null) {
      return StringUtils.EMPTY;
    }
    List<Pair<String, String>> params = tags.get("@param");
    for (Pair<String, String> param : params) {
      if (StringUtils.equals(paramName, param.getFirst()) == true) {
        return param.getSecond();
      }
    }
    return StringUtils.EMPTY;
  }

  public Pair<String, String> getUniqueTagInfo(String tagName)
  {
    if (tags.get(tagName) == null) {
      return null;
    }
    Pair<String, String> tagInfo = tags.get(tagName).iterator().next();
    return tagInfo;
  }

  public List<Pair<String, String>> getTagInfo(String tagName)
  {
    if (tags.get(tagName) == null) {
      return new ArrayList<Pair<String, String>>();
    }
    return tags.get(tagName);
  }
}