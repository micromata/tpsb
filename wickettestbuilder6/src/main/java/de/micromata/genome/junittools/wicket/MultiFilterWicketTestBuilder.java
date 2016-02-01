package de.micromata.genome.junittools.wicket;

import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.util.tester.WicketTester;

/**
 * Wrapper for all the fancy multi filter wicket projects here. If you have the need to use several filters, like in the wickettestbuilder
 * 1.4 implementation, please use this class to do this.
 * 
 * @author Johannes Unterstein (j.unterstein@micromata.de)
 */
public class MultiFilterWicketTestBuilder<T extends WicketTestBuilder< ? >> extends WicketTestBuilder<T>
{
  public MultiFilterWicketTestBuilder(WebApplication application)
  {
    super(application);
  }

  @Override
  protected WicketTester _getWicketTester(WebApplication application)
  {
    return super._getWicketTester(application); // change the return value to your own special WicketTester implementation
  }
}
