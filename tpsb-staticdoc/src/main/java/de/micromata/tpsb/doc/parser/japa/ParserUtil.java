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

import japa.parser.ast.expr.AnnotationExpr;
import japa.parser.ast.expr.ArrayInitializerExpr;
import japa.parser.ast.expr.BooleanLiteralExpr;
import japa.parser.ast.expr.ClassExpr;
import japa.parser.ast.expr.DoubleLiteralExpr;
import japa.parser.ast.expr.Expression;
import japa.parser.ast.expr.IntegerLiteralExpr;
import japa.parser.ast.expr.LongLiteralExpr;
import japa.parser.ast.expr.MarkerAnnotationExpr;
import japa.parser.ast.expr.MemberValuePair;
import japa.parser.ast.expr.NormalAnnotationExpr;
import japa.parser.ast.expr.ObjectCreationExpr;
import japa.parser.ast.expr.SingleMemberAnnotationExpr;
import japa.parser.ast.expr.StringLiteralExpr;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.micromata.genome.tpsb.annotations.TpsbIgnore;
import de.micromata.tpsb.doc.TpsbEnvUtils;
import de.micromata.tpsb.doc.parser.AnnotationInfo;
import de.micromata.tpsb.doc.parser.FileInfo;
import de.micromata.tpsb.doc.parser.MethodInfo;
import de.micromata.tpsb.doc.parser.ParserResult;

public class ParserUtil
{
  private static Logger log = Logger.getLogger(ParserUtil.class);

  public static String extractScope(FileInfo tbFileInfo, List<Expression> list, String methodName, ParserResult testBuilders,
      String defaultScope)
  {
    MethodInfo methodInfo = tbFileInfo.findMethodInfo(methodName, parseArgs(list));
    if (methodInfo == null) {
      return defaultScope;
    }
    String returnType = methodInfo.getReturnType();
    if (testBuilders.getFileInfoForClassName(returnType) != null) {
      return returnType;
    }
    return defaultScope;
  }

  public static List<String> parseArgs(List<Expression> args)
  {
    if (args == null) {
      return new ArrayList<String>();
    }

    List<String> argTypes = new ArrayList<String>();
    for (Expression ex : args) {
      if (ex instanceof BooleanLiteralExpr) {
        argTypes.add("Boolean");
      } else if (ex instanceof DoubleLiteralExpr) {
        argTypes.add("Double");
      } else if (ex instanceof IntegerLiteralExpr) {
        argTypes.add("Integer");
      } else if (ex instanceof LongLiteralExpr) {
        argTypes.add("Long");
      } else if (ex instanceof StringLiteralExpr) {
        argTypes.add("String");
      } else if (ex instanceof ObjectCreationExpr) {
        ObjectCreationExpr objEx = (ObjectCreationExpr) ex;
        argTypes.add(objEx.getType().getName());
      } else {
        argTypes.add("Unknown");
      }
    }
    return argTypes;
  }

  private static Object parseAnnotationParamValue(Expression exp)
  {
    if (exp instanceof StringLiteralExpr) {
      return ((StringLiteralExpr) exp).getValue();
    }
    if (exp instanceof ArrayInitializerExpr) {
      ArrayInitializerExpr arexp = (ArrayInitializerExpr) exp;
      List<String> ret = new ArrayList<String>();
      for (Expression le : arexp.getValues()) {
        if (le instanceof ClassExpr) {
          ClassExpr cle = (ClassExpr) le;
          ret.add(cle.getType().toString() + ".class");
        } else {
          log.warn("Unhandled Annotation value expr type: " + le);
          ret.add(le.toString());

        }
      }
      return ret;
    } else {
      log.info("Unhandled Annotation value expr: " + exp);
      return exp.toString();
    }
  }

  public static boolean ignoreClass(FileInfo fi)
  {
    if (TpsbEnvUtils.getAnnotation(fi, TpsbIgnore.class.getSimpleName()) != null) {
      return true;
    }
    if ((fi.getModifier() & Modifier.PUBLIC) != Modifier.PUBLIC) {
      return true;
    }
    return false;
  }

  public static boolean ignoreMethod(FileInfo fi, MethodInfo mi)
  {
    if (TpsbEnvUtils.getAnnotation(fi, TpsbIgnore.class.getSimpleName()) != null) {
      return true;
    }
    if (TpsbEnvUtils.getAnnotation(mi, TpsbIgnore.class.getSimpleName()) != null) {
      return true;
    }
    if ((mi.getModifier() & Modifier.STATIC) == Modifier.STATIC) {
      return true;
    }
    if ((mi.getModifier() & Modifier.PUBLIC) != Modifier.PUBLIC) {
      return true;
    }
    return false;
  }

  public static List<AnnotationInfo> parseAnnotation(List<AnnotationExpr> anexprl)
  {
    if (anexprl == null) {
      return null;
    }
    List<AnnotationInfo> ret = new ArrayList<AnnotationInfo>();
    for (AnnotationExpr anex : anexprl) {
      AnnotationInfo aninfo = new AnnotationInfo();
      aninfo.setName(anex.getName().toString());
      if (anex instanceof SingleMemberAnnotationExpr) {
        SingleMemberAnnotationExpr sme = (SingleMemberAnnotationExpr) anex;
        Expression exp = sme.getMemberValue();
        aninfo.setDefaultAnnotationValue(parseAnnotationParamValue(exp));
      } else if (anex instanceof MarkerAnnotationExpr) {
        ; // nothing
      } else if (anex instanceof NormalAnnotationExpr) {
        NormalAnnotationExpr norman = (NormalAnnotationExpr) anex;
        for (MemberValuePair pair : norman.getPairs()) {

          aninfo.getParams().put(pair.getName(), parseAnnotationParamValue(pair.getValue()));
        }
      } else {

        log.info("Unhandled Annotation expr: " + anex);
      }

      ret.add(aninfo);
    }
    return ret;
  }

  public static boolean isPathDevider(char c)
  {
    return c == '\\' || c == '/';
  }

  /**
   * org.apache.commons.io.FilenameUtils.concat(String, String) does some normalization, we don't want. here a more simple way.
   * 
   * @param base
   * @param add
   * @return
   */
  public static String concatFileName(String base, String add)
  {
    if (StringUtils.isEmpty(base) == true) {
      return add;
    }
    if (StringUtils.isEmpty(add) == true) {
      return base;
    }
    boolean baseEnd = isPathDevider(base.charAt(base.length() - 1));
    boolean addBegin = isPathDevider(add.charAt(0));
    if ((baseEnd == true && addBegin == false) || (baseEnd == false && addBegin == true)) {
      return base + add;
    }
    if (baseEnd == true && addBegin == true) {
      return base + add.substring(1);
    } else {
      return base + '/' + add;
    }
  }

  /**
   * Looks for // comments above given source text
   * 
   * @param sourceText
   * @param lineNo
   * @return null if none found
   */
  public static String lookupInlineCode(String sourceText, int lineNo)
  {
    if (sourceText == null) {
      return null;
    }
    String[] lines = StringUtils.split(sourceText);
    if (lines == null || lines.length < lineNo) {
      return null;
    }
    String c1 = StringUtils.trim(lines[lineNo - 1]);
    if (c1.startsWith("//") == true) {
      return c1.substring(2);
    }
    return null;
  }
}
