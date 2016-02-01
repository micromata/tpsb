package de.micromata.genome.tpsb.htmlunit.genomelogin.pages;

import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;

import de.micromata.genome.tpsb.htmlunit.By;
import de.micromata.genome.tpsb.htmlunit.HtmlPageBase;
import de.micromata.genome.tpsb.htmlunit.HtmlPageUrl;

/**
 * The login page.
 * 
 * @author roger
 * 
 */
@HtmlPageUrl("Login.action")
public class GenomeLoginPage extends HtmlPageBase<GenomeLoginPage>
{
  public GenomeLoginPage(GenomeConsoleApp webApp, HtmlPage htmlPage)
  {
    super(webApp, htmlPage);
  }

  @Override
  public HtmlForm getStdForm()
  {
    return getFormById("loginForm");
  }

  /**
   * sets the user name
   * 
   * @param user
   * @return this page
   */
  public GenomeLoginPage setUserName(String user)
  {
    HtmlTextInput hinp = (HtmlTextInput) By.and(By.elementType(HtmlTextInput.class), //
        By.id("userName")).findElement(getHtmlPage());
    hinp.setValueAttribute(user);

    return getBuilder();
  }

  /**
   * sets the user password
   * 
   * @param pass
   * @return this page
   */
  public GenomeLoginPage setPasswort(String pass)
  {
    By.id("userPass").findElement(htmlPage).setAttribute("value", pass);
    return getBuilder();
  }

  /**
   * Submit the page and do login
   * 
   * @param <X>
   * @param targetClazz expected page type
   * @return expected page or error
   */
  public <X extends HtmlPageBase< ? >> X doLogin(Class<X> targetClazz)
  {
    return doExecuteSubmit("onLogin", targetClazz);
  }
}
