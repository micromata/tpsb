package de.micromata.genome.tpsb.httpmockup.internaltest;

import de.micromata.genome.tpsb.httpmockup.testbuilder.HttpTestBuilder;

/**
 * Sample TestBuilder.
 * 
 * @author Roger Rene Kommer (r.kommer.extern@micromata.de)
 * 
 */
public class InternalHttpTestBuilder extends HttpTestBuilder<InternalHttpTestBuilder>
{
  public InternalHttpTestBuilder()
  {
    registerServlet("TestServletOne", "/one", TestServletOne.class);
  }

}
