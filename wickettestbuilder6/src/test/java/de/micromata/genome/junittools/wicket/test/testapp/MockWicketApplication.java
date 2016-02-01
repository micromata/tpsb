package de.micromata.genome.junittools.wicket.test.testapp;

import org.apache.wicket.Page;
import org.apache.wicket.protocol.http.WebApplication;

/**
 * @author Johannes Unterstein (j.unterstein@micromata.de)
 */
public class MockWicketApplication extends WebApplication
{

  @Override
  public Class< ? extends Page> getHomePage()
  {
    return HomePage.class;
  }
}
