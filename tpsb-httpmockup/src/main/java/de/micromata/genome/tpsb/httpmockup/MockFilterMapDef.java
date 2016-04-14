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

public class MockFilterMapDef extends MockMapDef
{
  public static enum FilterDispatchFlags
  {
    REQUEST(0x01), //
    FORWARD(0x02), //
    INCLUDE(0x04), //
    ERROR(0x08);
    private int flags;

    private FilterDispatchFlags(int flags)
    {
      this.flags = flags;
    }

    public int getFlags()
    {
      return flags;
    }

  }

  private MockFilterDef filterDef;

  /**
   * Combination of combination of GFilterConfig.FilterDispatchFlags
   */
  private int dispatcherFlags;

  public MockFilterMapDef()
  {

  }

  public MockFilterMapDef(String urlPattern, MockFilterDef filterDef, int dispatcherFlags)
  {
    this.filterDef = filterDef;
    setUrlPattern(urlPattern);
    this.dispatcherFlags = dispatcherFlags;
  }

  public MockFilterDef getFilterDef()
  {
    return filterDef;
  }

  public void setFilterDef(MockFilterDef filterDef)
  {
    this.filterDef = filterDef;
  }

  @Override
  public void setDispatcher(String disp)
  {
    for (FilterDispatchFlags f : FilterDispatchFlags.values()) {
      if (f.name().equals(disp) == true) {
        dispatcherFlags |= f.getFlags();
        return;
      }
    }
    throw new RuntimeException("Unknown Filter dispatcher value: " + disp);
  }

  public int getDispatcherFlags()
  {
    return dispatcherFlags;
  }

  public void setDispatcherFlags(int dispatcherFlags)
  {
    this.dispatcherFlags = dispatcherFlags;
  }
}
