package de.micromata.genome.tpsb;

import org.apache.commons.lang.StringUtils;

/**
 * An test assertion failed.
 * 
 * @author roger
 * 
 */
public class AssertionFailedException extends TpsbException
{

  private static final long serialVersionUID = -7365059191838753147L;

  /**
   * Line number in tpsb source code test.
   */
  private int lineNo;

  /**
   * Class name of the tpsb test.
   */
  private String className;

  /**
   * method name of the tpsb test.
   */
  private String methodName;

  /**
   * source code line.
   */
  private String codeLine;

  public AssertionFailedException()
  {
    super();
  }

  public AssertionFailedException(String message, TestBuilder< ? > testBuilder)
  {
    super(message, testBuilder);
  }

  public AssertionFailedException(String message, Throwable cause, TestBuilder< ? > testBuilder)
  {
    super(message, cause, testBuilder);
  }

  public AssertionFailedException(String message, Throwable cause)
  {
    super(message, cause);
  }

  public AssertionFailedException(String message)
  {
    super(message);
  }

  public AssertionFailedException(TestBuilder< ? > testBuilder)
  {
    super(testBuilder);
  }

  public AssertionFailedException(Throwable cause)
  {
    super(cause);
  }

  @Override
  public String getMessage()
  {

    String msg = super.getMessage();
    if (StringUtils.isNotEmpty(className) == true) {
      msg = msg + "; " + className + "." + methodName + "(" + lineNo + "): " + codeLine;
    }
    return msg;
  }

  public int getLineNo()
  {
    return lineNo;
  }

  public void setLineNo(int lineNo)
  {
    this.lineNo = lineNo;
  }

  public String getClassName()
  {
    return className;
  }

  public void setClassName(String className)
  {
    this.className = className;
  }

  public String getMethodName()
  {
    return methodName;
  }

  public void setMethodName(String methodName)
  {
    this.methodName = methodName;
  }

  public String getCodeLine()
  {
    return codeLine;
  }

  public void setCodeLine(String codeLine)
  {
    this.codeLine = codeLine;
  }

}
