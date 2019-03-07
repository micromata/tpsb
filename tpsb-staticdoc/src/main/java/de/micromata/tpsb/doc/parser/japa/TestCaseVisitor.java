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

package de.micromata.tpsb.doc.parser.japa;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.comments.JavadocComment;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.BooleanLiteralExpr;
import com.github.javaparser.ast.expr.ClassExpr;
import com.github.javaparser.ast.expr.DoubleLiteralExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.IntegerLiteralExpr;
import com.github.javaparser.ast.expr.LiteralExpr;
import com.github.javaparser.ast.expr.LongLiteralExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.expr.UnaryExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.Type;

import de.micromata.tpsb.doc.ParserContext;
import de.micromata.tpsb.doc.TpsbEnvironment;
import de.micromata.tpsb.doc.parser.FileInfo;
import de.micromata.tpsb.doc.parser.JavaDocInfo;
import de.micromata.tpsb.doc.parser.JavaDocUtil;
import de.micromata.tpsb.doc.parser.MethodInfo;
import de.micromata.tpsb.doc.parser.ParameterInfo;
import de.micromata.tpsb.doc.parser.TestStepInfo;
import de.micromata.tpsb.doc.parser.japa.handler.INodeHandler;

/**
 * AST Visitor welche fuer Testcases interessante Informationen besucht
 * 
 * @author Stefan Stützer (s.stuetzer@micromata.com)
 * @author Roger Kommer (roger.kommer.extern@micromata.de)
 */
public class TestCaseVisitor extends TestBuilderVisitor
{

  private final static Logger log = Logger.getLogger(TestCaseVisitor.class);

  private int methodDepth = 0;

  @Override
  public void visit(ClassOrInterfaceDeclaration n, ParserContext ctx)
  {
    super.visitClassOrInterfaceDeclaration(n, ctx, false);
  }

  @Override
  public void visit(FieldDeclaration n, ParserContext ctx)
  {
    if (n.getVariables() == null) {
      super.visit(n, ctx);
      return;
    }
    for (VariableDeclarator var : n.getVariables()) {
      if (var.getInit() != null && var.getInit() instanceof ObjectCreationExpr) {
        ClassOrInterfaceType type = ((ObjectCreationExpr) var.getInit()).getType();
        ctx.addField(var.getId().getName(), type.toString());
        log.debug(IND_2 + "Felddeklaration: " + type + " " + var.getId());
      }
    }
    super.visit(n, ctx);
  }

  @Override
  public void visit(VariableDeclarationExpr n, ParserContext ctx)
  {
    if (n.getVars() == null) {
      super.visit(n, ctx);
      return;
    }

    for (VariableDeclarator var : n.getVars()) {
      if (var.getInit() == null) {
        super.visit(n, ctx);
        return;
      }

      // 1. unterstütze Fall: Direkte Instanziierung (z.B. TestBuilder a =
      // new TestBuilder();)
      if (var.getInit() instanceof ObjectCreationExpr) {
        ClassOrInterfaceType type = ((ObjectCreationExpr) var.getInit()).getType();
        ctx.addLocalVar(var.getId().getName(), type.toString());
        log.debug(IND_2 + "Variablen Deklaration: " + type + " " + var.getId());
      }

      // 2. unterstütze Fall: Instanziierung in Methodenargument (z.B.
      // TestBuilder a = Proxy.wrap(new TestBuilder());)
      else if (var.getInit() instanceof MethodCallExpr) {
        MethodCallExpr methodCall = (MethodCallExpr) var.getInit();
        if (methodCall.getArgs() != null && methodCall.getArgs().size() != 0) {
          Expression nv = methodCall.getArgs().iterator().next();
          if (nv instanceof ObjectCreationExpr) {
            ClassOrInterfaceType type = ((ObjectCreationExpr) methodCall.getArgs().iterator().next()).getType();
            ctx.addLocalVar(var.getId().getName(), type.toString());
            log.debug(IND_2 + "Variablen Deklaration: " + type + " " + var.getId());
          } else if (nv instanceof ClassExpr) {
            ClassExpr clazzExpr = (ClassExpr) nv;
            Type type = clazzExpr.getType();
            ctx.addLocalVar(var.getId().getName(), type.toString());
            log.debug(IND_2 + "Variablen Deklaration: " + type + " " + var.getId());
          } else {
            log.warn(IND_2 + "Unknown VariableDeclarator init type: " + var.getInit());
          }
        }
      } else if (var.getInit() instanceof LiteralExpr) {

      } else {
        log.warn(IND_2 + "Unknown VariableDeclarator type: " + var);
      }
    }
    super.visit(n, ctx);
  }

  /**
   * Checks if the methods is a test method ==&gt; Either starts with test or has an annotation @Test
   * 
   * @param n the methoddeclaration which is to test if it is a test
   * @return true if it is a test method TODO consider check if the test method is void, etc and with no args?
   */
  protected boolean isTest(MethodDeclaration n)
  {
    if ((n.getModifiers() & Modifier.PUBLIC) != Modifier.PUBLIC) {
      return false;
    }
    if ((n.getModifiers() & Modifier.STATIC) == Modifier.STATIC) {
      return false;
    }
    if (n.getName().startsWith("test")) {
      return true;
    }
    if (n.getAnnotations() == null) {
      return false;
    }

    for (AnnotationExpr ae : n.getAnnotations()) {
      if (ae.getName().getName().equals("Test")) {
        return true;
      }
      if (ae.getName().getName().equals("TpsbMetaMethod") == true) {
        return true;
      }
    }

    return false;
  }

  @Override
  public void visit(MethodDeclaration n, ParserContext ctx)
  {
    if (isTest(n)) {
      super.visit(n, ctx);
    }
  }

  /**
   * Besuche einen Methodenaufruf auf einem Testbuilder
   */
  @Override
  public void visit(MethodCallExpr n, ParserContext ctx)
  {

    //    super.visit(n, ctx);
    if (n.getScope() != null) {
      n.getScope().accept(this, ctx);
    }

    if (reservedMethods.contains(n.getName()) == true) {
      @SuppressWarnings("unchecked")
      INodeHandler<Node> handler = (INodeHandler) handlers.get(n.getName());
      handler.handle(n, ctx);
      return;
    }
    // leider ist das immer begin der gesamten Expression.
    int line = n.getBeginLine();

    String inlineComment = null;//ParserUtil.lookupInlineCode(ctx.getSourceText(), line);
    log.debug(IND_3 + "Methodenaufruf: " + n.getName());
    if (n.getScope() instanceof NameExpr) {
      String type = ctx.getTypeOfVariable(n.getScope().toString());
      ctx.setCurrentScope(type);
    } else if (n.getScope() instanceof ObjectCreationExpr) {
      ObjectCreationExpr oce = (ObjectCreationExpr) n.getScope();
      ctx.setCurrentScope(oce.getType().toString());
    } else if (n.getScope() instanceof MethodCallExpr) {

      ; // nothing
    } else {
      log.warn(IND_4 + "unknown expression in method call: " + n.getScope());
    }
    String scopeAsString = ctx.getCurrentScope() == null ? "" : ctx.getCurrentScope().toString();
    log.debug(IND_4 + "auf Objekt: " + scopeAsString);
    if (StringUtils.isEmpty(scopeAsString)) {
      log.warn(IND_4 + "No scope for Method: " + n.getScope());
      return;
    }
    // In Testbuilderdaten den Methodenaufruf suchen
    FileInfo tbFileInfo = ctx.getTestBuilderInfoFromCurrentScope(ctx.getCurrentScope());
    if (tbFileInfo == null) {
      log.warn(IND_4 + "Testbuilder nicht gefunden: " + n.getName() + //
          ", scope="
          + ctx.getCurrentScope());
      return;
    }
    List<String> argTypes = parseArgs(n.getArgs());
    String scope = extractScope(ctx, n.getArgs(), n.getName());

    ctx.setCurrentScope(scope);
    log.debug(IND_3 + "New Scope: " + scope);
    MethodInfo calledTestbuilderMethodInfo = tbFileInfo.findMethodInfo(n.getName(), argTypes);
    if (calledTestbuilderMethodInfo == null) {
      log.error("Canot find method " + n.getName() + ", scope=" + ctx.getCurrentScope());
      // only for debugging
      calledTestbuilderMethodInfo = tbFileInfo.findMethodInfo(n.getName(), argTypes);
    }
    // TestStep dokumentieren
    TestStepInfo stepInfo = new TestStepInfo();
    stepInfo.setLineNo(n.getBeginLine());
    stepInfo.setTbClassName(tbFileInfo.getClassName());
    if (calledTestbuilderMethodInfo != null) {
      stepInfo.setTbMethodName(calledTestbuilderMethodInfo.getMethodName());
      stepInfo.setTbJavaDocInfo(calledTestbuilderMethodInfo.getJavaDocInfo());
    }
    if (StringUtils.isNotBlank(ctx.getCurrentInlineComment()) == true) {
      JavaDocInfo inlineJavaDocInfo = JavaDocUtil.parseJavaDoc(new JavadocComment(ctx.getCurrentInlineComment()));
      stepInfo.setInlineJavaDocInfo(inlineJavaDocInfo);
      ctx.resetCurrentInlineComment();
    } else if (inlineComment != null) {
      stepInfo.setInlineJavaDocInfo(new JavaDocInfo(inlineComment));
    }
    if (calledTestbuilderMethodInfo != null) {
      if (n.getArgs() != null) {
        for (int i = 0; i < n.getArgs().size(); i++) {
          Expression expr = n.getArgs().get(i);
          ParameterInfo pInfo = calledTestbuilderMethodInfo.getParameterAtPos(i);
          if (pInfo == null) {
            continue;
          }

          ParameterInfo callArgument = new ParameterInfo(pInfo);
          callArgument.setParamValue(expr.toString());
          stepInfo.getParameters().add(callArgument);
          log.debug(IND_4 + "Argument: " + expr.toString());
        }
      }
    }
    MethodInfo currentMethodInfo = ctx.findMethodInfo(ctx.getCurrentClassName(), ctx.getCurrentMethodName());
    if (currentMethodInfo != null) {
      currentMethodInfo.addTestStepInfo(stepInfo);
    }

  }

  private String resolveTypeFromGenericSupers(ParserContext ctx, MethodInfo methodInfo)
  {
    String retName = methodInfo.getReturnType();
    FileInfo thisTb = TpsbEnvironment.get().findTestBuilder(ctx.getCurrentScope());
    if (thisTb == null || thisTb.getSuperTemplateArgs() == null || thisTb.getSuperTemplateArgs().isEmpty() == true) {
      return null;
    }
    if (thisTb.getSuperClassFileInfo() == null) {
      return null;
    }
    FileInfo superF = thisTb.getSuperClassFileInfo();
    if (superF.getTypeArgNames() == null) {
      return null;
    }
    // TODO RK das hier ist nicht richtig, man muss positional die Generic-Namen aufloesen.
    for (int i = 0; i < superF.getTypeArgNames().size(); ++i) {
      String typeArgName = superF.getTypeArgNames().get(i);
      if (retName.equals(typeArgName) == true) {
        if (thisTb.getSuperTemplateArgs().size() > i) {
          FileInfo retTypeFi = TpsbEnvironment.get().findTestBuilder(thisTb.getSuperTemplateArgs().get(i));
          if (retTypeFi != null) {
            return retTypeFi.getClassName();
          }
        }
      }
    }
    return null;
  }

  private String extractScope(ParserContext ctx, List<Expression> list, String methodName)
  {
    FileInfo tbFileInfo = ctx.getTestBuilderInfoFromCurrentScope(ctx.getCurrentScope());
    MethodInfo methodInfo = tbFileInfo.findMethodInfo(methodName, parseArgs(list));
    if (methodInfo == null) {
      log.error("Cannot resolve return type for " + tbFileInfo.getClassName() + "." + methodName + "(" + list + ")");
      return ctx.getCurrentScope();
    }
    String returnType = methodInfo.getReturnType();

    if (ctx.getParsedTestBuilders() != null) {
      if (ctx.getParsedTestBuilders().getFileInfoForClassName(returnType) != null) {
        return returnType;
      }

      String clazzName = extractArgs(ctx, list);
      if (ctx.getParsedTestBuilders().getFileInfoForClassName(clazzName) != null) {
        return clazzName;
      }
    }
    FileInfo fi = TpsbEnvironment.get().findTestBuilder(returnType);
    if (fi != null) {
      return fi.getClassName();
    }
    String resolvedTypeName = resolveTypeFromGenericSupers(ctx, methodInfo);
    if (resolvedTypeName != null) {
      return resolvedTypeName;
    }
    String clazzName = extractArgs(ctx, list);
    fi = TpsbEnvironment.get().findTestBuilder(clazzName);
    if (fi != null) {
      return fi.getClassName();
    }
    log.error("Cannot resolve testbuilder class: " + returnType + " " + methodInfo.getClassName() + "."
        + methodInfo.getMethodName());
    // TODO look for fqpath
    return ctx.getCurrentScope();
  }

  private String extractArgs(ParserContext ctx, List<Expression> list)
  {
    if (CollectionUtils.isEmpty(list)) {
      return StringUtils.EMPTY;
    }
    Expression expr = list.get(0);
    if (!(expr instanceof ClassExpr)) {
      if (list.size() > 1 && list.get(list.size() - 1) instanceof ClassExpr) {
        expr = list.get(list.size() - 1);
      } else {
        return StringUtils.EMPTY;
      }
    }
    ClassExpr clazzExpr = (ClassExpr) expr;
    Type type = clazzExpr.getType();
    String fullQualifiedClassName = ctx.getFullQualifiedNameFromImports(type.toString());
    if (StringUtils.isNotEmpty(fullQualifiedClassName)) {
      return type.toString();
    }
    return ctx.getCurrentClassName();

  }

  public String parseArg(Expression ex)
  {
    if (ex instanceof BooleanLiteralExpr) {
      return "Boolean";
    } else if (ex instanceof DoubleLiteralExpr) {
      return "Double";
    } else if (ex instanceof IntegerLiteralExpr) {
      return "Integer";
    } else if (ex instanceof LongLiteralExpr) {
      return "Long";
    } else if (ex instanceof StringLiteralExpr) {
      return "String";
    } else if (ex instanceof ObjectCreationExpr) {
      ObjectCreationExpr objEx = (ObjectCreationExpr) ex;
      return objEx.getType().getName();
    } else if (ex instanceof ClassExpr) {
      ClassExpr clsExpr = (ClassExpr) ex;
      Type type = clsExpr.getType();
      String shortClassName = type.toString();
      // TODO have to resolve
      return shortClassName;
    } else if (ex instanceof UnaryExpr) {
      UnaryExpr unary = (UnaryExpr) ex;
      return parseArg(unary.getExpr());
    } else {

      log.warn("Cannot understand type: " + ex);
      return "Unknown";
    }

  }

  public List<String> parseArgs(List<Expression> args)
  {
    if (args == null) {
      return new ArrayList<String>();
    }

    List<String> argTypes = new ArrayList<String>();
    for (Expression ex : args) {
      argTypes.add(parseArg(ex));
    }
    return argTypes;
  }
}