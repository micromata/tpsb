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

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.io.FilenameUtils;

/**
 * 
 * @author Roger Rene Kommer (r.kommer.extern@micromata.de)
 * 
 */
public abstract class MockWebElementConfig
{
  public int getNextMapDef(String path, int offset)
  {
    if (path.startsWith("/") == false) {
      path = "/" + path;
    }
    List< ? extends MockMapDef> mapDefs = getMappingDefs();
    for (int i = offset; i < mapDefs.size(); ++i) {
      MockMapDef md = mapDefs.get(i);
      String p = md.getUrlPattern();
      if (p == null) {
        return -1;
      }
      if (p.equals("/") == true) {
        return i;
      }
      if (md.getUrlMatcher().match(path) == true) {
        return i;
      }
      if (FilenameUtils.wildcardMatch(path, p) == true) {
        return i;
      }
    }
    return -1;
  }

  public void sortMappingDefs()
  {
    List< ? extends MockMapDef> mapDef = getMappingDefs();
    Collections.sort(mapDef, new Comparator<MockMapDef>() {
      int getMapDefPrio(MockMapDef md)
      {
        String up = md.getUrlPattern();
        if (up.equals("/") == true) {
          return 10000;
        }
        if (up.startsWith("*.") == true) {
          return 1000;
        }

        if (up.contains("*") == false) {
          return up.length() * 100;
        }
        return up.length();
      }

      @Override
      public int compare(MockMapDef o1, MockMapDef o2)
      {
        int o1prio = getMapDefPrio(o1);
        int o2prio = getMapDefPrio(o2);
        int ret = o1prio - o2prio;
        return ret;
      }
    });

  }

  protected abstract List< ? extends MockMapDef> getMappingDefs();
}
