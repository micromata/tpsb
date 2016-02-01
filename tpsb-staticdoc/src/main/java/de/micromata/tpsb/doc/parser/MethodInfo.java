package de.micromata.tpsb.doc.parser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.log4j.Logger;

/**
 * Repraesentiert die Parser-Information zu einer Java-Mathode
 * 
 * @author Roger Kommer (roger.kommer.extern@micromata.de)
 * @author Stefan Stützer (s.stuetzer@micromata.com)
 */
public class MethodInfo extends AnnotatedBase
{
  private static final Logger log = Logger.getLogger(MethodInfo.class);

  private static final long serialVersionUID = -6122186553508938101L;

  private String className;

  private String methodName;

  private String returnType;

  /**
   * public <X> X getValX(Class<X> class);
   */
  private List<String> typeArgNames;

  private List<ParameterInfo> parameters = new ArrayList<ParameterInfo>();

  private List<TestStepInfo> testSteps = new ArrayList<TestStepInfo>();

  /**
   * The builder the method contains.
   */
  transient private FileInfo classInfo;

  public MethodInfo()
  {

  }

  public MethodInfo(MethodInfo other)
  {
    super(other);
    this.className = other.className;
    this.methodName = other.methodName;
    this.returnType = other.returnType;
    if (other.typeArgNames != null) {
      typeArgNames = new ArrayList<String>();
      typeArgNames.addAll(other.typeArgNames);
    }
    this.parameters.addAll(other.parameters);
    testSteps.addAll(other.testSteps);
  }

  @Override
  public boolean isValid()
  {
    if (super.isValid() == false) {
      return false;
    }
    for (TestStepInfo ti : testSteps) {
      if (ti.isValid() == false) {
        return false;
      }
    }
    return true;
  }

  @Override
  public void clearValidationMessages()
  {
    super.clearValidationMessages();
    for (TestStepInfo si : getTestSteps()) {
      si.clearValidationMessages();
    }
  }

  @Override
  public void clearValidationMessagesWithPrefix(String prefix)
  {
    super.clearValidationMessagesWithPrefix(prefix);
    for (TestStepInfo si : getTestSteps()) {
      si.clearValidationMessagesWithPrefix(prefix);
    }
  }

  @Override
  public void collectValidationMessages(List<String> ret)
  {
    super.collectValidationMessages(ret);
    for (TestStepInfo si : getTestSteps()) {
      si.collectValidationMessages(ret);
    }
  }

  public boolean isGenericReturnType()
  {
    if (typeArgNames == null) {
      return false;
    }
    for (String typeName : typeArgNames) {
      if (StringUtils.equals(typeName, returnType) == true) {
        return true;
      }
    }
    return false;
  }

  public boolean isTpsbMetaMethod()
  {
    if (getAnnotations() == null) {
      return false;
    }
    for (AnnotationInfo ai : getAnnotations()) {
      if (ai.getName().equals("TpsbMetaMethod") == true) {
        return true;
      }
    }
    return false;
  }

  public List<TestStepInfo> getTestSteps()
  {
    return testSteps;
  }

  public void addParamInfo(ParameterInfo pInfo)
  {
    getParameters().add(pInfo);
  }

  private void setDirty()
  {
    if (classInfo != null) {
      classInfo.setDirty(true);
    } else {
      log.warn("classInfo in method not set: " + className + "." + methodName);
    }
  }

  public void addTestStepInfo(TestStepInfo tInfo)
  {
    tInfo.setTestStep(testSteps.size() + 1);
    testSteps.add(tInfo);
    tInfo.setTestMethod(this);
    setDirty();
  }

  public void removeTestStep(TestStepInfo ts)
  {
    testSteps.remove(ts);
    setDirty();
  }

  public void replaceTestStep(TestStepInfo oldTs, TestStepInfo newTs)
  {
    Collections.replaceAll(getTestSteps(), oldTs, newTs);
    setDirty();
  }

  public int getParamCount()
  {
    if (getParameters() == null || getParameters().isEmpty() == true) {
      return 0;
    }
    return getParameters().size();
  }

  public boolean hasVarArgs()
  {
    if (getParamCount() == 0) {
      return false;
    }
    ParameterInfo lastParam = getParameters().get(getParamCount() - 1);
    return lastParam.isVarArg();
  }

  /**
   * Überprüft ob die Methode die übergebenen Argumente unterstüzt.
   * 
   * @param argTypes die übergebenen Argumente
   * @return true wenn die Methode die Parameter untertützt, ansonsten false.
   */
  public boolean matchArgTypes(List<String> argTypes)
  {
    for (int index = 0; argTypes.size() < index; index++) {
      if (!matchArgType(index, argTypes.get(index))) {
        return false;
      }
    }
    return true;
  }

  // TODO: assignable prüfen
  public boolean matchArgType(int idx, String type)
  {
    if (idx > getParamCount() && hasVarArgs() == false) {
      return false;
    } else if (idx > getParamCount() && hasVarArgs() == true) {
      ParameterInfo parameterInfo = getParameters().get(getParamCount() - 1);
      return StringUtils.equalsIgnoreCase(parameterInfo.getParamType(), type);
    } else {
      ParameterInfo parameterInfo = getParameters().get(idx - 1);
      return StringUtils.equalsIgnoreCase(parameterInfo.getParamType(), type);
    }
  }

  public String getScenarioContent()
  {
    return JavaDocUtil.getScenarioFromTestMethod(this);
  }

  public String getScenarioHtmlContent()
  {
    String ret = JavaDocUtil.getScenarioHtmlFromTestMethod(this);
    return ret;
  }

  public String getClassName()
  {
    return className;
  }

  public void setClassName(String className)
  {
    this.className = className;
  }

  public ParameterInfo getParameterAtPos(int idx)
  {
    if (getParamCount() == 0) {
      return null;
    }

    for (int i = idx; idx >= 0; i--) {
      if (getParameters().size() <= i) {
        continue;
      }
      return getParameters().get(i);
    }
    return null;
  }

  @Override
  public String toString()
  {
    ToStringBuilder tb = new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE);
    tb.append(methodName).append(getJavaDocInfo()).append(getParameters());

    return tb.toString();
  }

  public void setReturnType(String returnType)
  {
    this.returnType = returnType;
  }

  public String getReturnType()
  {
    return returnType;
  }

  public void setMethodName(String methodName)
  {
    this.methodName = methodName;
  }

  public String getMethodName()
  {
    return methodName;
  }

  public void setParameters(List<ParameterInfo> parameters)
  {
    this.parameters = parameters;
  }

  public List<ParameterInfo> getParameters()
  {
    return parameters;
  }

  public FileInfo getClassInfo()
  {
    return classInfo;
  }

  public void setClassInfo(FileInfo classInfo)
  {
    this.classInfo = classInfo;
  }

  public List<String> getTypeArgNames()
  {
    return typeArgNames;
  }

  public void setTypeArgNames(List<String> typeArgNames)
  {
    this.typeArgNames = typeArgNames;
  }

}
