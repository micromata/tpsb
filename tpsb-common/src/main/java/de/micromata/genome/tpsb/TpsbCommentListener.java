package de.micromata.genome.tpsb;

/**
 * Listener listen to addComment() method.
 * 
 * @author roger
 * 
 */
public interface TpsbCommentListener
{
  /**
   * Add a raw comment to the listener
   * 
   * @param builder the current testbuilder
   * @param expression String expression with optional ${} expressions.
   */
  public void addCommentExpression(TestBuilder< ? > builder, String expression);

  /**
   * Inside addCommentRaw(...) the expression will be evaluated. The evaluated will be call addCommentText()
   * 
   * @param builder
   * @param text
   */
  public void addCommentText(TestBuilder< ? > builder, String text);
}
