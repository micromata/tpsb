package de.micromata.genome.tpsb;

/**
 * Assertion missing exception.
 * 
 * @author Roger Rene Kommer (r.kommer@micromata.de)
 * 
 */
public class AssertionMissingException extends TpsbException
{

  private static final long serialVersionUID = 3331646837334473854L;

  private Class< ? extends Throwable> missingException;

  public AssertionMissingException(final Class< ? extends Throwable> missingException)
  {
    super();
    this.missingException = missingException;
  }

  public AssertionMissingException(final Class< ? extends Throwable> missingException, TestBuilder< ? > testBuilder)
  {
    super(testBuilder);
    this.missingException = missingException;
  }

  public AssertionMissingException()
  {
    super();
  }

  public Class< ? extends Throwable> getMissingException()
  {
    return this.missingException;
  }

  public void setMissingException(final Class< ? extends Throwable> missingException)
  {
    this.missingException = missingException;
  }
}
