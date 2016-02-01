package de.micromata.tpsb.doc.parser.japa.handler;

import japa.parser.ast.expr.Expression;
import japa.parser.ast.expr.MethodCallExpr;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import de.micromata.tpsb.doc.ParserContext;

public class AddCommentMethodCallHandler implements INodeHandler<MethodCallExpr>
{

  public static String QUOTE = "\"";

  @Override
  public void handle(MethodCallExpr node, ParserContext ctx)
  {
    List<Expression> methodArgs = node.getArgs();
    if (methodArgs == null || methodArgs.size() != 1) {
      return;
    }
    String comment = methodArgs.iterator().next().toString();
    if (StringUtils.isBlank(comment) == true) {
      return;
    }

    if (comment.startsWith(QUOTE) == true) {
      comment = StringUtils.removeStart(comment, QUOTE);
    }

    if (comment.endsWith(QUOTE) == true) {
      comment = StringUtils.removeEnd(comment, QUOTE);
    }

    ctx.setCurrentInlineComment(comment);
  }
}
