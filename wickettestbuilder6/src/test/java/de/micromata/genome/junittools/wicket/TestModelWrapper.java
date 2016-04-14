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

import org.apache.wicket.markup.html.form.TextField;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.google.gson.Gson;

import de.micromata.genome.junittools.wicket.models.WicketTestBuilderGsonBuilder;
import de.micromata.genome.junittools.wicket.models.WicketTestBuilderModelWrapper;
import de.micromata.genome.junittools.wicket.test.MyWicketTestBuilder;
import de.micromata.genome.junittools.wicket.test.testapp.FormPage;

/**
 * @author Johannes Unterstein (j.unterstein@micromata.de)
 */
public class TestModelWrapper
{
  private Gson gson;

  MyWicketTestBuilder builder;

  @Before
  public void setUp()
  {
    builder = new MyWicketTestBuilder();
    gson = WicketTestBuilderGsonBuilder.getGson();
  }

  @Test
  public void testGson() throws Exception
  {
    WicketTestBuilderModelWrapper wrapper = new WicketTestBuilderModelWrapper();
    wrapper //
        .addModelWrapper(TextField.class, "input1", "text1") //
        .addModelWrapper(TextField.class, "input2", "text2") //
        .addModelWrapper(TextField.class, "input3", "text3") //
        .addModelWrapper(TextField.class, "input4", "text4") //
        .addModelWrapper(TextField.class, "input5", "text5");
    String json = gson.toJson(wrapper);
    WicketTestBuilderModelWrapper newWrapper = gson.fromJson(json, WicketTestBuilderModelWrapper.class);
    Assert.assertEquals("Wrapper size must be 5!", 5, newWrapper.size());
  }

  @Test
  public void testFormPage() throws Exception
  {
    builder._gotoPage(FormPage.class);
    List<TextField> first = builder._findComponentsForWicketId(TextField.class, "first");
    Assert.assertEquals("There must be this form field!", 1, first.size());
    TextField< ? > textField = first.get(0);
    Assert.assertNotNull(textField.getModel());
    Assert.assertEquals("firstString", textField.getModelObject());
  }

  @Test
  @Ignore // TODO ju remove ignore later
  public void testFormPageToFillModel() throws Exception
  {
    builder._gotoPage(FormPage.class);
    List<TextField> first = builder._findComponentsForWicketId(TextField.class, "first");
    Assert.assertEquals("There must be this form field!", 1, first.size());
    TextField< ? > textField = first.get(0);
    Assert.assertNotNull(textField.getModel());
    Assert.assertEquals("firstString", textField.getModelObject());

    // build wrapper
    WicketTestBuilderModelWrapper wrapper = new WicketTestBuilderModelWrapper();
    wrapper.addModelWrapper(TextField.class, "first", "text1");
    wrapper.addModelWrapper(TextField.class, "second", "text2");
    builder._doFillModels(wrapper);
    Assert.assertEquals("text1", textField.getModelObject());
  }
}
