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

package de.micromata.genome.tpsb.htmlunit;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import org.apache.commons.collections15.CollectionUtils;
import org.w3c.dom.Element;

import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

/**
 * Adopt http://webdriver.googlecode.com/svn/trunk/common/src/java/org/openqa/selenium/By.java
 * 
 * @author roger@micromata.de
 * 
 */
public abstract class By
{
  public static By id(final String id)
  {
    if (id == null)
      throw new IllegalArgumentException("Cannot find elements with a null id attribute.");

    return new By() {
      @Override
      public List<Element> findElements(HtmlPage page)
      {
        List<Element> ret = new ArrayList<Element>();
        ret.add(page.getElementById(id));
        return ret;
      }

      @Override
      public Element findElement(HtmlPage page)
      {
        return page.getElementById(id);
      }

      public String toString()
      {
        return "By.id: " + id;
      }
    };
  }

  public static By name(final String name)
  {
    if (name == null)
      throw new IllegalArgumentException("Cannot find elements when name is null.");

    return new By() {
      @Override
      public List<Element> findElements(HtmlPage page)
      {
        return By.xpath("//*[@name = '" + name + "']").findElements(page);
      }

      @Override
      public String toString()
      {
        return "By.name: " + name;
      }
    };
  }

  public static By title(final String name)
  {
    if (name == null)
      throw new IllegalArgumentException("Cannot find elements when title is null.");

    return new By() {
      @Override
      public List<Element> findElements(HtmlPage page)
      {
        return By.xpath("//*[@title = '" + name + "']").findElements(page);
      }

      @Override
      public String toString()
      {
        return "By.title: " + name;
      }
    };
  }

  public static By xpath(final String xpathExpression)
  {
    if (xpathExpression == null)
      throw new IllegalArgumentException("Cannot find elements when the XPath expression is null.");

    return new By() {
      @Override
      public List<Element> findElements(HtmlPage page)
      {
        List< ? extends Object> objects = page.getByXPath(xpathExpression);
        List<Element> ret = new ArrayList<Element>();
        for (Object o : objects) {
          ret.add((Element) o);
        }
        return ret;
      }

      @Override
      public String toString()
      {
        return "By.xpath: " + xpathExpression;
      }
    };
  }

  public static By and(final By... bies)
  {
    return new By() {

      @Override
      public List<Element> findElements(HtmlPage page)
      {
        List<Element> ret = null;
        for (By by : bies) {
          if (ret == null) {
            ret = by.findElements(page);
          } else {
            ret = new ArrayList<Element>(CollectionUtils.intersection(ret, by.findElements(page)));
          }
        }
        return ret;
      }
    };
  }

  public static By elementType(final Class< ? > clazz)
  {
    return new By() {

      @Override
      public List<Element> findElements(HtmlPage page)
      {
        Iterable<HtmlElement> ell = page.getAllHtmlChildElements();
        List<Element> ret = new ArrayList<Element>();
        for (HtmlElement el : ell) {
          if (clazz.isAssignableFrom(el.getClass()) == true) {
            ret.add(el);
          }
        }
        return ret;
      }

    };
  }

  public static By className(final String className)
  {
    if (className == null)
      throw new IllegalArgumentException("Cannot find elements when the class name expression is null.");

    return new By() {
      @Override
      public List<Element> findElements(HtmlPage page)
      {
        return By.xpath("//*[" + containingWord("class", className) + "]").findElements(page);
      }

      /**
       * Generates a partial xpath expression that matches an element whose specified attribute contains the given CSS word. So to match
       * &lt;div class='foo bar'&gt; you would say "//div[" + containingWord("class", "foo") + "]".
       * 
       * @param attribute name
       * @param word name
       * @return XPath fragment
       */
      private String containingWord(String attribute, String word)
      {
        return "contains(concat(' ',normalize-space(@" + attribute + "),' '),' " + word + " ')";
      }

      @Override
      public String toString()
      {
        return "By.className: " + className;
      }
    };
  }

  /**
   * Find a single element. Override this method if necessary.
   * 
   * @param context A context to use to find the element
   * @return The WebElement that matches the selector
   */
  public Element findElement(HtmlPage context)
  {
    List<Element> allElements = findElements(context);
    if (allElements == null || allElements.size() == 0)
      throw new NoSuchElementException("Cannot locate an element using " + toString());
    return allElements.get(0);
  }

  /**
   * Find many elements.
   * 
   * @param context A context to use to find the element
   * @return A list of WebElements matching the selector
   */
  public abstract List<Element> findElements(HtmlPage page);

  @Override
  public boolean equals(Object o)
  {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;

    By by = (By) o;

    return toString().equals(by.toString());
  }

  @Override
  public int hashCode()
  {
    return toString().hashCode();
  }

}
