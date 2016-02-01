package de.micromata.genome.tpsb;

public class MyFoo extends CommonTestBuilder<MyFoo>
{
  public MyFoo foo(String args)
  {
    if (true) {
      throw new IllegalArgumentException("Hello");
      // throw new RuntimeException("hallo");
    }
    return this;
  }
}
