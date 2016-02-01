/////////////////////////////////////////////////////////////////////////////
//
// Project   Micromata Genome Core
//
// Author    roger@micromata.de
// Created   22.01.2008
// Copyright Micromata 22.01.2008
//
/////////////////////////////////////////////////////////////////////////////
package de.micromata.genome.tpsb.httpmockup;

/**
 * 
 * @author Roger Rene Kommer (r.kommer.extern@micromata.de)
 * 
 */
public class MockErrorPageDef
{
  private String location;

  private int errorCode = -1;

  private String exceptionType;

  private Class< ? > exceptionTypeClass;

  public String getLocation()
  {
    return location;
  }

  public void setLocation(String location)
  {
    this.location = location;
  }

  public int getErrorCode()
  {
    return errorCode;
  }

  public void setErrorCode(int errorCode)
  {
    this.errorCode = errorCode;
  }

  public void setErrorCode(String errorCode)
  {
    this.errorCode = Integer.parseInt(errorCode);
  }

  public String getExceptionType()
  {
    return exceptionType;
  }

  public void setExceptionType(String exceptionType)
  {
    this.exceptionType = exceptionType;
  }

  public Class< ? > getExceptionTypeClass()
  {
    if (exceptionTypeClass == null && exceptionType != null) {
      try {
        exceptionTypeClass = Class.forName(exceptionType);
      } catch (ClassNotFoundException ex) {
        throw new RuntimeException(ex);
      }
    }
    return exceptionTypeClass;
  }

  public void setExceptionTypeClass(Class< ? > exceptionTypeClass)
  {
    this.exceptionTypeClass = exceptionTypeClass;
  }

}
