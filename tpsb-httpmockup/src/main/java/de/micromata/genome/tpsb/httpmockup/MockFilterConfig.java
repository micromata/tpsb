package de.micromata.genome.tpsb.httpmockup;

import javax.servlet.FilterConfig;

/**
 * Mock implementation of the FilterConfig interface from the Http Servlet spec.
 * 
 * @author Tim Fennell
 * @since Stripes 1.1.1
 */
public class MockFilterConfig extends MockBaseConfig implements FilterConfig
{
  private String filterName;

  /** Sets the filter name that will be retrieved by getFilterName(). */
  public void setFilterName(String filterName)
  {
    this.filterName = filterName;
  }

  /** Returns the name of the filter for which this is the config. */
  @Override
  public String getFilterName()
  {
    return this.filterName;
  }
}
