package de.micromata.genome.junittools.wicket.test.testapp;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.request.mapper.parameter.PageParameters;

/**
 * @author Johannes Unterstein (j.unterstein@micromata.de)
 */
public class HomePage extends WebPage
{
  public HomePage(PageParameters parameters)
  {
    super(parameters);
  }

  @Override
  protected void onInitialize()
  {
    super.onInitialize();

    WebMarkupContainer main = new WebMarkupContainer("main");
    add(main);
    main.add(new WebMarkupContainer("first"));
    main.add(new WebMarkupContainer("second"));
  }
}
