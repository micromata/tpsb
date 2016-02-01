package de.micromata.genome.tpsb;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.framework.ProxyFactory;

/**
 * Intercepts a TestBuilder expect an exception. See
 *
 * @see CommonTestBuilder.runWithExpectedException
 * @author Roger Rene Kommer (r.kommer.extern@micromata.de)
 *
 */
public class TpsbExpectExInterceptor<BUILDER extends TestBuilder<?>>implements MethodInterceptor
{
  /**
   * Key for a variable stored in test context of a TestBuilder that holds an expected exception.
   */
  public static final String CTX_LAST_EXCEPTION = "lastException";

  private Class<? extends Throwable> expectedException;

  private TestBuilder<?> target;

  private ProxyFactory pf;

  private boolean active = true;

  private BUILDER proxy;

  private Throwable lastException;

  public TpsbExpectExInterceptor(final BUILDER instance, Class<? extends Throwable> expectedException)
  {
    pf = new ProxyFactory(instance);
    pf.setProxyTargetClass(true);
    pf.addAdvice(this);
    target = instance;
    this.expectedException = expectedException;

  }

  public BUILDER getProxy()
  {
    if (proxy == null) {
      proxy = (BUILDER) pf.getProxy();
    }
    return proxy;
  }

  @Override
  public Object invoke(MethodInvocation mi) throws Throwable
  {
    if (active == false) {
      return mi.proceed();
    }
    active = false;
    Object result = null;
    boolean inFailure = false;
    try {
      // Object c = mi.getThis();
      // System.err.println(c);
      result = mi.proceed();
      inFailure = true;
      throw new AssertionFailedException("Missed Expected exception: " + expectedException.getName());

    } catch (Throwable ex) { // NOSONAR "Illegal Catch" framework
      if (inFailure == true) {
        throw ex;
      }
      lastException = ex;
      if (expectedException.isAssignableFrom(ex.getClass()) == false) {
        throw new AssertionFailedException("Wrong exception. expected: "
            + expectedException.getName()
            + "; got: "
            + ex.getClass().getName()
            + "; "
            + ex.getMessage(), ex);
      }
      target.setTestContextVar(CTX_LAST_EXCEPTION, ex);
      return target;
    } finally {

    }
  }

  public Class<? extends Throwable> getExpectedException()
  {
    return expectedException;
  }

  public void setExpectedException(Class<? extends Throwable> expectedException)
  {
    this.expectedException = expectedException;
  }

  public Throwable getLastException()
  {
    return lastException;
  }

  public void setLastException(Throwable lastException)
  {
    this.lastException = lastException;
  }

  public boolean isActive()
  {
    return active;
  }

  public void setActive(boolean active)
  {
    this.active = active;
  }
}
