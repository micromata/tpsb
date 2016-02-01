package de.micromata.genome.tpsb;

import de.micromata.genome.tpsb.annotations.TpsbBuilder;

/**
 * Just a plain TestBuilder with no specific functions.
 * 
 * To start a Test flow, use this class and create your specific testbuilder with createBuilder(YourTestBuilder.class).
 * 
 * @author Roger Rene Kommer (r.kommer.extern@micromata.de)
 * 
 */
@TpsbBuilder
public class StartTestBuilder extends CommonTestBuilder<StartTestBuilder>
{
  public StartTestBuilder()
  {

  }

  public StartTestBuilder(CommonTestBuilder< ? > other)
  {
    super(other);
  }

}
