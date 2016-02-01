package de.micromata.genome.tpsb;

/**
 * Root of test exception.
 * 
 * @author roger
 * 
 */
public class TpsbException extends RuntimeException
{

  private static final long serialVersionUID = -802056617581452536L;

  private TestBuilder< ? > testBuilder;

  public TpsbException()
  {
    super();
  }

  public TpsbException(TestBuilder< ? > testBuilder)
  {
    super();
    this.testBuilder = testBuilder;
  }

  public TpsbException(String message)
  {
    super(message);
  }

  public TpsbException(String message, TestBuilder< ? > testBuilder)
  {
    this(message);
    this.testBuilder = testBuilder;
  }

  public TpsbException(String message, Throwable cause)
  {
    super(message, cause);
  }

  public TpsbException(String message, Throwable cause, TestBuilder< ? > testBuilder)
  {
    this(message, cause);
    this.testBuilder = testBuilder;
  }

  public TpsbException(Throwable cause)
  {
    super(cause);
  }

  public TpsbException(Throwable cause, TestBuilder< ? > testBuilder)
  {
    super(cause);
    this.testBuilder = testBuilder;
  }

  public TestBuilder< ? > getTestBuilder()
  {
    return testBuilder;
  }

  public void setTestBuilder(TestBuilder< ? > testBuilder)
  {
    this.testBuilder = testBuilder;
  }

}
