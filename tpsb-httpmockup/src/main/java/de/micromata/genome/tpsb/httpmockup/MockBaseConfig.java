package de.micromata.genome.tpsb.httpmockup;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;

/**
 * 
 * Common parent class for both MockServletConfig and MockFilterConfig since they are both essentially the same with a couple of method
 * names changed.
 * 
 * @author Tim Fennell
 * @since Stripes 1.1.1
 */
public class MockBaseConfig
{
  private ServletContext servletContext;

  private Map<String, String> initParameters = new HashMap<String, String>();

  /** Sets the ServletContext that will be returned by getServletContext(). */
  public void setServletContext(ServletContext ctx)
  {
    this.servletContext = ctx;
  }

  /** Gets the ServletContext in whiich the filter is running. */
  public ServletContext getServletContext()
  {
    return this.servletContext;
  }

  /** Adds a value to the set of init parameters. */
  public void addInitParameter(String name, String value)
  {
    this.initParameters.put(name, value);
  }

  /** Adds all the values in the provided Map to the set of init parameters. */
  public void addAllInitParameters(Map<String, String> parameters)
  {
    this.initParameters.putAll(parameters);
  }

  /** Gets the named init parameter if it exists, or null if it doesn't. */
  public String getInitParameter(String name)
  {
    return this.initParameters.get(name);
  }

  /** Gets an enumeration of all the init parameter names present. */
  public Enumeration getInitParameterNames()
  {
    return Collections.enumeration(this.initParameters.keySet());
  }
}