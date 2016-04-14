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

import java.io.IOException;
import java.util.List;

import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Element;

import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.TopLevelWindow;
import com.gargoylesoftware.htmlunit.WebRequestSettings;
import com.gargoylesoftware.htmlunit.html.ClickableElement;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlOption;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSelect;
import com.gargoylesoftware.htmlunit.html.SubmittableElement;

import de.micromata.genome.tpsb.CommonTestBuilder;
import de.micromata.genome.tpsb.TpsbException;
import de.micromata.genome.util.bean.PrivateBeanUtils;
import de.micromata.genome.util.matcher.Matcher;
import de.micromata.genome.util.runtime.RuntimeIOException;

/**
 * Base class for a TestBuilder representing a Html page.
 * 
 * @author roger
 * 
 * @param <T>
 */
public abstract class HtmlPageBase<T extends HtmlPageBase< ? >> extends CommonTestBuilder<T>
{

  protected HtmlPage htmlPage;

  /**
   * 
   * @return the standard form if any, other null
   */
  public abstract HtmlForm getStdForm();

  HtmlWebTestApp< ? > webApp;

  public HtmlPageBase(HtmlWebTestApp< ? > webApp, HtmlPage htmlPage)
  {
    this.webApp = webApp;
    this.htmlPage = htmlPage;
  }

  //
  public HtmlPageBase(HtmlPageBase< ? > otherPage)
  {
    super(otherPage);
    this.webApp = getWebApp();
  }

  protected void validatePage()
  {
    String url = htmlPage.getWebResponse().getRequestUrl().toString();
    String page = getPageName();
    if (url.indexOf(page) == -1) {
      throw new HtmlPageException("Invalid page. Expected: " + page + "; url: " + url, this);
    }
  }

  public T setP(String name, String value)
  {
    HtmlForm form = getStdForm();
    form.getInputByName(name).setValueAttribute(value);
    return getBuilder();
  }

  public T setCheckBox(String name, boolean on) throws Exception
  {
    HtmlInput input = getStdForm().getInputByName(name);
    if (input.isChecked() != on)
      input.click();
    return getBuilder();
  }

  /**
   * Load pending content
   */
  public T loadLinkedContent()
  {
    try {
      if (getHtmlWebClient().isLoadImages() == true) {
        HtmlPageHelper.loadImages(this);
      }
      return getBuilder();
    } catch (RuntimeException ex) {
      throw ex;
    } catch (Exception ex) {
      throw new TpsbException(ex, this);
    }
  }

  public String getPageName()
  {
    return HtmlPageBase.getPageName(getClass());
  }

  public static String getPageName(Class< ? extends HtmlPageBase> clazz)
  {
    HtmlPageUrl an = clazz.getAnnotation(HtmlPageUrl.class);
    if (an == null)
      throw new RuntimeException("Class missing HtmlPageUrl annotation: " + clazz.getSimpleName());
    return an.value();
  }

  public String getPageURL(Class< ? extends HtmlPageBase< ? >> clazz)
  {
    return getBaseUrl() + getPageName(clazz);

  }

  public String getBaseUrl()
  {
    return getWebClient().getBaseUrl();
  }

  public HtmlWebClient getHtmlWebClient()
  {
    return (HtmlWebClient) htmlPage.getWebClient();
  }

  /**
   * 
   * @return true if JavaScript is enabled
   */
  public boolean isJs()
  {
    return htmlPage.getWebClient().isJavaScriptEnabled();
  }

  public void ensureJs()
  {
    if (isJs() == false)
      throw new RuntimeException("Function requires JavaScript");
  }

  public void closeWindow()
  {
    TopLevelWindow tlw = (TopLevelWindow) getHtmlPage().getEnclosingWindow().getTopWindow();
    tlw.close();
  }

  public HtmlForm getFormByName(String name)
  {
    try {
      return htmlPage.getFormByName(name);
    } catch (Exception ex) {
      throw new HtmlPageException("Form not found: " + ex.getMessage(), this, ex);
    }
  }

  protected HtmlPage executeOnXNJ(String on)
  {
    return executeOnXNJ("executeMethod", on);
  }

  protected HtmlPage executeOnXNJ(String field, String on)
  {
    String method = on;
    try {
      getElement(By.id(field)).setAttribute("value", method);
      getElement(By.id(field)).setAttribute("name", method);
      return (HtmlPage) getStdForm().submit(null);
    } catch (RuntimeException ex) {
      throw ex;
    } catch (Exception ex) {
      throw new TpsbException(ex, this);
    }
  }

  public static <X extends HtmlPageBase< ? >> X createPage(HtmlWebTestApp< ? > webApp, HtmlPage target, Class<X> targetClazz)
  {
    try {
      X basePage = PrivateBeanUtils.createInstance(targetClazz, webApp, target);
      basePage.setHtmlPage(target);
      basePage.validatePage();
      basePage.loadLinkedContent();
      return basePage;
    } catch (RuntimeException ex) {
      throw ex;
    } catch (Exception ex) {
      throw new TpsbException(ex);
    }
  }

  public static <X extends HtmlPageBase< ? >> X createPage(HtmlWebTestApp< ? > webApp, Page target, Class<X> targetClazz)
  {
    return createPage(webApp, (HtmlPage) target, targetClazz);
  }

  public <X extends HtmlPageBase< ? >> X doExecuteSubmit(String buttonNameOrId, Class<X> targetClazz)
  {
    SubmittableElement submit = getSubmitableById(buttonNameOrId);
    if (submit != null) {
      return executeSubmit(getStdForm(), submit, targetClazz);
    }
    return executeSubmit(getStdForm(), (SubmittableElement) getElementByIdOrName(buttonNameOrId), targetClazz);
  }

  public <X extends HtmlPageBase< ? >> X executeSubmit(SubmittableElement submit, Class<X> targetClazz) throws Exception
  {
    return executeSubmit(getStdForm(), submit, targetClazz);
  }

  public <X extends HtmlPageBase< ? >> X executeSubmit(HtmlForm form, SubmittableElement submit, Class<X> targetClazz)
  {
    try {
      HtmlPage target = (HtmlPage) form.submit(submit);
      return createPage(webApp, target, targetClazz);
    } catch (IOException ex) {
      throw new RuntimeIOException(ex);
    } catch (RuntimeException ex) {
      throw ex;
    }
  }

  /**
   * This method switch on JavaScript
   * 
   * @param <X>
   * @param id
   * @param targetClazz
   * @return
   * @throws Exception
   */
  public <X extends HtmlPageBase< ? >> X clickJavaScriptButtonById(String id, Class<X> targetClazz) throws Exception
  {
    boolean wasEnabled = getHtmlWebClient().isJavaScriptEnabled();

    try {
      getHtmlWebClient().setJavaScriptEnabled(true);
      Element el = htmlPage.getElementById(id);
      HtmlPage target = (HtmlPage) ((ClickableElement) el).click();
      return createPage(webApp, target, targetClazz);
    } finally {
      getHtmlWebClient().setJavaScriptEnabled(wasEnabled);
    }
  }

  public <OT extends HtmlPageBase< ? >> OT doLink(By by, Class<OT> expected)
  {
    try {
      return createPage(webApp, getLink(by).click(), expected);
    } catch (RuntimeException ex) {
      throw ex;
    } catch (Exception ex) {
      throw new TpsbException(ex, this);
    }
  }

  public Element getElementByIdOrName(String idOrName)
  {
    Element el = htmlPage.getElementById(idOrName);
    if (el != null)
      return el;
    el = getStdForm().getInputByName(idOrName);
    if (el == null)
      throw new HtmlPageException("Assert element not found by Name or Id: " + idOrName, this);
    return el;
  }

  public <X extends HtmlPageBase< ? >> X executeStdLinkSubmit(String method, Class<X> targetClazz)
  {
    HtmlPage target;
    try {
      if (htmlPage.getWebClient().isJavaScriptEnabled() == true) {
        target = (HtmlPage) ((HtmlAnchor) getElementByIdOrName(method)).click();
      } else {
        target = executeOnXNJ(method);
      }
      return createPage(webApp, target, targetClazz);
    } catch (RuntimeException ex) {
      throw ex;
    } catch (Exception ex) {
      throw new TpsbException(ex, this);
    }
  }

  // public <X extends HtmlPageBase< ? >> X doOk(Class<X> targetClazz)
  // {
  // return executeStdLinkSubmit("method_onOk", targetClazz);
  // }

  public <X extends HtmlPageBase< ? >> X doLinkByHref(String href, Class<X> targetClazz)
  {
    try {
      String pureLink = href;
      String command = null;
      int idx = href.indexOf('?');
      if (idx != -1) {
        pureLink = href.substring(0, idx);
        command = StringUtils.trimToNull(href.substring(idx + 1));
      }

      List<HtmlAnchor> anchors = getHtmlPage().getAnchors();
      for (HtmlAnchor a : anchors) {
        String hrf = a.getHrefAttribute();
        if (hrf.startsWith(pureLink) == true && (command == null || href.contains(command) == true)) {
          HtmlPage target = (HtmlPage) getHtmlPage().getAnchorByHref(a.getHrefAttribute()).click();
          return createPage(webApp, target, targetClazz);
        }
      }
      throw new HtmlPageException("HREF not found: " + href, this);
    } catch (RuntimeException ex) {
      throw ex;
    } catch (Exception ex) {
      throw new TpsbException(ex, this);
    }
  }

  public <X extends HtmlPageBase< ? >> X doLinkByPage(Class<X> targetClazz)
  {
    return doLinkByHref(getWebClient().getRootUrl() + getPageName(targetClazz), targetClazz);
  }

  public HtmlForm getFormById(String id)
  {
    Element el = htmlPage.getElementById(id);
    return (HtmlForm) el;
  }

  public SubmittableElement getSubmitableById(String id)
  {
    return (SubmittableElement) htmlPage.getElementById(id);
  }

  public SubmittableElement getSubmitableByName(String name)
  {
    for (HtmlElement hl : htmlPage.getElementsByName(name)) {
      if (hl instanceof SubmittableElement) {
        return (SubmittableElement) hl;
      }
    }
    return null;
  }

  public HtmlPage getHtmlPage()
  {
    return htmlPage;
  }

  public void setHtmlPage(HtmlPage htmlPage)
  {
    this.htmlPage = htmlPage;
  }

  public String getRequestDump()
  {
    WebRequestSettings wr = htmlPage.getWebResponse().getRequestSettings();
    StringBuilder sb = new StringBuilder();
    sb.append(wr.getHttpMethod().toString()).append(" ").append(wr.getURL().toExternalForm()).append("\n");

    for (NameValuePair nv : wr.getRequestParameters()) {
      sb.append(nv.getName()).append("=").append(nv.getValue()).append("\n");
    }
    return sb.toString();
  }

  public String getResponseDump()
  {
    return htmlPage.getWebResponse().getContentAsString();
  }

  public Element getElement(By by) throws Exception
  {
    Element el = by.findElement(getHtmlPage());
    if (el == null)
      throw new HtmlPageException("Assert element not found: " + by, this);
    return el;
  }

  public HtmlAnchor getLink(By by) throws Exception
  {
    return (HtmlAnchor) getElement(by);
  }

  public T validateElementExists(By by) throws Exception
  {
    Element el = by.findElement(getHtmlPage());
    if (el == null)
      throw new HtmlPageException("Assert element not found: " + by, this);
    return getBuilder();
  }

  public String getElementAttribute(By by, String attrName) throws Exception
  {
    Element el = by.findElement(getHtmlPage());
    if (el == null)
      throw new HtmlPageException("Assert element not found: " + by, this);
    String t = el.getAttribute(attrName);
    return t;
  }

  public T validateInputText(By by, Matcher<String> m) throws Exception
  {
    String t = getElementAttribute(by, "value");
    if (m.match(t) == false)
      throw new HtmlPageException("element value not equals. by: " + by + "; " + "; want: " + m.toString() + "; is: " + t, this);
    return getBuilder();
  }

  /**
   * Sets the "selected" state of the specified option. If this "select" element is single-select, then calling this method will deselect
   * all other options. Only options that are actually in the document may be selected.
   * 
   * @param htmlSelect the concerned html select
   * @param index the index of the option that is to change
   * @param isSelected true if the option is to become selected
   * @return return this page
   * @throws Exception
   */
  protected <X extends HtmlPageBase< ? >> X selectOption(HtmlSelect htmlSelect, int index, boolean isSelected, Class<X> targetClazz)
      throws Exception
  {
    HtmlOption htmlOption = htmlSelect.getOption(index);
    Page page = htmlSelect.setSelectedAttribute(htmlOption, isSelected);
    return createPage(webApp, page, targetClazz);
  }

  public HtmlWebClient getWebClient()
  {
    return webApp.getWebClient();
  }

  public HtmlWebTestApp< ? > getWebApp()
  {
    return webApp;
  }

  public void setWebApp(HtmlWebTestApp< ? > webApp)
  {
    this.webApp = webApp;
  }
}
