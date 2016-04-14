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

package de.micromata.genome.tpsb.executor;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.internal.AssumptionViolatedException;
import org.junit.internal.runners.model.EachTestNotifier;
import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;
import org.junit.runner.notification.StoppedByUserException;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;

/**
 * Runner for a junit class.
 * 
 * Only needed, because BlockJUnit4ClassRunner checks for multiple constructors :-/.
 * 
 * @author Roger Rene Kommer (r.kommer.extern@micromata.de)
 * 
 */
public class GroovyJUnit4ClassRunner extends Runner
{
  private String method;

  private Class<?> clazz;

  List<Method> tooRun = new ArrayList<Method>();

  public GroovyJUnit4ClassRunner(Class<?> klass, String method) throws InitializationError
  {
    this.clazz = klass;
    this.method = method;
    collectMethods();
  }

  private void collectMethods()
  {
    for (Method m : clazz.getMethods()) {
      if (m.getAnnotation(Test.class) == null) {
        continue;
      }
      if (method != null && m.getName().equals(method) == false) {
        continue;
      }
      tooRun.add(m);
    }
  }

  @Override
  public Description getDescription()
  {
    Description spec = Description.createSuiteDescription(clazz.getName(), clazz.getAnnotations());
    for (Method method : tooRun) {
      spec.addChild(methodDescription(method));
    }
    return spec;
  }

  @Override
  public void run(RunNotifier notifier)
  {
    EachTestNotifier testNotifier = new EachTestNotifier(notifier, getDescription());
    try {
      //InvokeMethod 
      Statement statement = classBlock(notifier);
      statement.evaluate();
    } catch (AssumptionViolatedException e) {
      testNotifier.fireTestIgnored();
    } catch (StoppedByUserException e) {
      throw e;
    } catch (Throwable e) { // NOSONAR "Illegal Catch" framework
      testNotifier.addFailure(e);
    }
  }

  protected Statement classBlock(final RunNotifier notifier)
  {
    System.out.println("Execute class block");
    Statement statement = new Statement()
    {
      @Override
      public void evaluate() throws Throwable
      {
        final Object bean = clazz.newInstance();
        for (Method m : tooRun) {
          System.out.println("Execute method: " + m.toString());
          m.invoke(bean);
        }
      }

    };
    //statement = withBeforeClasses(statement);
    //statement = withAfterClasses(statement);
    //statement = withClassRules(statement);
    return statement;
  }

  protected Description methodDescription(Method method)
  {
    return Description.createTestDescription(clazz.getName(), method.getName(), method.getAnnotations());
  }

}
