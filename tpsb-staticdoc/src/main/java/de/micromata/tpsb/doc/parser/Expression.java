package de.micromata.tpsb.doc.parser;

import java.io.Serializable;

/**
 * Repräsentiert eine Expression die an einem Buildermethodenaufruf dokumentiert wird
 * 
 * @author lado
 * @author Roger Kommer (roger.kommer.extern@micromata.de)
 * 
 */
public class Expression implements Serializable
{

  private static final long serialVersionUID = -4232799859781685761L;

  protected String name;

  protected String expression;

  public Expression(String name, String expr)
  {
    this.name = name;
    this.expression = expr;
  }

  public String getName()
  {
    return name;
  }

  public void setName(String name)
  {
    this.name = name;
  }

  public String getExpression()
  {
    return expression;
  }

  public void setExpression(String expression)
  {
    this.expression = expression;
  }
}
