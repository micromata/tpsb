package de.micromata.genome.junittools.wicket.test;

import de.micromata.genome.junittools.wicket.WicketTestBuilder;
import de.micromata.genome.junittools.wicket.test.testapp.*;

/**
 * @author Johannes Unterstein (j.unterstein@micromata.de)
 */
public class MyWicketTestBuilder extends WicketTestBuilder<MyWicketTestBuilder>
{
  public MyWicketTestBuilder()
  {
    super(new MockWicketApplication());
  }
}
