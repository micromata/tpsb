package de.micromata.tpsb.doc.parser;

/**
 * Contains optional JavaDocInfo element
 * 
 * @author roger
 * 
 */
public interface WithJavaDoc
{
  /**
   * 
   * @return may return null
   */
  public JavaDocInfo getJavaDocInfo();
}
