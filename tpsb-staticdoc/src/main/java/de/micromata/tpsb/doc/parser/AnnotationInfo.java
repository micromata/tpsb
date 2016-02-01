package de.micromata.tpsb.doc.parser;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Holds Annotation information.
 * 
 * @author roger
 * 
 */
public class AnnotationInfo implements Serializable
{

  private static final long serialVersionUID = 3502633322355646665L;

  private String name;

  private Map<String, Object> params = new HashMap<String, Object>();

  public String getName()
  {
    return name;
  }

  public Object getDefaultAnnotationValue()
  {
    return params.get("default");
  }

  public void setDefaultAnnotationValue(Object value)
  {
    params.put("default", value);
  }

  public void setName(String name)
  {
    this.name = name;
  }

  public Map<String, Object> getParams()
  {
    return params;
  }

  public void setParams(Map<String, Object> params)
  {
    this.params = params;
  }

}
