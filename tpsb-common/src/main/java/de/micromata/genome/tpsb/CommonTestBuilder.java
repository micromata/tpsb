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

import de.micromata.genome.tpsb.annotations.TpsbBuilder;
import de.micromata.genome.tpsb.annotations.TpsbIgnore;
import de.micromata.genome.util.bean.PrivateBeanUtils;
import de.micromata.genome.util.runtime.AssertUtils;
import de.micromata.genome.util.runtime.CallableX;
import de.micromata.genome.util.runtime.GroovyUtils;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.codehaus.groovy.control.CompilationFailedException;

/**
 * Common testbuilder with testContext.
 * 
 * @author roger
 * 
 * @param <T>
 */
@TpsbBuilder
public class CommonTestBuilder<T extends CommonTestBuilder<?>>implements TestBuilder<T>
{
  private static final Logger LOG = Logger.getLogger(CommonTestBuilder.class);
  /**
   * Backup of all parameter of Testbuilder.
   */
  protected Map<String, Object> testContext = new HashMap<String, Object>();

  /**
   * The Comment listener.
   */
  protected TpsbCommentListener commentListener = new Log4JCommentListener();

  /**
   * A map of saved TestBuilders.
   * 
   * TODO RK THIS IS NOT THREADSAFE. Running test in multiple threads, will fail if static.
   */
  protected Map<String, CommonTestBuilder<?>> testContextMap = new HashMap<String, CommonTestBuilder<?>>();

  /**
   * In case an exception this interceptor is active
   */
  protected TpsbExpectExInterceptor<T> expectedExInterceptor = null;

  /**
   * Instantiates a new Common test builder.
   */
  public CommonTestBuilder()
  {

  }

  /**
   * Instantiates a new Common test builder.
   * 
   * @param other the other
   */
  public CommonTestBuilder(CommonTestBuilder<?> other)
  {
    this.testContext = other.testContext;
    PrivateBeanUtils.fetchAllNonStaticFields(other.testContext, other.getClass(), other);
    PrivateBeanUtils.populate(this, testContext);
  }

  /**
   * Copy vars to other builder.
   * 
   * @param target the target
   */
  @TpsbIgnore
  private void copyVarsToOtherBuilder(TestBuilder<?> target)
  {
    PrivateBeanUtils.fetchAllNonStaticFields(testContext, getClass(), this);
    target.getTestContext().putAll(this.testContext);
    populate(target, testContext);

  }

  private static void populate(Object bean, Map<String, Object> properties)
  {
    for (Map.Entry<String, Object> me : properties.entrySet()) {
      Field f = PrivateBeanUtils.findField(bean, me.getKey());
      if (f == null) {
        continue;
      }
      try {
        PrivateBeanUtils.writeField(bean, f, me.getValue());
      } catch (Exception ex) {
        LOG.warn("Cannot copy var: " + f.getName() + " to " + bean.getClass().getName() + ": " + ex.getMessage(), ex);
      }
    }
  }

  /**
   * Create a new TestBuilder of given Class.
   * 
   * After creating the new TestBuilder, it copies the existant testcontext to the new TestBuilder
   * 
   * Note: the class must not be generic.
   * 
   * @param clazz Class of the new created builder.
   */
  @Override
  public <X> X createBuilder(Class<X> clazz)
  {
    try {
      // TpsbTestBase.watcher.wrap()
      X newBuilder = clazz.newInstance();
      if (newBuilder instanceof TestBuilder) {
        copyVarsToOtherBuilder((TestBuilder<?>) newBuilder);
        if (newBuilder instanceof CommonTestBuilder) {
          CommonTestBuilder<?> ctm = (CommonTestBuilder<?>) newBuilder;
          ctm.expectedExInterceptor = null;
        }
      }
      return newBuilder;
    } catch (Exception ex) {
      throw new RuntimeException("Cannot create builder class: " + clazz + "; " + ex.getMessage(), ex);
    }
  }

  /**
   * Gets builder.
   * 
   * @return the builder
   */
  @SuppressWarnings("unchecked")
  @TpsbIgnore
  final protected T getBuilder()
  {
    return (T) this;
  }

  /**
   * Sets a variable inside the testcontext.
   * 
   * if in the current builder a Field with same name and assignable type, it will be set too.
   * 
   * @param varName the variable name of context variable.
   * @param value value
   * @return builder
   */
  @Override
  public T setTestContextVar(String varName, Object value)
  {
    testContext.put(varName, value);
    Field f = PrivateBeanUtils.findField(getClass(), varName);
    if (f != null) {
      PrivateBeanUtils.writeField(this, f, value);
    }
    return getBuilder();
  }

  /**
   * Setzt den ausgewerteten Wert der expression als Variable im TestKontext
   * 
   * @param varName Name of the variable
   * @param expression A Groovy expression.
   * @return context var expression
   */
  public T setContextVarExpression(String varName, String expression)
  {
    copyThisToTestContext();
    Object ret = evaluate(testContext, expression, this);
    setTestContextVar(varName, ret);
    return getBuilder();
  }

  /**
   * Check if given context variable is equal to expectedValue
   * 
   * @param varName Name of the variable
   * @param expectedValue expected value
   * @return t
   */
  public T validateTestContextVar(String varName, Object expectedValue)
  {
    copyThisToTestContext();
    Object realVal = testContext.get(varName);
    if (ObjectUtils.equals(realVal, expectedValue) == false) {
      failImpl("Variable " + varName + " is not excected value: " + expectedValue + " but " + realVal);
    }
    return getBuilder();

  }

  /**
   * Store the current testbuilder under given name
   * 
   * @param name variable name
   * @return builder t
   */
  public T storeTestBuilder(String name)
  {
    testContextMap.put(name, this);
    return getBuilder();
  }

  /**
   * return to the named previously stored TestBuilder and remove the testbuilder from the storage.
   * 
   * @param name of the builder in the test context.
   * @param exprectedType type of the test builder.
   * @return builder x
   */
  public <X extends CommonTestBuilder<?>> X returnToStoredTestBuilder(String name, Class<X> exprectedType)
  {
    X ret = switchToStoredTestBuilder(name, exprectedType);
    testContext.remove(name);
    return ret;
  }

  /**
   * Stores the current testbuilder under the fully qualified class name.
   * 
   * @return builder
   */
  public T storeCurrentTestBuilder()
  {
    return storeTestBuilder(this.getClass().getName());
  }

  /**
   * Restore previous storeCurrentTestBuilder on given class.
   * 
   * @param <X> the generic type
   * @param testBuilderClass the test builder class
   * @return the x
   */
  public <X extends CommonTestBuilder<?>> X returnToPreviousTestBuilder(Class<X> testBuilderClass)
  {
    return returnToStoredTestBuilder(testBuilderClass.getName(), testBuilderClass);
  }

  /**
   * switch to previously stored Testbuilder, but does not delete it from storage.
   * 
   * @param name the name
   * @param exprectedType the exprected type
   * @return x
   */
  public <X extends CommonTestBuilder<?>> X switchToStoredTestBuilder(String name, Class<X> exprectedType)
  {
    CommonTestBuilder<?> ret = testContextMap.get(name);
    if (ret == null) {
      fail("There is no stored TestBuilder with name: " + name);
    }
    return (X) ret;
  }

  @Override
  @TpsbIgnore
  public Object getTestContextVar(String varName)
  {
    return testContext.get(varName);
  }

  @Override
  @TpsbIgnore
  public Map<String, Object> getTestContext()
  {
    copyThisToTestContext();
    return testContext;
  }

  /**
   * Clear the context state of the bilder.
   * 
   * @return t
   */
  public T clearTestContext()
  {
    testContext.clear();
    return getBuilder();
  }

  /**
   * Copy test context to.
   * 
   * @param target the target
   */
  @TpsbIgnore
  protected void copyTestContextTo(CommonTestBuilder<?> target)
  {
    target.testContext.putAll(testContext);
    PrivateBeanUtils.populate(target, target.testContext);
  }

  /**
   * Copy this to test context.
   */
  @TpsbIgnore
  protected void copyThisToTestContext()
  {
    testContext.putAll(PrivateBeanUtils.getAllFields(this, 0, Modifier.STATIC | Modifier.TRANSIENT));
    testContext.put("builder", this);
  }

  /**
   * Add a comment
   * 
   * @param text the text
   * @return t
   */
  public T addComment(String text)
  {
    commentListener.addCommentText(this, text);
    return getBuilder();
  }

  /**
   * Add a comment expression
   * 
   * @param expression a groovy expression.
   * @return t
   */
  public T addCommentExpression(String expression)
  {
    commentListener.addCommentExpression(this, expression);
    return getBuilder();
  }

  /**
   * validate the boolean expression. If not true, fail with message
   * 
   * @param expression boolean
   * @param message Message to fail
   * @return t
   */
  public T validateTrue(boolean expression, String message)
  {
    if (expression == true) {
      return getBuilder();
    }
    return failImpl("validateTrue failed: " + message);
  }

  /**
   * validate the boolean expression. If not false, fail with message.
   * 
   * @param expression boolean
   * @param message Message to fail
   * @return
   */
  public T validateFalse(boolean expression, String message)
  {
    if (expression == false) {
      return getBuilder();
    }

    return failImpl("validateFalse failed: " + message);
  }

  /**
   * Validates that both values are equal.
   * 
   * @param expected
   * @param given
   * @return
   */
  public T validateEquals(Object expected, Object given)
  {
    if (ObjectUtils.equals(given, expected) == true) {
      return getBuilder();
    }
    return failImpl("Expect: " + expected + "; given: " + given);
  }

  /**
   * Validates that both values are not equal.
   * 
   * @param expected
   * @param given
   * @return
   */
  public T validateNotEquals(Object expected, Object given)
  {
    if (ObjectUtils.equals(given, expected) == false) {
      return getBuilder();
    }
    return failImpl("Expect: " + expected + "; given: " + given);
  }

  @TpsbIgnore
  public T failMissingException(final Class<? extends Throwable> expected)
  {
    throw new AssertionMissingException(expected, this);
  }

  /**
   * Run next statement and validate if expected exception is thrown.
   * 
   * The next statement must return the this in the method.
   * 
   * The catched expected exception will be stored in the testContext with the name "expectedException"
   * 
   * @param expectEx expected exception.
   * @param callable callback
   * @return t
   */
  @TpsbIgnore
  public <EX extends Throwable> T runWithExpectedException(final Class<EX> expectEx, CallableX<T, EX> callable)
  {
    try {
      callable.call();
      fail("Missing expected exception: " + expectEx.getCanonicalName());
    } catch (Throwable ex) { // NOSONAR "Illegal Catch" framework
      if (expectEx.isAssignableFrom(ex.getClass()) == false) {
        fail("expected exception: " + expectEx.getCanonicalName() + " but got: " + ex.getClass().getCanonicalName()
            + "; " + ex.getMessage());
      }
    }
    return getBuilder();
  }

  /**
   * Calling next test step expect to throw an exception
   * 
   * @param exceptionClass the exception class
   * @return t
   */
  @SuppressWarnings("unchecked")
  public T validateNextException(Class<? extends Throwable> exceptionClass)
  {
    if (expectedExInterceptor == null) {
      expectedExInterceptor = new TpsbExpectExInterceptor(getBuilder(), exceptionClass);
    } else {
      expectedExInterceptor.setExpectedException(exceptionClass);
      expectedExInterceptor.setActive(true);
    }
    return expectedExInterceptor.getProxy();
  }

  /**
   * validate groovy expression.
   * 
   * All nonstatics will be copied into testContext
   * 
   * @param expression the expression
   * @return the t
   */
  public T validateExpression(String expression)
  {
    copyThisToTestContext();
    Object ret = evaluate(testContext, expression, this);
    if (ret == null) {
      return failImpl("Expression returned null: " + expression);
    }
    if (ret instanceof Boolean && ((Boolean) ret).booleanValue() == false) {
      return failImpl("Expression returned false: " + expression);
    }
    return getBuilder();
  }

  /**
   * execute a groovy expression.
   * 
   * All nonstatics will be copied into testContext.
   * 
   * The result will be stored int he testContet as tpsbExpressionResult.
   * 
   * @param expression the expression
   * @return t
   */
  public T executeExpression(String expression)
  {
    copyThisToTestContext();
    Object ret = evaluate(testContext, expression, this);
    testContext.put("tpsbExpressionResult", ret);
    return getBuilder();
  }

  /**
   * evaluate the given expression with the test context.
   * 
   * @param expression the expression
   * @return object
   */
  @TpsbIgnore
  public Object eval(String expression)
  {
    copyThisToTestContext();
    return evaluate(testContext, expression, getClass());
  }

  /**
   * Evaluate object.
   * 
   * @param context the context
   * @param expression the expression
   * @return the object
   */
  @TpsbIgnore
  public static Object evaluate(Map<String, Object> context, String expression)
  {
    return evaluate(context, expression, null);

  }

  /**
   * Evaluate object.
   * 
   * @param context the context
   * @param expression the expression
   * @param deriveFrom the derive from
   * @return the object
   */
  @TpsbIgnore
  public static Object evaluate(Map<String, Object> context, String expression, Object deriveFrom)
  {
    Binding b = new Binding();
    for (Map.Entry<String, Object> me : context.entrySet()) {
      b.setVariable(me.getKey(), me.getValue());
    }
    String text = expression;

    GroovyShell sh = new GroovyShell(b);
    try {
      Object ret = GroovyUtils.evaluate(sh, text, "testbuilderexpr.groovy");
      return ret;
    } catch (CompilationFailedException e) {
      throw new RuntimeException("Invalid expression: " + expression + "; " + e.getMessage(), e);
    }

  }

  /**
   * Validate expression.
   * 
   * @param bean the bean
   * @param expression the expression
   * @return the t
   */
  public T validateExpression(final Object bean, final String expression)
  {
    testContext.put("bean", bean);
    return validateExpression(expression);
  }

  /**
   * Just fail the test
   * 
   * @param message to printed as fail
   * @return t
   */
  public T fail(String message)
  {
    return failImpl("fail: " + message);
  }

  /**
   * Just fail the test with given exception.
   * 
   * @param ex the ex
   * @return t
   */
  public T fail(AssertionFailedException ex)
  {
    return failImpl(ex);
  }

  /**
   * Fail impl.
   * 
   * @param message the message
   * @return the t
   */
  protected T failImpl(String message)
  {
    return failImpl(new AssertionFailedException(message));
  }

  protected T failImpl(AssertionFailedException ex)
  {
    ex.setTestBuilder(this);
    StackTraceElement el = AssertUtils.getStackAbove(this.getClass());
    if (el != null) {
      String line = StringUtils.trim(AssertUtils.getCodeLine(el));
      ex.setClassName(el.getClassName());
      ex.setMethodName(el.getMethodName());
      ex.setLineNo(el.getLineNumber());
      ex.setCodeLine(line);
    }
    throw ex;
  }

  protected T failImpl(Exception ex)
  {
    AssertionFailedException nex = new AssertionFailedException(ex);
    nex.setTestBuilder(this);
    StackTraceElement el = AssertUtils.getStackAbove(this.getClass());
    if (el != null) {
      String line = StringUtils.trim(AssertUtils.getCodeLine(el));
      nex.setClassName(el.getClassName());
      nex.setMethodName(el.getMethodName());
      nex.setLineNo(el.getLineNumber());
      nex.setCodeLine(line);
    }
    throw nex;
  }

  /**
   * Dump Builder state information
   * 
   * @return string
   */
  @TpsbIgnore
  public String dumpState()
  {
    final StringBuilder sb = new StringBuilder();
    dumpState(sb);
    return sb.toString();
  }

  /**
   * Dump builder state to System.out.
   * 
   * @return builder
   */
  public T dumpStateToSystemOut()
  {
    System.out.println(dumpState());
    return getBuilder();
  }

  /**
   * Dump state.
   * 
   * @param sb the sb
   */
  @TpsbIgnore
  public void dumpState(final StringBuilder sb)
  {
    sb.append("TestContexte:\n");
    for (Map.Entry<String, Object> me : testContext.entrySet()) {
      sb.append("  ").append(me.getKey()).append(": ").append(me.getValue() == null
          ? StringUtils.EMPTY : me.getValue().toString()).append("\n");
    }
  }

  /**
   * Pause the test
   * 
   * @param millis Milliseconds to sleep
   * @return builder t
   */
  public T sleep(long millis)
  {
    try {
      Thread.sleep(millis);
      return getBuilder();
    } catch (InterruptedException ex) {
      throw new RuntimeException(ex);
    }
  }

  /**
   * Gets comment listener.
   * 
   * @return the comment listener
   */
  @TpsbIgnore
  public TpsbCommentListener getCommentListener()
  {
    return commentListener;
  }

  /**
   * Sets comment listener.
   * 
   * @param commentListener the comment listener
   */
  @TpsbIgnore
  public void setCommentListener(TpsbCommentListener commentListener)
  {
    this.commentListener = commentListener;
  }

}
