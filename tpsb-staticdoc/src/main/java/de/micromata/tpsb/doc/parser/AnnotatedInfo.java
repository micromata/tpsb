package de.micromata.tpsb.doc.parser;

import java.util.List;

/**
 * 
 * @author roger
 * 
 */
public interface AnnotatedInfo
{
  /**
   * 
   * @return may return null;
   */
  public List<AnnotationInfo> getAnnotations();
}
