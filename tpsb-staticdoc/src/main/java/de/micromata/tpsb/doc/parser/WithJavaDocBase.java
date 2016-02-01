package de.micromata.tpsb.doc.parser;

import java.io.Serializable;

/**
 * 
 * @author roger
 * 
 */
public abstract class WithJavaDocBase implements WithJavaDoc, Serializable
{

  private static final long serialVersionUID = -4680456236687996022L;

  private JavaDocInfo javaDocInfo;

  public WithJavaDocBase()
  {

  }

  public WithJavaDocBase(WithJavaDocBase other)
  {
    this.javaDocInfo = other.javaDocInfo;
  }

  @Override
  public JavaDocInfo getJavaDocInfo()
  {
    return javaDocInfo;
  }

  public void setJavaDocInfo(JavaDocInfo javaDocInfo)
  {
    this.javaDocInfo = javaDocInfo;
  }
}
