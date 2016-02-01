package de.micromata.genome.junittools.wicket.test.testapp;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;

/**
 * @author Johannes Unterstein (j.unterstein@micromata.de)
 */
public class FormPage extends WebPage
{
  public FormPage(PageParameters parameters)
  {
    super(parameters);
  }

  @Override
  protected void onInitialize()
  {
    super.onInitialize();

    Form<Void> form = new Form<Void>("form");
    add(form);
    form.add(new TextField<String>("first", Model.of("firstString")));
    form.add(new TextField<String>("second", Model.of("secondString")));
  }
}
