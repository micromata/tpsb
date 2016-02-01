package de.micromata.tpsb.doc.parser;

import java.io.Serializable;
import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * Repraesentiert die Parser-Information zu einem Parameter einer Java-Methoden-Signatur
 * 
 * @author Roger Kommer (roger.kommer.extern@micromata.de)
 * @author Stefan Stützer (s.stuetzer@micromata.com)
 */
public class ParameterInfo implements Serializable
{
  private static final long serialVersionUID = -4384405230282882206L;

  private String paramName;

  private String javaDoc;

  private String paramType;

  private String paramValue;

  private boolean isVarArg;

  private List<AnnotationInfo> annotations;

  public ParameterInfo()
  {
  }

  public ParameterInfo(ParameterInfo other)
  {
    this.paramName = other.getParamName();
    this.javaDoc = other.getJavaDoc();
    this.paramType = other.getParamType();
    this.paramValue = other.getParamValue();
  }

  @Override
  public String toString()
  {
    ToStringBuilder tb = new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE);
    tb.append(paramName).append(javaDoc).append(paramType).append(paramValue).append(isVarArg);
    return tb.toString();
  }

  public String getParamName()
  {
    return paramName;
  }

  public void setParamName(String paramName)
  {
    this.paramName = paramName;
  }

  public String getJavaDoc()
  {
    return javaDoc;
  }

  public void setJavaDoc(String paramrJavaDoc)
  {
    this.javaDoc = paramrJavaDoc;
  }

  public String getParamType()
  {
    return paramType;
  }

  public void setParamType(String paramType)
  {
    this.paramType = paramType;
  }

  public void setParamValue(String paramValue)
  {
    this.paramValue = paramValue;
  }

  public String getParamValue()
  {
    return paramValue;
  }

  public void setVarArg(boolean isVarArg)
  {
    this.isVarArg = isVarArg;
  }

  public boolean isVarArg()
  {
    return isVarArg;
  }

  public List<AnnotationInfo> getAnnotations()
  {
    return annotations;
  }

  public void setAnnotations(List<AnnotationInfo> annotations)
  {
    this.annotations = annotations;
  }

}
