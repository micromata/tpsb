package de.micromata.genome.tpsb;

import org.apache.commons.lang.ObjectUtils;

/**
 * Default implemenattion for addCommentExpression(...)
 * 
 * @author roger
 * 
 */
public abstract class CommonCommentListener implements TpsbCommentListener
{

  @Override
  public void addCommentExpression(TestBuilder< ? > builder, String expression)
  {
    String text = evaluate(builder, expression);
    addCommentText(builder, text);
  }

  protected String evaluate(TestBuilder< ? > builder, String expression)
  {
    Object o = CommonTestBuilder.evaluate(builder.getTestContext(), expression);
    return ObjectUtils.toString(o);
  }
}
