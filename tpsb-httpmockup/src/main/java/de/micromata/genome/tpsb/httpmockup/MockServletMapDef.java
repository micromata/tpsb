package de.micromata.genome.tpsb.httpmockup;

/**
 * 
 * @author Roger Rene Kommer (r.kommer.extern@micromata.de)
 * 
 */
public class MockServletMapDef extends MockMapDef
{
  private MockServletDef servletDef;

  public MockServletMapDef()
  {

  }

  public MockServletMapDef(String urlPattern, MockServletDef servletDef)
  {
    this.servletDef = servletDef;
    setUrlPattern(urlPattern);
  }

  public MockServletDef getServletDef()
  {
    return servletDef;
  }

  public void setServletDef(MockServletDef servletDef)
  {
    this.servletDef = servletDef;
  }
}