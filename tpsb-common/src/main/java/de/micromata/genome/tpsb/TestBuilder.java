package de.micromata.genome.tpsb;

import java.util.Map;

/**
 * Base class of test builders
 * 
 * @author roger
 * 
 */
public interface TestBuilder<T extends TestBuilder< ? >>
{
  /**
   * Creates a new Testbuilder via default constructor and copies testcontext and populates the fields.
   * 
   * @param clazz
   * @return
   */
  public <X> X createBuilder(Class<X> clazz);

  /**
   * set a context variable.
   * 
   * @param varName
   * @param value
   * @return this
   */
  public T setTestContextVar(String varName, Object value);

  /**
   * get a context var
   * 
   * @param varName
   * @return the variable or null if not exists
   */
  public Object getTestContextVar(String varName);

  /**
   * Return the test context as map
   * 
   * @return
   */
  public Map<String, Object> getTestContext();
}
