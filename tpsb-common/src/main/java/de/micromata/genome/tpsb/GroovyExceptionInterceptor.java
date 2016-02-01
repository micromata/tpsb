package de.micromata.genome.tpsb;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.codehaus.groovy.control.CompilationFailedException;

import de.micromata.genome.tpsb.annotations.TpsbIgnore;
import de.micromata.genome.util.runtime.GroovyUtils;

/**
 * Groovy interceptor for expected exception.
 * 
 * @deprecated use TpsbExpectExInterceptor
 * @author Roger Rene Kommer (r.kommer.extern@micromata.de)
 * 
 */
@Deprecated
public class GroovyExceptionInterceptor
{
  static Set<String> ignoreMethods = new HashSet<String>();
  static {
    ignoreMethods.add("setTestContextVar");
    ignoreMethods.add("getTestContextVar");
  }

  static String codeTemplate = "class ExceptionWrapperTestBuilder extends ${PARENTCLASS} {\r\n"
      + "  Object target;\r\n"
      + "  Class<? extends Throwable> exptectedEx;\r\n"
      + "  ExceptionWrapperTestBuilder(Object target, Class<? extends Throwable> exptectedEx) {\r\n"
      + "    this.target = target;\r\n"
      + "    this.exptectedEx = exptectedEx;\r\n"
      + "  }\r\n"
      + "  def wrappExpectedEx(code) {\r\n"
      + "    boolean thrown = false;\r\n"
      + "    try {\r\n"
      + "      code();\r\n"
      + "      thrown = true;\r\n"
      + "      throw new de.micromata.genome.tpsb.AssertionFailedException(\"Missed Excected exception: \" + exptectedEx.getName());\r\n"
      + "    } catch (Throwable ex) {\r\n"
      + "      if (thrown == true) {\r\n"
      + "        throw (de.micromata.genome.tpsb.AssertionFailedException)ex;\r\n"
      + "      }\r\n"
      + "      if (exptectedEx.isAssignableFrom(ex.getClass()) == true) {\r\n"
      + "        return getBuilder();\r\n"
      + "      }\r\n"
      + "      throw new de.micromata.genome.tpsb.AssertionFailedException(\"Wrong exception. expected: \" + exptectedEx.getName() + \"; got: \" + ex.getClass().getName() + \"; \" + ex.getMessage(), ex);\r\n"
      + "    }\r\n"
      + "  }\r\n"
      + " ${METHODBLOCK}"
      // + "  MyFoo foo(String arg) {\r\n"
      // + "    return wrappExpectedEx( { target.foo(arg) });\r\n"
      // + "  }\r\n"
      + "}\n\n"
      + "return new ExceptionWrapperTestBuilder(target, exptectedEx);";

  protected static String methodToString(Method m)
  {
    StringBuilder sb = new StringBuilder();
    String returnType = m.getReturnType().getName();
    // returnType = "def";
    sb.append(returnType).append(" ").append(m.getName()).append("(");
    int pos = 0;
    for (Class< ? > cls : m.getParameterTypes()) {
      if (pos > 0) {
        sb.append(", ");
      }
      sb.append(cls.getName()).append(" arg" + pos);
      ++pos;
    }
    sb.append(")");
    return sb.toString();
  }

  protected static void collectMethods(Class< ? > cls, Map<String, Method> collected)
  {
    for (Method m : cls.getDeclaredMethods()) {
      if ((m.getModifiers() & Modifier.PUBLIC) != Modifier.PUBLIC) {
        continue;
      }
      if ((m.getModifiers() & Modifier.STATIC) == Modifier.STATIC) {
        continue;
      }
      if (m.getAnnotation(TpsbIgnore.class) != null) {
        continue;
      }
      if (ignoreMethods.contains(m.getName()) == true) {
        continue;
      }
      if (m.getReturnType().isPrimitive() == true) {
        continue;
      }
      String sm = methodToString(m);
      if (collected.containsKey(sm) == true) {
        continue;
      }
      collected.put(sm, m);
    }
    Class< ? > scls = cls.getSuperclass();
    if (scls == Object.class) {
      return;
    }
    collectMethods(scls, collected);
  }

  protected static String renderMethodBlock(Map<String, Method> collectedMethods)
  {
    StringBuilder sb = new StringBuilder();
    for (String methn : collectedMethods.keySet()) {

      Method m = collectedMethods.get(methn);

      sb.append("  ").append(methn).append("{\n")//
          .append("    return wrappExpectedEx( { target.").append(m.getName()).append("(");
      for (int i = 0; i < m.getParameterTypes().length; ++i) {
        if (i > 0) {
          sb.append(", ");
        }
        sb.append("arg").append(i);
      }
      sb.append(");");
      sb.append("} );\n  }\n");
    }
    return sb.toString();
  }

  public static <T> T wrappTestBuilder(T builder, Class< ? extends Throwable> ex)
  {
    Map<String, Method> collectedMethods = new HashMap<String, Method>();
    collectMethods(builder.getClass(), collectedMethods);
    String methodBlock = renderMethodBlock(collectedMethods);
    String code = StringUtils.replace(codeTemplate, "${METHODBLOCK}", methodBlock);
    code = StringUtils.replace(code, "${PARENTCLASS}", builder.getClass().getName());
    Binding b = new Binding();
    b.setProperty("target", builder);
    b.setProperty("exptectedEx", ex);
    GroovyShell sh = new GroovyShell(b);
    try {
      Object ret = GroovyUtils.evaluate(sh, code, "wrapper.groovy");
      return (T) ret;
    } catch (CompilationFailedException e) {
      throw new RuntimeException("Invalid expression: " + code + "; " + e.getMessage(), e);
    }
  }
}
