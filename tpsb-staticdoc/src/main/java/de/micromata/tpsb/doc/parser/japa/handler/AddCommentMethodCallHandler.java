//
// Copyright (C) 2010-2016 Micromata GmbH
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//  http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//

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
