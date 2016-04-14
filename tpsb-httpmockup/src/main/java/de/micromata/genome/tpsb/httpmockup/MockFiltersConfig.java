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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.Filter;

import de.micromata.genome.tpsb.httpmockup.MockFilterMapDef.FilterDispatchFlags;

/**
 * Configuration for a filter
 * 
 * @author roger@micromata.de
 * 
 */
public class MockFiltersConfig extends MockWebElementConfig
{
  private Map<String, MockFilterDef> filter = new HashMap<String, MockFilterDef>();

  private List<MockFilterMapDef> filterMapping = new ArrayList<MockFilterMapDef>();

  @Override
  protected List< ? extends MockMapDef> getMappingDefs()
  {
    return filterMapping;
  }

  public int getNextFilterMapDef(String path, int offset, FilterDispatchFlags dispatcherFlag)
  {
    do {
      offset = super.getNextMapDef(path, offset);
      if (offset == -1) {
        return offset;
      }
      MockFilterMapDef md = filterMapping.get(offset);
      if ((md.getDispatcherFlags() & dispatcherFlag.getFlags()) != 0) {
        return offset;
      }
      ++offset;
    } while (true);
  }

  public void addFilter(String name, Filter filter)
  {
    this.filter.put(name, new MockFilterDef(filter));
  }

  public void addFilterMapping(String filterName, String path, int dispatcherFlags)
  {
    if (filter.containsKey(filterName) == false) {
      throw new RuntimeException("Filter with name: " + filterName + " not found");
    }
    filterMapping.add(new MockFilterMapDef(path, filter.get(filterName), dispatcherFlags));
  }

  public boolean hasAnyFilterDispatchFlag(FilterDispatchFlags flag)
  {
    for (MockFilterMapDef fmd : filterMapping) {
      if ((fmd.getDispatcherFlags() & flag.getFlags()) != 0) {
        return true;
      }
    }
    return false;
  }

  public Map<String, MockFilterDef> getFilter()
  {
    return filter;
  }

  public void setFilter(Map<String, MockFilterDef> filter)
  {
    this.filter = filter;
  }

  public List<MockFilterMapDef> getFilterMapping()
  {
    return filterMapping;
  }

  public void setFilterMapping(List<MockFilterMapDef> filterMapping)
  {
    this.filterMapping = filterMapping;
  }

}
