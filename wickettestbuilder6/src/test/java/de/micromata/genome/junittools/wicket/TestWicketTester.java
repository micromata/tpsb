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

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.FormComponent;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.micromata.genome.junittools.wicket.test.MyWicketTestBuilder;
import de.micromata.genome.junittools.wicket.test.testapp.HomePage;

/**
 * @author Johannes Unterstein (j.unterstein@micromata.de)
 */
public class TestWicketTester
{
  MyWicketTestBuilder builder;

  @Before
  public void setUp()
  {
    builder = new MyWicketTestBuilder();
  }

  @Test
  public void testFindComponent()
  {
    builder._gotoPage(HomePage.class);
    List<WebMarkupContainer> first = builder._findComponentsForWicketId(WebMarkupContainer.class, "first");
    Assert.assertEquals("You should only find one component with wicket:id='first'", 1, first.size());
  }

  @Test
  public void testNotFindComponent()
  {
    builder._gotoPage(HomePage.class);
    List<FormComponent> first = builder._findComponentsForWicketId(FormComponent.class, "first");
    Assert.assertEquals("You should find no FormComponent with wicket:id='first'", 0, first.size());
  }

  @Test
  public void testFindPath()
  {
    builder._gotoPage(HomePage.class);
    List<WebMarkupContainer> first = builder._findComponentsForSuffixInPath(WebMarkupContainer.class, "main:first");
    Assert.assertEquals("You should only find one component with wicket:path='main:first'", 1, first.size());
  }

  @Test
  public void testFindComponentWithStartComponent()
  {
    builder._gotoPage(HomePage.class);
    List<WebMarkupContainer> main = builder._findComponentsForWicketId(WebMarkupContainer.class, "main");
    Assert.assertEquals("You should only find one component with wicket:id='main'", 1, main.size());
    List<WebMarkupContainer> first = builder._findComponentsForWicketId(main.get(0), WebMarkupContainer.class, "first");
    Assert.assertEquals("You should only find one component with wicket:id='first'", 1, first.size());
  }
}
