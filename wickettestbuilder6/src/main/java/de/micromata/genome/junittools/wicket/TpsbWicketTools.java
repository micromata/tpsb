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

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.Page;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.AbstractPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.tester.WicketTester;
import org.apache.wicket.util.visit.IVisit;
import org.apache.wicket.util.visit.IVisitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.micromata.genome.util.matcher.Matcher;

/**
 * Tools to navigate through wicket dom.
 * 
 * @author Johannes Unterstein (j.unterstein@micromata.de)
 * @author Roger Rene Kommer (r.kommer.extern@micromata.de)
 * 
 */
public class TpsbWicketTools
{

  /**
   * The Constant LOG.
   */
  private static final Logger LOG = LoggerFactory.getLogger(WicketTestBuilder.class);

  public static <T extends WicketTestBuilder< ? >> T validateLabelContainingText(T builder, MarkupContainer startComponent, String text)
  {
    List<String> labels = collectLabelTexte(builder, startComponent);
    if (labels.contains(text) == false) {
      builder.fail("Cannot find label with text: " + text);
    }
    return builder;
  }

  /**
   * Collect strings in FeedbackPanels.
   * 
   * @param wicketTestBuilder the wicket test builder
   * @param startComponent the start component
   * @return the list
   */
  public static List<String> collectFeedbackMessages(WicketTestBuilder< ? > wicketTestBuilder,
      MarkupContainer startComponent)
  {
    List<FeedbackPanel> fbpanels = findComponentsFor(wicketTestBuilder, startComponent, FeedbackPanel.class,
        TpsbWicketMatchers.all());
    List<String> ret = new ArrayList<String>();
    for (FeedbackPanel fpanel : fbpanels) {
      List<Label> labels = findComponentsFor(wicketTestBuilder, fpanel, Label.class,
          TpsbWicketMatchers.all());
      for (Label label : labels) {
        String text = getLabelText(label);
        if (text == null) {
          continue;
        }
        ret.add(text);
      }
    }
    return ret;
  }

  /**
   * Collect the label texts for given page.
   * 
   * @param wicketTestBuilder the wicket test builder
   * @param startComponent the start component
   * @return the list
   */
  public static List<String> collectLabelTexte(WicketTestBuilder< ? > wicketTestBuilder,
      MarkupContainer startComponent)
  {
    List<String> ret = new ArrayList<String>();
    List<Label> labels = findComponentsFor(wicketTestBuilder, startComponent, Label.class,
        TpsbWicketMatchers.all());
    for (Label label : labels) {
      String text = getLabelText(label);
      if (text == null) {
        continue;
      }
      ret.add(text);
    }
    return ret;
  }

  /**
   * Gets the label text.
   * 
   * @param label the label
   * @return the label text
   */
  public static String getLabelText(Label label)
  {
    try {
      IModel< ? > model = label.getDefaultModel();
      if (model == null) {
        return null;
      }
      Object obj = model.getObject();
      if ((obj instanceof String) == false) {
        return null;
      }
      return (String) obj;
    } catch (RuntimeException ex) {
      return null;
    }
  }

  /**
   * Find components for given matcher.
   * 
   * @param <X> the generic return type
   * @param wicketTestBuilder the wicket test builder
   * @param startComponent the start component. if null searches on page.
   * @param componentClass the component class if null, return Component.class
   * @param pathMatcher the path matcher. Can be created via TpsbWicketMatchers and derived.
   * @return the list
   */
  public static <X extends Component> List<X> findComponentsFor(WicketTestBuilder< ? > wicketTestBuilder,
      MarkupContainer startComponent,
      final Class<X> componentClass,
      final Matcher<Component> pathMatcher)
  {
    return internalFinder(wicketTestBuilder._getWicketTester(), startComponent, componentClass,
        pathMatcher);
  }

  /**
   * Find single components for.
   * 
   * @param <X> the generic return type
   * @param wicketTestBuilder the wicket test builder
   * @param startComponent the start component. if null searches on page.
   * @param componentClass the component class if null, return Component.class
   * @param pathMatcher the path matcher. Can be created via TpsbWicketMatchers and derived.
   * @return the component
   * @throws TpsbWicketComponentNotFoundException component not found
   * @throws TpsbWicketComponentNotUniqueException more than 1 component found
   */
  public static <X extends Component> X findSingleComponentsFor(WicketTestBuilder< ? > wicketTestBuilder,
      MarkupContainer startComponent,
      final Class<X> componentClass,
      final Matcher<Component> pathMatcher)
      throws TpsbWicketComponentNotFoundException, TpsbWicketComponentNotUniqueException
  {
    List<X> result = findComponentsFor(wicketTestBuilder, startComponent, componentClass, pathMatcher);
    if (result.size() == 0) {
      wicketTestBuilder.fail(new TpsbWicketComponentNotFoundException("Component not found.", wicketTestBuilder, pathMatcher.toString()));
    } else if (result.size() > 1) {
      wicketTestBuilder.fail(new TpsbWicketComponentNotUniqueException("Component not unique.", wicketTestBuilder, pathMatcher.toString()));
    }
    return result.get(0);
  }

  //  @Deprecated
  //  public static <X extends Component> X findSingleComponentForWicketId(WicketTestBuilder< ? > wicketTestBuilder,
  //      final Class<X> componentClass, final String wicketId)
  //  {
  //    List<X> result = findComponentsForWicketId(wicketTestBuilder, componentClass, wicketId);
  //    if (result.size() != 1) {
  //      wicketTestBuilder.fail("There must be only one result, found: " + result.size());
  //    }
  //    return result.get(0);
  //  }

  /**
   * Find components for wicket id.
   * 
   * @param <X> the generic type
   * @param wicketTestBuilder the wicket test builder
   * @param componentClass the component class
   * @param wicketId the wicket id
   * @return the list
   */
  @Deprecated
  public static <X extends Component> List<X> _findComponentsForWicketId(WicketTestBuilder< ? > wicketTestBuilder,
      final Class<X> componentClass,
      final String wicketId)
  {
    return internalFinder(wicketTestBuilder._getWicketTester(), null, componentClass,
        TpsbWicketMatchers.exactWicketId(wicketId));
  }

  //
  //  @Deprecated
  //  public static <X extends Component> X findSingleComponentForSuffixInPath(WicketTestBuilder< ? > wicketTestBuilder,
  //      final Class<X> componentClass, final String wicketId)
  //  {
  //    List<X> result = findComponentsForSuffixInPath(wicketTestBuilder, componentClass, wicketId);
  //    if (result.size() != 1) {
  //      wicketTestBuilder.fail("There must be only one result, found: " + result.size());
  //    }
  //    return result.get(0);
  //  }

  /**
   * Find components for suffix in path.
   * 
   * @param <X> the generic type
   * @param wicketTestBuilder the wicket test builder
   * @param componentClass the component class
   * @param path the path
   * @return the list
   */
  @Deprecated
  public static <X extends Component> List<X> _findComponentsForSuffixInPath(WicketTestBuilder< ? > wicketTestBuilder,
      final Class<X> componentClass,
      final String path)
  {
    return findComponentsFor(wicketTestBuilder, null, componentClass,
        TpsbWicketMatchers.endsWithWicketPath(path));
  }

  /**
   * Normalize wicket id.
   * 
   * @param wicketId the wicket id
   * @return the string
   */
  public static String normalizeWicketId(String wicketId)
  {
    return StringUtils.replace(wicketId, ":", "_");
  }

  /**
   * Internal finder.
   * 
   * @param <X> the generic type
   * @param tester the tester
   * @param startComponent the start component
   * @param componentClass the component class
   * @param path the path
   * @param exactMatch the exact match
   * @return the list
   */
  @Deprecated
  public static <X extends Component> List<X> _internalFinder(WicketTester tester, MarkupContainer startComponent,
      Class<X> componentClass,
      String path, boolean exactMatch)
  {
    Matcher<Component> matcher;
    if (exactMatch == true) {
      matcher = TpsbWicketMatchers.exactWicketId(path);
    } else {
      matcher = TpsbWicketMatchers.endsWithWicketPath(path);
    }
    return internalFinder(tester, startComponent, componentClass, matcher);
  }

  /**
   * Internal finder.
   * 
   * @param <X> the generic type
   * @param tester the tester
   * @param startComponent the start component
   * @param componentClass the component class
   * @param matcher the matcher
   * @return the list
   */
  private static <X extends Component> List<X> internalFinder(WicketTester tester, MarkupContainer startComponent,
      Class<X> componentClass,
      final Matcher<Component> matcher)
  {
    final List<X> results = new LinkedList<X>();
    Page lastRenderedPage = tester.getLastRenderedPage();

    if (lastRenderedPage == null) {
      throw new IllegalStateException("You must call #goToPage before you can find components.");
    }

    if (componentClass == null) {
      componentClass = (Class<X>) Component.class;
    }
    final Class< ? extends Component> searchComponent = componentClass;

    IVisitor<Component, Object> visitor = new IVisitor<Component, Object>() {
      @Override
      public void component(Component object, IVisit<Object> visit)
      {
        if (LOG.isDebugEnabled() == true) {
          LOG.debug("candite for wicket internalFinder: "
              + object.getClass().getSimpleName()
              + "|"
              + object.getId()
              + "|"
              + object.getPath());
        }
        if (searchComponent.isAssignableFrom(object.getClass()) == true) {

          if (matcher.match(object) == true) {
            if (matcher instanceof TpsbWicketMatchSelector) {
              results.add((X) ((TpsbWicketMatchSelector) matcher).selectMatched(object));
            } else {
              results.add((X) object);
            }
          }
        }
      }
    };
    if (startComponent == null) {
      lastRenderedPage.visitChildren(visitor);
    } else {
      startComponent.visitChildren(visitor);
    }
    return results;
  }

  /**
   * Find components for property model property.
   * 
   * @param <X> the generic type
   * @param tester the tester
   * @param startComponent the start component
   * @param componentClass the component class
   * @param property the property
   * @return the list
   * @deprecated use TpsbWicketMatchSelector
   */
  @Deprecated
  public static <X extends Component> List<X> _findComponentsForPropertyModelProperty(WicketTester tester,
      final MarkupContainer startComponent,
      final Class<X> componentClass, final String property)
  {
    final List<X> results = new LinkedList<X>();
    Page lastRenderedPage = tester.getLastRenderedPage();

    if (lastRenderedPage == null) {
      throw new IllegalStateException("You must call #goToPage before you can find components.");
    }

    if (componentClass == null || property == null) {
      throw new IllegalArgumentException("You must provide not null arguments.");
    }

    IVisitor<Component, Object> visitor = new IVisitor<Component, Object>() {
      @Override
      public void component(Component object, IVisit<Object> visit)
      {
        if (LOG.isDebugEnabled() == true) {
          LOG.debug("candite for wicket internalFinder: "
              + object.getClass().getSimpleName()
              + "|"
              + object.getId()
              + "|"
              + object.getPath());
        }
        if (componentClass.isAssignableFrom(object.getClass()) == true) {
          IModel< ? > defaultModel = object.getDefaultModel();
          if (defaultModel instanceof AbstractPropertyModel == true) {
            AbstractPropertyModel propertyModel = (AbstractPropertyModel) defaultModel;
            if (StringUtils.equals(property, propertyModel.getPropertyExpression()) == true) {
              results.add((X) object);
            }
          }
        }
      }
    };
    if (startComponent == null) {
      lastRenderedPage.visitChildren(visitor);
    } else {
      startComponent.visitChildren(visitor);
    }
    return results;
  }
}
