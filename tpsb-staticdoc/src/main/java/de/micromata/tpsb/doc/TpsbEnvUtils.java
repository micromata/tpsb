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

package de.micromata.tpsb.doc;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.log4j.Logger;

import de.micromata.genome.tpsb.annotations.TpsbIgnore;
import de.micromata.genome.tpsb.annotations.TpsbTestSuite;
import de.micromata.genome.util.types.Pair;
import de.micromata.tpsb.doc.parser.AnnotatedBase;
import de.micromata.tpsb.doc.parser.AnnotatedInfo;
import de.micromata.tpsb.doc.parser.AnnotationInfo;
import de.micromata.tpsb.doc.parser.FileInfo;
import de.micromata.tpsb.doc.parser.JavaDocInfo;
import de.micromata.tpsb.doc.parser.JavaDocUtil;
import de.micromata.tpsb.doc.parser.MethodInfo;
import de.micromata.tpsb.doc.parser.ParameterInfo;
import de.micromata.tpsb.doc.parser.TestStepInfo;
import de.micromata.tpsb.doc.parser.TypeUtils;

/**
 * Misc tools to hande Env
 * 
 * @author roger
 * 
 */
public class TpsbEnvUtils
{
  private static final Logger log = Logger.getLogger(TpsbEnvironment.class);

  public static String DefaultBaseBuilderName = de.micromata.genome.tpsb.StartTestBuilder.class.getName();

  public static MethodInfo resolveTestSteps(TpsbEnvironment env, MethodInfo testMethod)
  {
    testMethod.clearValidationMessagesWithPrefix("ResolveSteps;");
    for (TestStepInfo ts : testMethod.getTestSteps()) {
      // ts.clearValidationMessages();
      if (ts.getTestBuilderMethod() == null) {
        String className = ts.getTbClassName();
        FileInfo tmi = env.findTestBuilder(className);
        if (tmi == null) {
          ts.addValidationMessage("ResolveSteps; Cannot resolve class: " + className);
          env.findTestBuilder(className);
          continue;
        }
        MethodInfo tm = tmi.findMethodInfo(ts.getTbMethodName());
        if (tm == null) {
          ts.addValidationMessage("ResolveSteps; Cannot resolve class method: " + tmi.getClassName() + "." + ts.getTbMethodName());
          continue;
        }
        ts.setTestBuilderMethod(tm);
      }
      if (ts.getTestBuilderMethod() == null) {
        continue;
      }
      resolveTmReturnType(env, ts, ts.getTestBuilderMethod());
      resolveTsJavaDoc(env, ts, ts.getTestBuilderMethod());
    }
    validateStepFlow(env, testMethod);
    return testMethod;
  }

  public static boolean isGenericType(String type)
  {
    return /* type.length() < 3 && */StringUtils.isAllUpperCase(type) == true;
  }

  public static void validateStepFlow(TpsbEnvironment env, MethodInfo tm)
  {
    FileInfo previousReturn = null;
    for (TestStepInfo ti : tm.getTestSteps()) {
      if (ti.getTbMethodName() == null || ti.getTbClassName() == null) {
        ti.addValidationMessage("ResolveSteps; unknown TestBuilder class or method");
        continue;
      }
      if (previousReturn == null) {
        if (ti.getTbMethodName().equals("createBuilder") == true
            && ti.getTbClassName().endsWith(TypeUtils.getShortClassName(DefaultBaseBuilderName)) == true) {
          ; // is ok

        } else {
          ti.addValidationMessage("ResolveSteps; Don't know how to resume from previous step");
        }
        previousReturn = ti.getReturnType();
        continue;
      }
      String tbname = ti.getTbClassName();
      if (previousReturn.getClassName().equals(tbname) == false && previousReturn.getShortClassName().equals(tbname) == false) {
        ti.addValidationMessage("ResolveSteps; Previous step results into "
            + previousReturn.getShortClassName()
            + " but this step needs: "
            + tbname);
      }
      previousReturn = ti.getReturnType();
    }
  }

  private static void resolveTsJavaDoc(TpsbEnvironment env, TestStepInfo ts, MethodInfo tm)
  {
    if (ts.getTbJavaDocInfo() != null) {
      return;
    }
    FileInfo fi = env.findTestBuilder(ts.getTbClassName());
    if (fi == null) {
      return;
    }
    List<String> types = new ArrayList<String>();
    for (ParameterInfo pi : ts.getParameters()) {
      types.add(pi.getParamType());
    }
    MethodInfo bm = fi.findMethodInfo(ts.getTbMethodName(), types);
    if (bm == null) {
      return;
    }
    JavaDocInfo jdoc = bm.getJavaDocInfo();
    ts.setTbJavaDocInfo(jdoc);
  }

  private static FileInfo resolveGenericType(TpsbEnvironment env, String genericType, FileInfo currentBuilder, MethodInfo tm)
  {

    if (currentBuilder.getSuperTemplateArgs() == null) {
      return null;
    }
    List<String> replaceMents = new ArrayList<String>();
    for (String s : currentBuilder.getSuperTemplateArgs()) {
      replaceMents.add(currentBuilder.resolveFqClassInfoFromImports(s));
    }

    FileInfo superF = currentBuilder.getSuperClassFileInfo();
    while (superF != null) {
      if (superF.getTypeArgNames() == null) {
        return null;
      }
      if (superF.getMethodInfos().contains(tm) == true) {
        int idx = superF.getTypeArgNames().indexOf(genericType);
        if (idx != -1 && idx < replaceMents.size()) {
          return env.findTestBuilder(replaceMents.get(idx));
        }
      }
      if (superF.getSuperTemplateArgs() == null) {
        return null;
      }
      // handle A<X, U> extends B<U,X>
      List<String> nReplacements = new ArrayList<String>();

      for (int i = 0; i < superF.getSuperTemplateArgs().size(); ++i) {
        String passName = superF.getSuperTemplateArgs().get(i);
        int idx = superF.getTypeArgNames().indexOf(passName);
        if (idx != -1) {
          nReplacements.add(replaceMents.get(idx));
        } else {
          nReplacements.add(superF.resolveFqClassInfoFromImports(passName));
        }
      }
      replaceMents = nReplacements;
      superF = superF.getSuperClassFileInfo();
    }
    return null;
  }

  private static void resolveTmReturnType(TpsbEnvironment env, TestStepInfo ts, MethodInfo tm)
  {
    if (ts.getReturnType() != null) {
      return;
    }
    String returnType = tm.getReturnType();
    if (isGenericType(returnType) == true) {
      if (tm != null && tm.getParameters().size() == ts.getParameters().size()) {
        for (int i = 0; i < tm.getParameters().size(); ++i) {
          ParameterInfo typeP = tm.getParameters().get(i);
          ParameterInfo valP = ts.getParameters().get(i);
          String tn = valP.getParamValue();
          if (typeP.getParamType().equals("Class<" + returnType + ">") == true) {
            if (tn.endsWith(".class") == true) {
              tn = tn.substring(0, tn.length() - ".class".length());
            }
            FileInfo fi = env.findTestBuilder(tn);
            if (fi == null) {
              ts.addValidationMessage("ResolveSteps; Cannot find return type for Method1: " + ts.getTbMethodName() + " for class parameter");
            }
            ts.setReturnType(fi);
            return;
          }
        }
      }
      for (ParameterInfo pi : ts.getParameters()) {
        if (pi.getParamType().equals("Class<" + returnType + ">") == true) {
          String tn = pi.getParamValue();
          if (tn.endsWith(".class") == true) {
            tn = tn.substring(0, tn.length() - ".class".length());
          }
          FileInfo fi = env.findTestBuilder(tn);
          if (fi == null) {
            ts.addValidationMessage("ResolveSteps; Cannot find return type for Method1: " + ts.getTbMethodName() + " for class parameter");
          }
          ts.setReturnType(fi);
          return;
        }
      }
      // try here to resolve template args
      FileInfo fi = env.findTestBuilder(ts.getTbClassName());
      if (fi != null) {
        fi = resolveGenericType(env, returnType, fi, tm);
        if (fi != null) {
          ts.setReturnType(fi);
        } else {
          ts.addValidationMessage("ResolveSteps; Cannot determine template return type "
              + returnType
              + " for Method: "
              + ts.getTbMethodName());
        }
      }
    } else {
      FileInfo fi = env.findTestBuilder(returnType);
      if (fi == null) {
        ts.addValidationMessage("ResolveSteps; Cannot resolve return type for Method " + ts.getTbMethodName());
      }
      ts.setReturnType(fi);
    }
  }

  static String getDefaultValue(ParameterInfo np)
  {
    if ("int".equals(np.getParamType()) == true) {
      return "0";
    }
    if ("boolean".equals(np.getParamType()) == true) {
      return "false";
    }
    if ("String".equals(np.getParamType()) == true) {
      return "";
    }
    return "";
  }

  public static void createDefaultParams(TestStepInfo target, MethodInfo builderMethod)
  {
    List<ParameterInfo> pl = new ArrayList<ParameterInfo>();
    for (ParameterInfo p : builderMethod.getParameters()) {
      ParameterInfo np = new ParameterInfo(p);
      np.setParamValue(getDefaultValue(np));
      pl.add(np);
    }
    target.setParameters(pl);
  }

  public static void replaceOrAddMethod(FileInfo fi, MethodInfo nmethod)
  {
    MethodInfo om = fi.findMethodInfo(nmethod.getMethodName());
    if (om != null) {
      fi.removeMethodInfo(om);
    }
    fi.addMethodInfo(nmethod);
    fi.setDirty(true);
  }

  public static boolean insertNewTestMethod(FileInfo fi, String methodName)
  {
    MethodInfo om = fi.findMethodInfo(methodName);
    if (om != null) {
      return false;
    }
    fi.addImport(DefaultBaseBuilderName);
    MethodInfo mi = new MethodInfo();
    mi.setClassInfo(fi);

    mi.setReturnType("void");
    mi.setMethodName(methodName);
    addTestAnnotation(fi, mi);
    TestStepInfo is = new TestStepInfo();
    is.setTestMethod(mi);
    is.setTbMethodName("createBuilder");
    is.setTbClassName(TypeUtils.getShortClassName(TpsbEnvUtils.DefaultBaseBuilderName));
    ParameterInfo pi = new ParameterInfo();
    pi.setParamName("builder");
    pi.setParamType("Class<T>");
    pi.setParamValue(TpsbEnvUtils.DefaultBaseBuilderName + ".class");
    is.getParameters().add(pi);
    mi.getTestSteps().add(is);
    fi.addMethodInfo(mi);
    return true;
  }

  public static boolean deleteTestMethod(FileInfo fi, String methodName)
  {
    MethodInfo om = fi.findMethodInfo(methodName);
    if (om == null) {
      return false;
    }
    fi.removeMethodInfo(om);
    return true;
  }

  public static FileInfo createNewTestSuite(String className)
  {
    FileInfo fi = new FileInfo();
    fi.setClassName(className);
    String testClaseAnnotation = TpsbTestSuite.class.getName();
    fi.addImport(testClaseAnnotation);
    AnnotationInfo ai = new AnnotationInfo();
    ai.setName(TypeUtils.getShortClassName(testClaseAnnotation));
    ai.getParams().put("generated", true);
    fi.addAnnotation(ai);
    return fi;
  }

  private static ParameterInfo getParameterFromInfo(MethodInfo mi, ParameterInfo pi)
  {
    if (mi == null) {
      return null;
    }
    for (ParameterInfo pmi : mi.getParameters()) {
      if (pmi.getParamName().equals(pi.getParamName()) == true) {
        return pmi;
      }
    }
    return null;
  }

  /**
   * 
   * @param mi the methodinfo
   * @param pi the parameter info
   * @param projectRoot the root of the project
   * @return null, if no dropdownlist should be shown
   */
  public static List<String> getPossibleValue(String projectRoot, MethodInfo mi, ParameterInfo pi)
  {
    ParameterInfo pmi = getParameterFromInfo(mi, pi);

    if (pmi != null && pmi.getAnnotations() != null) {
      for (AnnotationInfo ai : pmi.getAnnotations()) {
        String name = ai.getName();
        if (StringUtils.equals(name, "TpsbParamStringValues") == true) {
          return (List<String>) ai.getDefaultAnnotationValue();
        }
      }
    }
    AnnotationInfo scenarioFileAnotation = getAnnotation(mi, "TpsbScenarioFile");
    if (scenarioFileAnotation != null) {
      List<String> ret = JavaDocUtil.getScenarioFileNames(projectRoot, scenarioFileAnotation);
      if (ret != null) {
        return ret;
      }
    }
    AnnotationInfo returnTypesAnnotation = getAnnotation(mi, "TpsbReturnTypes");
    if (returnTypesAnnotation != null) {
      if (pi.getParamType().startsWith("Class<") == true) {
        List<String> stripedClassed = new ArrayList<String>();
        for (String s : (List<String>) returnTypesAnnotation.getDefaultAnnotationValue()) {
          stripedClassed.add(TypeUtils.stripClassEnd(s));
        }
        List<String> s = mi.getClassInfo().resolveFqClassInfoFromImports(stripedClassed);
        for (int i = 0; i < s.size(); ++i) {
          s.set(i, TypeUtils.appendClassEnd(s.get(i)));
        }
        return s;
      }
    }
    String ptype = pi.getParamType();
    if (ptype.equals("Class") == true || ptype.startsWith("Class<") == true) {
      List<String> l = new ArrayList<String>();
      Collection<String> names = TpsbEnvironment.get().getFqTestBuilderNames();
      for (String n : names) {
        String tn = n;
        if (tn.endsWith(".class") == false) {
          tn += ".class";
        }
        l.add(tn);
      }
      Collections.sort(l);
      return l;
    } else if (ptype.equals("boolean") == true) {
      List<String> l = new ArrayList<String>();
      l.add("true");
      l.add("false");
      return l;
    }
    return null;
  }

  public static boolean isAvailableForTpsbConsole(MethodInfo mi)
  {
    if (getAnnotation(mi, TpsbIgnore.class.getSimpleName()) != null) {
      return false;
    }
    if ((mi.getModifier() & Modifier.PUBLIC) != Modifier.PUBLIC) {
      return false;
    }
    if ((mi.getModifier() & Modifier.STATIC) == Modifier.STATIC) {
      return false;
    }
    return true;
  }

  public static List<MethodInfo> getAvailableBuilderMethods(FileInfo fi)
  {
    List<MethodInfo> ret = new ArrayList<MethodInfo>();
    collectAvailableBuilderMethods(fi, ret);
    return ret;
  }

  public static void collectAvailableBuilderMethods(FileInfo fi, List<MethodInfo> ret)
  {
    for (MethodInfo mi : fi.getMethodInfos()) {
      if (isAvailableForTpsbConsole(mi) == true) {
        ret.add(mi);
      }
    }
    if (fi.getSuperClassFileInfo() == null && StringUtils.isNotEmpty(fi.getSuperClassName()) == true) {
      fi.setSuperClassFileInfo(TpsbEnvironment.get().findTestBuilder(fi.getFqSuperClassName()));
    }
    if (fi.getSuperClassFileInfo() != null) {
      collectAvailableBuilderMethods(fi.getSuperClassFileInfo(), ret);
    }
  }

  public static String getJavaDocForParam(MethodInfo mi, ParameterInfo pi)
  {
    if (mi == null || mi.getJavaDocInfo() == null) {
      return "";
    }
    return mi.getJavaDocInfo().getParamDoc(pi.getParamName());
  }

  private static String escapeHtml(String text, boolean html)
  {
    if (html == false) {
      return text;
    }
    return StringEscapeUtils.escapeHtml4(text);
  }

  private static String escapeJavaDoc(String text, boolean html)
  {
    if (text == null || html == false) {
      return text;
    }
    if (text.contains("<") && text.contains(">") && text.contains("/") == true) {
      return text;
    }
    return escapeHtml(text, true);
  }

  private static String nl(boolean html)
  {
    return html == true ? "<br/>\n" : "\n";
  }

  private static void appendJavaDocInfo(StringBuilder sb, JavaDocInfo jdoc, boolean html, String... excludedTags)
  {
    if (jdoc == null) {
      return;
    }
    if (StringUtils.isNotBlank(jdoc.getTitle()) == true) {
      sb.append(escapeJavaDoc(jdoc.getTitle(), html)).append(nl(html));
    }
    if (StringUtils.isNotBlank(jdoc.getDescription()) == true) {
      sb.append(escapeJavaDoc(jdoc.getDescription(), html)).append(nl(html));
    }
    if (excludedTags.length > 0 && "all".equals(excludedTags[0]) == true) {
      return;
    }
    boolean aParamWritten = false;
    if (jdoc.getTags() == null || jdoc.getTags().isEmpty() == true) {
      return;
    }
    if (html == false) {
      for (Map.Entry<String, List<Pair<String, String>>> me : jdoc.getTags().entrySet()) {
        if (ArrayUtils.contains(excludedTags, me.getKey()) == true) {
          continue;
        }

        for (Pair<String, String> p : me.getValue()) {
          sb.append(escapeJavaDoc(me.getKey(), html)).append(" ");
          if (StringUtils.isNotBlank(p.getKey()) == true) {
            sb.append(escapeJavaDoc(p.getKey(), html)).append(" ");
          }
          sb.append(escapeJavaDoc(p.getValue(), html)).append(nl(html));
        }

      }
    } else {
      for (Map.Entry<String, List<Pair<String, String>>> me : jdoc.getTags().entrySet()) {
        if (ArrayUtils.contains(excludedTags, me.getKey()) == true) {
          continue;
        }
        if (aParamWritten == false) {
          sb.append("<ul>");
          aParamWritten = true;
        }
        boolean multipleValues = me.getValue().size() > 1;
        String key = me.getKey();
        if (key.startsWith("@") == true) {
          key = key.substring(1);
        }
        sb.append("<li>").append(escapeJavaDoc(key, html));
        if (multipleValues) {
          sb.append("<ul>");
        }
        for (Pair<String, String> p : me.getValue()) {
          if (multipleValues) {
            sb.append("<li>");
          }
          if (StringUtils.isNotBlank(p.getKey()) == true) {
            sb.append(" ").append(escapeJavaDoc(p.getKey(), html));
          }
          sb.append(" ").append(escapeJavaDoc(p.getValue(), html));
          if (multipleValues) {
            sb.append("</li>");
          }
        }
        if (me.getValue().size() > 1) {
          sb.append("</ul>");
        }
        sb.append("</li>");
      }
      if (aParamWritten == true) {
        sb.append("</ul>\n");
      }
    }
  }

  /**
   * 
   * @param jdoc the {@link JavaDocInfo} contining the java doc
   * @param html the description of the java doc as html
   * @param excludedTags tagnames (with @) which should not included. if first excludedTags is "all" no tags are appended
   * @return the java doc description as a {@link String}
   */
  public static String getJavaDocDescription(JavaDocInfo jdoc, boolean html, String... excludedTags)
  {
    StringBuilder sb = new StringBuilder();
    appendJavaDocInfo(sb, jdoc, html, excludedTags);
    return sb.toString();
  }

  /**
   * 
   * @param mi the {@link AnnotatedBase}
   * @param html when true return the content as html
   * @param excludedTags tagnames (with @) which should not included. if first excludedTags is "all" no tags are appended
   * @return the java doc description
   */
  public static String getJavaDocDescription(AnnotatedBase mi, boolean html, String... excludedTags)
  {
    if (mi == null) {
      return "";
    }
    StringBuilder sb = new StringBuilder();
    appendJavaDocInfo(sb, mi.getJavaDocInfo(), html, excludedTags);
    return sb.toString();
  }

  public static String getEscapedJavaDocDescription(TestStepInfo ts)
  {
    if (ts == null) {
      return "";
    }
    StringBuilder sb = new StringBuilder();
    appendJavaDocInfo(sb, ts.getInlineJavaDocInfo(), true);
    appendJavaDocInfo(sb, ts.getTbJavaDocInfo(), true, "all");
    return sb.toString();
  }

  public static String getUnescapedCompleteJavaDocDescription(JavaDocInfo ji)
  {
    if (ji == null) {
      return "";
    }
    StringBuilder sb = new StringBuilder();
    if (StringUtils.isNotBlank(ji.getTitle()) == true) {
      sb.append(ji.getTitle()).append("\n\n");
    }
    if (StringUtils.isNotBlank(ji.getDescription()) == true) {
      sb.append(ji.getDescription()).append("\n\n");
    }
    if (ji.getTags() != null) {
      for (Map.Entry<String, List<Pair<String, String>>> me : ji.getTags().entrySet()) {
        for (Pair<String, String> p : me.getValue()) {
          sb.append(me.getKey()).append(" ").append(p.getKey()).append(" ").append(p.getValue()).append("\n");
        }
      }
    }
    return sb.toString();
  }

  public static AnnotationInfo getAnnotation(AnnotatedInfo mi, String name)
  {
    if (mi == null || mi.getAnnotations() == null) {
      return null;
    }
    for (AnnotationInfo ai : mi.getAnnotations()) {
      if (ai.getName().equals(name) == true) {
        return ai;
      }
    }
    return null;
  }

  public static void addTestAnnotation(FileInfo fi, MethodInfo mi)
  {
    fi.addImport("org.junit.Test");
    AnnotationInfo ai = new AnnotationInfo();
    ai.setName("Test");
    mi.addAnnotation(ai);
  }

  public static void addSimpleAnnotation(FileInfo fi, String fqAnnonationClass)
  {
    fi.addImport(fqAnnonationClass);

  }

  public static void scanSources(String source, String repo, List<String> includeRepos)
  {
    List<String> args = new ArrayList<String>();
    args.add("--ignore-local-settings");
    args.add("-ti");
    args.add(source);
    args.add("-op");
    args.add(repo);
    for (String rep : includeRepos) {
      args.add("-op");
      args.add(rep);
    }

    StaticTestDocGenerator.main(args.toArray(new String[0]));
  }
}
