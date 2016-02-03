package de.micromata.genome.tpsb;

public class GroovyExceptionWrapperTest
{
  @Deprecated
  public void testGroovyWrapper()
  {
    MyFoo builder = new MyFoo();
    builder = GroovyExceptionInterceptor.wrappTestBuilder(builder, IllegalArgumentException.class);
    builder.foo("Hello");
  }

}
