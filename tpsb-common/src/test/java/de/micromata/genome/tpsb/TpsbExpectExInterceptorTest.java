package de.micromata.genome.tpsb;

import org.junit.Test;

/**
 * 
 * @author Roger Rene Kommer (r.kommer.extern@micromata.de)
 * 
 */
public class TpsbExpectExInterceptorTest
{
  @Test
  public void testExpectEx()
  {
    MyFoo builder = new MyFoo();
    builder = builder.validateNextException(IllegalArgumentException.class);
    builder = builder.foo("Hello");
    Object expected = builder.getTestContextVar("lastException");
    System.out.println("Catched ex: " + expected);
  }
}
