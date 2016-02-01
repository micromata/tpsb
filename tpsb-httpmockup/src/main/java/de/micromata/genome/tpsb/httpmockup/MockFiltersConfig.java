/////////////////////////////////////////////////////////////////////////////
//
// Project   Micromata Genome Core
//
// Author    roger@micromata.de
// Created   16.01.2008
// Copyright Micromata 16.01.2008
//
/////////////////////////////////////////////////////////////////////////////
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
