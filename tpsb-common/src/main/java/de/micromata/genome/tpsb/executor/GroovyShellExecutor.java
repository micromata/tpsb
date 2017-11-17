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

import groovy.lang.GroovyClassLoader;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.manipulation.Filter;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;
import org.junit.runner.notification.StoppedByUserException;
import org.junit.runners.BlockJUnit4ClassRunner;

/**
 * A Main Java, which reads a groovy script from input, which contains a Junit class.
 * 
 * The class will be executed.
 * 
 * @author Roger Rene Kommer (r.kommer.extern@micromata.de)
 * 
 */
public class GroovyShellExecutor
{
  private static String decode(String text)
  {
    if (text.contains("\n") == false) {
      return text;
    }
    return "EOT<<\n" + text + "\n<<EOT";
  }

  private static void println(String text)
  {
    System.out.println(decode(text));
    System.out.flush();
  }

  private static void info(String text)
  {
    println("INFO: " + text);

  }

  private static void warn(String text)
  {
    println("WARN: " + text);
  }

  private boolean hasFailure = false;

  RunNotifier runNotifier = new RunNotifier()
  {

    @Override
    public void fireTestRunStarted(Description description)
    {
      info("Test start");
      super.fireTestRunStarted(description);
    }

    @Override
    public void fireTestRunFinished(Result result)
    {
      //info("Test run finished");
      super.fireTestRunFinished(result);
    }

    @Override
    public void fireTestStarted(Description description) throws StoppedByUserException
    {
      super.fireTestStarted(description);
    }

    @Override
    public void fireTestFailure(Failure failure)
    {
      hasFailure = true;
      String exmsgString = null;
      String stacktrace = null;
      StringBuilder sb = new StringBuilder();
      if (failure.getException() != null) {
        sb = new StringBuilder();
        //sb.append("TPSBSTACKTRACE:\n");
        Throwable ex = failure.getException();
        if (ex instanceof InvocationTargetException) {
          ex = ((InvocationTargetException) ex).getTargetException();
        }
        stacktrace = ExceptionUtils.getStackTrace(failure.getException());
        //sb.append(s);
        exmsgString = ex.getMessage();
      }
      sb = new StringBuilder();
      sb.append("Test failed");
      if (failure.getMessage() != null) {
        sb.append(": ").append(failure.getMessage());
      }
      if (exmsgString != null) {
        sb.append("; ").append(exmsgString);
      }
      if (stacktrace != null) {
        sb.append("\n").append(stacktrace);
      }
      println("TPSBFAIL: " + sb.toString());
    }

    @Override
    public void fireTestAssumptionFailed(Failure failure)
    {
      fireTestFailure(failure);
    }

    @Override
    public void fireTestFinished(Description description)
    {
      if (hasFailure == false) {
        println("TPSBSUCC: " + description.getDisplayName());
        super.fireTestFinished(description);
      }
      hasFailure = false;
    }

  };

  public void executeCode(String code, final String method) throws Exception
  {
    final GroovyClassLoader loader = new GroovyClassLoader();
    Class<?> cls = loader.parseClass(code);

    //ScriptTestAdapter stadapter = new ScriptTestAdapter(cls, new String[] {});
    //GroovyJUnit4ClassRunner runner = new GroovyJUnit4ClassRunner(cls, method);
    BlockJUnit4ClassRunner jclsrunner = new BlockJUnit4ClassRunner(cls);
    jclsrunner.filter(new Filter()
    {

      @Override
      public boolean shouldRun(Description description)
      {
        if (StringUtils.isBlank(method) == true) {
          return true;
        }
        if (description.getMethodName().equals(method) == true) {
          return true;
        }
        return false;
      }

      @Override
      public String describe()
      {
        return "run only given method";
      }

    });
    info("Start test");
    //runner.run(runNotifier);
    jclsrunner.run(runNotifier);
    if (hasFailure == false) {
      //stadapter.run(testResult);
      info("Test Finished");
    }
  }

  public static void main(String[] args)
  {
    String methodName = null;
    if (args.length > 0 && StringUtils.isNotBlank(args[0])) {
      methodName = args[0];
    }
    GroovyShellExecutor exec = new GroovyShellExecutor();
    //System.out.println("GroovyShellExecutor start");
    BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
    String line;
    StringBuffer code = new StringBuffer();
    try {
      while ((line = in.readLine()) != null) {
        if (line.equals("--EOF--") == true) {
          break;
        }
        code.append(line);
        code.append("\n");

        //System.out.flush();
      }
    } catch (IOException ex) {
      throw new RuntimeException("Failure reading std in: " + ex.toString(), ex);
    }
    try {
      exec.executeCode(code.toString(), methodName);
    } catch (Throwable ex) { // NOSONAR "Illegal Catch" framework
      warn("GroovyShellExecutor failed with exception: "
          + ex.getClass().getName()
          + ": "
          + ex.getMessage()
          + "\n"
          + ExceptionUtils.getStackTrace(ex));
    }
    System.out.println("--EOP--");
    System.out.flush();
  }

}
