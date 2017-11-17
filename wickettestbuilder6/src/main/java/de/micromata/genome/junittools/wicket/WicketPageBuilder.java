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

package de.micromata.genome.junittools.wicket;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.wicket.Component;
import org.apache.wicket.Page;
import org.apache.wicket.util.tester.WicketTester;

import de.micromata.genome.tpsb.AssertionFailedException;
import de.micromata.genome.tpsb.annotations.TpsbBuilder;
import de.micromata.genome.util.matcher.Matcher;
import de.micromata.genome.util.runtime.CallableX;

/**
 * Represents page.
 * 
 * All derives should annotate the class with TpsbWicketPage.
 * 
 * @author Roger Rene Kommer (r.kommer.extern@micromata.de)
 * 
 * @param <T>
 */
@TpsbBuilder
public class WicketPageBuilder<T extends WicketPageBuilder<?>>extends WicketTestBuilder<T>
{
  private static final Logger log = Logger.getLogger(WicketPageBuilder.class);

  private Class<? extends Throwable> expectedExceptionClass;

  /**
   * Instantiates a new wicket page builder.
   * 
   * @param wicketTester the wicket tester
   */
  public WicketPageBuilder(WicketTester wicketTester)
  {
    super(wicketTester);
  }

  public Class<? extends Page> _getWicketPageClass()
  {
    return _getPageClassFromBuilder((Class<? extends WicketPageBuilder<?>>) getClass());
  }

  protected <X extends Page> X _getCurrentPage(Class<X> expected)
  {
    return (X) _getWicketTester().getLastRenderedPage();
  }

  protected static Class<? extends Page> _getPageClassFromBuilder(Class<? extends WicketPageBuilder<?>> fromBuilder)
  {
    TpsbWicketPage ano = fromBuilder.getAnnotation(TpsbWicketPage.class);
    return ano.value();
  }

  /**
   * Do execute ajax event.
   * 
   * @param <X> the generic type
   * @param component the component
   * @param event the event
   * @param expected the expected
   * @return the x
   */
  public <X extends WicketPageBuilder<?>> X _doExecuteAjaxEvent(final Component component, final String event,
      final Class<X> expected)
  {
    return wrappExpected(new CallableX<X, RuntimeException>()
    {
      @Override
      public X call() throws RuntimeException
      {
        Class<? extends Page> pageClass = _getPageClassFromBuilder(expected);
        return WicketPageBuilder.super._doExecuteAjaxEvent(component, event)
            ._validateRenderedPage(pageClass).createBuilder(expected);
      }
    });

  }

  public <X extends WicketPageBuilder<?>> X _doClickLink(final Component component, final Class<X> expected)
  {
    return wrappExpected(new CallableX<X, RuntimeException>()
    {
      @Override
      public X call() throws RuntimeException
      {
        Class<? extends Page> pageClass = _getPageClassFromBuilder(expected);
        return WicketPageBuilder.super.doClickLink(component, false)._validateRenderedPage(pageClass)
            .createBuilder(expected);
      }
    });
  }

  public <X extends WicketPageBuilder<?>> X _doClickAjaxLink(final Component component, final Class<X> expected)
  {
    return wrappExpected(new CallableX<X, RuntimeException>()
    {
      @Override
      public X call() throws RuntimeException
      {
        Class<? extends Page> pageClass = _getPageClassFromBuilder(expected);
        return WicketPageBuilder.super.doClickLink(component, true)._validateRenderedPage(pageClass)
            .createBuilder(expected);
      }
    });

  }

  @Override
  protected T failImpl(AssertionFailedException ex)
  {
    String lastResponse = _getWicketTester().getLastResponseAsString();
    String lastPageResponse = _getLastDocResponseAsString();

    System.out.println("Last Response:\n" + lastResponse + "\n\n");
    if (StringUtils.equals(lastPageResponse, lastResponse) == false) {
      System.out.println("Last Page Response:\n" + lastPageResponse + "\n\n");
    }
    List<String> messages = TpsbWicketTools.collectFeedbackMessages(this, null);
    if (messages.isEmpty() == false) {
      StringBuilder sb = new StringBuilder();
      sb.append("\n\nMessages:\n");
      for (String msg : messages) {
        sb.append(msg).append("\n");
      }
      sb.append("\n");
      System.out.println(sb.toString());
    }
    return super.failImpl(ex);
  }

  protected <X extends WicketPageBuilder<?>> X wrappExpected(CallableX<X, RuntimeException> callable)
  {
    if (expectedExceptionClass == null) {
      return callable.call();
    }
    try {
      callable.call();
      Class<? extends Throwable> excl = expectedExceptionClass;
      expectedExceptionClass = null;
      throw new AssertionFailedException("Expected Exception " + excl + " but none thrown");
    } catch (Throwable ex) { // NOSONAR "Illegal Catch" framework
      if (expectedExceptionClass.isAssignableFrom(ex.getClass()) == true) {
        expectedExceptionClass = null;
        return (X) getBuilder();

      } else {
        ex.printStackTrace();
        Class<? extends Throwable> excl = expectedExceptionClass;
        expectedExceptionClass = null;
        throw new AssertionFailedException("Expected Exception " + excl + " but got " + ex.getClass());
      }
    }
  }

  /**
   * Checks if the matching component can be found in given count.
   * 
   * @param count expected count of components.
   * @param matcher matcher. Use TpsbWicketMatchers to builder a matcher expression.
   * @return builder
   */
  public T validatePageContainsComponent(int count, Matcher<Component> matcher)
  {
    int found = TpsbWicketTools.findComponentsFor(this, null, null, matcher).size();
    if (found != count) {
      fail("Found components: " + found + "; expected: " + count + " for matcher: " + matcher);
    }
    return getBuilder();
  }

  /**
   * Note. super implementation doesn't work, because casting problems. {@inheritDoc}
   * 
   */
  @Override
  public T validateNextException(Class<? extends Throwable> exceptionClass)
  {
    this.expectedExceptionClass = exceptionClass;
    return getBuilder();
  }
}
