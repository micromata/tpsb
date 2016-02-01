package de.micromata.genome.tpsb;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;

import org.codehaus.groovy.control.CompilationFailedException;
import org.junit.Test;

import de.micromata.genome.util.runtime.GroovyUtils;

public class JavaAssisttest
{
  public static class Foo
  {
    public String append(String other)
    {
      return other + "  xxxxx";
    }
  }

  @Test
  public void testGroovy()
  {
    Foo f = new Foo();
    f = patchWithGroovy(f);
    System.out.println("Called: " + f.append("asdf"));
  }

  private static <T> T patchWithGroovy(T bean)
  {
    String className = "ExW" + bean.getClass().getSimpleName();

    String mmcls = "public class MyMetaClass extends DelegatingMetaClass{  MyMetaClass(Class theClass){  super(theClass);  }\n"
        + "  Object invokeMethod(Object object, String methodName, Object[] arguments){\n"
        + "\"MyMetaClass: ${super.invokeMethod(object, methodName, arguments)}\"\n"
        + "}\n"
        + "}\n";
    String gc = mmcls
        + "class "
        + className
        + " extends "
        + bean.getClass().getName()
        + " implements GroovyInterceptable {  "
        + " String append(String other) { return other + 'yyy'; }\n" //
        + "  def invokeMethod(String name, Object args){\n" //
        + "System.out.println( \"before calling method ${name}\")\n" //
        + "invoked method $name(${args.join(', ')})\n" //
        + "}\n"
        + "}\n" //
        + className
        + ".metaClass.invokeMethod = { String name, args -> \n" //
        + "System.out.println( \"before calling method ${name}\")\n" //
        + "  def m = delegate.metaClass.getMetaMethod(name, *args)\n" //
        + "def result = (m ? m.invoke(delegate, *args) : delegate.metaClass.invokeMissingMethod(delegate, name, args))\n" //
        + "System.out.println( \"after calling method ${name}\")\n" //
        + "result\n" //
        + "}\n" //
        + "def retval= new "
        + className
        + "()\n" //
        + "def amc = new MyMetaClass("
        + className
        + ")\n"
        + "amc.initialize()\n"
        + "retval.metaClass = amc\n"
        + "return retval\n";
    Binding b = new Binding();
    b.setProperty("delegate", bean);
    GroovyShell sh = new GroovyShell(b);
    try {
      Object ret = GroovyUtils.evaluate(sh, gc, "wrapper.groovy");
      return (T) ret;
    } catch (CompilationFailedException e) {
      throw new RuntimeException("Invalid expression: " + gc + "; " + e.getMessage(), e);
    }
  }

}
