package de.micromata.genome.tpsb;

import org.junit.Test;

public class GroovyExceptionWrapperTest
{
  @Test
  public void testGroovyWrapper()
  {
    MyFoo builder = new MyFoo();
    builder = GroovyExceptionInterceptor.wrappTestBuilder(builder, IllegalArgumentException.class);
    builder.foo("Hello");
  }

}
