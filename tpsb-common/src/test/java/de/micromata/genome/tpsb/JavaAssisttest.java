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
