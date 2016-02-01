/////////////////////////////////////////////////////////////////////////////
//
// Project   Micromata Genome Core
//
// Author    roger@micromata.de
// Created   22.01.2008
// Copyright Micromata 22.01.2008
//
/////////////////////////////////////////////////////////////////////////////
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
