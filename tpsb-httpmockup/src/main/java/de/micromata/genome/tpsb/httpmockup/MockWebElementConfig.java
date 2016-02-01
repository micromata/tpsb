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
