package de.micromata.genome.tpsb.annotations;

/**
 * Mark a test as a testSuite
 * 
 * @author roger
 * 
 */
public @interface TpsbTestSuite {
  /**
   * @return true, if test case is generated.
   */
  boolean generated() default false;
}
