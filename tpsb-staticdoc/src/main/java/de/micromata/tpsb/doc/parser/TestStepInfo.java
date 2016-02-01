package de.micromata.tpsb.doc.parser;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Repraesentiert die Parser-Information zu einem Teststep innerhalb einer JUnit Test-Methode. Dies ist mit einem Methodenaufruf auf einem
 * TestBuilder zu vergleichen
 * 
 * @author Stefan Stützer (s.stuetzer@micromata.com)
 */
public class TestStepInfo implements WithJavaDoc, WithValidationMessages, Serializable
{

  private static final long serialVersionUID = 382133641229255807L;

  /**
   * Parent method, defining this test.
   */
  private MethodInfo testMethod;

  /** Teststep Counter */
  private int testStep;

  /** Java-Klassenname der aufgerufenen Testbuilder-Klasse */
  private String tbClassName;

  /** Java-Methodenname der aufgerufenen Testbuilder-Methode */
  private String tbMethodName;

  /** Javadoc der aufgerufnenen Testbuilder-Methode */
  private JavaDocInfo tbJavaDocInfo;

  /** Inline Javadoc im Test beim Aufruf der Testbuilder-Methode */
  private JavaDocInfo inlineJavaDocInfo;

  /** Übergebene Parameter an Testbuilder Methodenaufruf */
  private List<ParameterInfo> parameters = new ArrayList<ParameterInfo>();

  private List<Expression> expressions = new ArrayList<Expression>();

  /**
   * Wird ggf. zu einem spaeteren Zeitpunkt aufgeloest, falls das zu einem Teststep gehoert.
   */
  private transient MethodInfo testBuilderMethod;

  /**
   * The type returned by this class.
   */
  private transient FileInfo returnType;

  private transient List<String> validationMessages = null;

  /**
   * Line number in Source
   */
  private int lineNo;

  public TestStepInfo()
  {

  }

  public TestStepInfo(TestStepInfo other)
  {
    testMethod = other.testMethod;
    testStep = other.testStep;
    tbClassName = other.tbClassName;
    tbMethodName = other.tbMethodName;
    tbJavaDocInfo = other.tbJavaDocInfo;
    inlineJavaDocInfo = other.inlineJavaDocInfo;
    parameters.addAll(other.parameters);
    expressions.addAll(other.expressions);
    testBuilderMethod = other.testBuilderMethod;

  }

  @Override
  public JavaDocInfo getJavaDocInfo()
  {
    return inlineJavaDocInfo;
  }

  @Override
  public boolean isValid()
  {
    return validationMessages == null || validationMessages.isEmpty();
  }

  @Override
  public void addValidationMessage(String message)
  {
    if (validationMessages == null) {
      validationMessages = new ArrayList<String>();
    }
    validationMessages.add(message);
  }

  @Override
  public List<String> getValidationMessages()
  {
    if (validationMessages == null) {
      validationMessages = new ArrayList<String>();
    }
    return validationMessages;
  }

  @Override
  public void collectValidationMessages(List<String> ret)
  {
    if (validationMessages != null) {
      ret.addAll(validationMessages);
    }
  }

  @Override
  public void clearValidationMessages()
  {
    validationMessages = null;
  }

  @Override
  public void clearValidationMessagesWithPrefix(String prefix)
  {
    if (validationMessages == null) {
      return;
    }
    for (Iterator<String> it = validationMessages.iterator(); it.hasNext();) {
      String t = it.next();
      if (t != null && t.startsWith(prefix) == true) {
        it.remove();
      }
    }
  }

  public String getScenarioFileContent()
  {
    return getScenarioFileContent(null);
  }

  public String getScenarioFileContent(String projectRoot)
  {
    return JavaDocUtil.getScenarioInfo(projectRoot, this);
  }


  public List<Expression> getExpressions()
  {
    return expressions;
  }

  public void setExpressions(List<Expression> expressions)
  {
    this.expressions = expressions;
  }

  public void addExpression(Expression expr)
  {
    expressions.add(expr);
  }

  public int getTestStep()
  {
    return testStep;
  }

  public void setTestStep(int testStep)
  {
    this.testStep = testStep;
  }

  public List<ParameterInfo> getParameters()
  {
    return parameters;
  }

  public void setParameters(List<ParameterInfo> parameters)
  {
    this.parameters = parameters;
  }

  public String getTbMethodName()
  {
    return tbMethodName;
  }

  public void setTbMethodName(String methodName)
  {
    this.tbMethodName = methodName;
  }

  public void setInlineJavaDocInfo(JavaDocInfo inlineJavaDocInfo)
  {
    this.inlineJavaDocInfo = inlineJavaDocInfo;
  }

  public JavaDocInfo getInlineJavaDocInfo()
  {
    return inlineJavaDocInfo;
  }

  public void setTbJavaDocInfo(JavaDocInfo javaDocInfo)
  {
    this.tbJavaDocInfo = javaDocInfo;
  }

  public JavaDocInfo getTbJavaDocInfo()
  {
    return tbJavaDocInfo;
  }

  public void setTbClassName(String tbClassName)
  {
    this.tbClassName = tbClassName;
  }

  public String getTbClassName()
  {
    return tbClassName;
  }

  public String getShortTbClassName()
  {
    int lidx = tbClassName.lastIndexOf('.');
    if (lidx == -1) {
      return tbClassName;
    }
    return tbClassName.substring(lidx + 1);
  }

  public MethodInfo getTestBuilderMethod()
  {
    return testBuilderMethod;
  }

  public void setTestBuilderMethod(MethodInfo testBuilderMethod)
  {
    this.testBuilderMethod = testBuilderMethod;
  }

  public FileInfo getReturnType()
  {
    return returnType;
  }

  public void setReturnType(FileInfo returnType)
  {
    this.returnType = returnType;
  }

  public int getLineNo()
  {
    return lineNo;
  }

  public void setLineNo(int lineNo)
  {
    this.lineNo = lineNo;
  }

  public MethodInfo getTestMethod()
  {
    return testMethod;
  }

  public void setTestMethod(MethodInfo testMethod)
  {
    this.testMethod = testMethod;
  }

}