package de.micromata.genome.tpsb;

import org.junit.Test;

/**
 * A simple test
 * 
 * @author roger
 * 
 */
public class MyTest
{
  @Test
  public void testFirst()
  {
    new CommonTestBuilder<CommonTestBuilder< ? >>() //
        .setTestContextVar("dummy", "VALUE") //
        .createBuilder(MyTestBuilder.class) //
        .validateTestContextVar("dummy", "VALUE") //
        .validateExpression("builder.getTestContextVar('dummy').equals('VALUE') == true") //
    ;
  }
}
