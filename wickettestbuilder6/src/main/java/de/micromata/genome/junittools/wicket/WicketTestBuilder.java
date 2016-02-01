package de.micromata.genome.junittools.wicket;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.Page;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.protocol.http.mock.MockHttpServletResponse;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.tester.WicketTester;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.micromata.genome.junittools.wicket.models.WicketTestBuilderModelWrapper;
import de.micromata.genome.tpsb.CommonTestBuilder;
import de.micromata.genome.util.matcher.BooleanListRulesFactory;
import de.micromata.genome.util.matcher.Matcher;
import junit.framework.AssertionFailedError;

/**
 * TODO RK (do-Methoden nicht fuer Interne)
 * 
 * @author Roger Rene Kommer (r.kommer.extern@micromata.de)
 * @author Johannes Unterstein (j.unterstein@micromata.de)
 */
public class WicketTestBuilder<T extends WicketTestBuilder<?>>extends CommonTestBuilder<T>
{

  private static final Logger LOG = LoggerFactory.getLogger(WicketTestBuilder.class);

  protected final WicketTester tester;

  public WicketTestBuilder(WicketTester tester)
  {
    this.tester = tester;
  }

  public WicketTestBuilder(WebApplication application)
  {
    tester = _getWicketTester(application);
  }

  public WicketTester _getWicketTester()
  {
    return tester;
  }

  /**
   * TODO RK make them protected.
   * 
   * @param pageClass
   * @return
   */
  @Deprecated
  public T _goToPage(Page page)
  {
    tester.startPage(page);
    return (T) this;
  }

  /**
   * TODO RK make them protected.
   * 
   * @param pageClass
   * @return
   */
  public T _gotoPage(Class<? extends Page> pageClass)
  {
    tester.startPage(pageClass);
    return getBuilder();
  }

  public T _gotoPage(Class<? extends Page> pageClass, PageParameters pageParameters)
  {
    tester.startPage(pageClass, pageParameters);
    return _validateRenderedPage(pageClass);
  }

  /**
   * TODO RK make them protected.
   * 
   * @param pageClass
   * @return
   */
  @Deprecated
  public T _validateRenderedPage(Class<? extends Page> pageClass) throws TpsbWicketWrongPageException
  {
    try {
      tester.assertRenderedPage(pageClass);
    } catch (AssertionFailedError ex) {
      fail(new TpsbWicketWrongPageException(ex.getMessage(), this));
    } catch (Throwable ex) { // NOSONAR "Illegal Catch" framework
      fail("Assertion failed: " + ex.getMessage());
    }
    return getBuilder();
  }

  protected T doClickLink(Component component)
  {
    return doClickLink(component, false);
  }

  @Deprecated
  public T _doExecuteAjaxEvent(final Component component, final String event)
  {
    tester.executeAjaxEvent(component, event);
    return (T) this;
  }

  @Deprecated
  protected T doClickLink(Component component, boolean isAjax)
  {
    tester.clickLink(component.getPageRelativePath(), isAjax);
    return (T) this;
  }

  public Page _getLastRenderedPage()
  {
    return tester.getLastRenderedPage();
  }

  @Deprecated
  public <X extends Component> X _findSingleComponentForWicketId(final Class<X> componentClass, final String wicketId)
  {
    List<X> result = _findComponentsForWicketId(componentClass, wicketId);
    if (result.size() != 1) {
      fail("There must be only one result, found: " + result.size() + "; wicketid: " + wicketId);
    }
    return result.get(0);
  }

  @Deprecated
  public <X extends Component> List<X> _findComponentsForWicketId(final Class<X> componentClass, final String wicketId)
  {
    return TpsbWicketTools._internalFinder(tester, null, componentClass, wicketId, true);
  }

  @Deprecated
  protected <X extends Component> X _findSingleComponentForSuffixInPath(final Class<X> componentClass,
      final String wicketId)
  {
    List<X> result = _findComponentsForSuffixInPath(componentClass, wicketId);
    if (result.size() != 1) {
      fail("There must be only one result, found: " + result.size() + "; wicketid: " + wicketId);
    }
    return result.get(0);
  }

  @Deprecated
  protected <X extends Component> List<X> _findComponentsForSuffixInPath(final Class<X> componentClass,
      final String path)
  {
    return TpsbWicketTools._internalFinder(tester, null, componentClass, path, false);
  }

  //  @Deprecated
  //  protected <X extends Component> X _findSingleComponentrrWicketId(MarkupContainer startComponent, final Class<X> componentClass,
  //      final String wicketId)
  //  {
  //    List<X> result = _findComponentsForWicketId(startComponent, componentClass, wicketId);
  //    if (result.size() != 1) {
  //      fail("There must be only one result, found: " + result.size() + "; wicketid: " + wicketId);
  //    }
  //    return result.get(0);
  //  }

  @Deprecated
  protected <X extends Component> List<X> _findComponentsForWicketId(MarkupContainer startComponent,
      final Class<X> componentClass,
      final String wicketId)
  {
    return TpsbWicketTools._internalFinder(tester, startComponent, componentClass, wicketId, true);
  }

  //  @Deprecated
  //  protected <X extends Component> List<X> _findComponentsForSuffixInPath(MarkupContainer startComponent, final Class<X> componentClass,
  //      final String path)
  //  {
  //    return TpsbWicketTools._internalFinder(tester, startComponent, componentClass, path, false);
  //  }

  protected T _doFillModels(WicketTestBuilderModelWrapper modelWrapper)
  {
    return _doFillModels(null, modelWrapper);
  }

  protected T _doFillModels(MarkupContainer startComponent, WicketTestBuilderModelWrapper modelWrapper)
  {
    for (WicketTestBuilderModelWrapper.ModelWrapper wrapper : modelWrapper.getWrappedList()) {
      List<? extends Component> componentsForSuffixInPath = TpsbWicketTools._findComponentsForPropertyModelProperty(
          tester,
          startComponent,
          wrapper.getTargetClass(), wrapper.getTargetWicketPath());
      if (componentsForSuffixInPath.size() == 1) {
        Component component = componentsForSuffixInPath.get(0);
        if (component.getDefaultModel() != null) {
          component.setDefaultModelObject(wrapper.getModelValue());
        } else {
          LOG.warn("Found component has no model attached, aborting. class="
              + wrapper.getTargetClass()
              + " and wicket:path="
              + wrapper.getTargetWicketPath());
        }
      } else {
        LOG.warn("Unable to find (or found more then one) component with class="
            + wrapper.getTargetClass()
            + " and wicket:path="
            + wrapper.getTargetWicketPath());
      }
    }
    return (T) this;
  }

  protected <X extends Component> X _findSingleComponentForPropertyModelProperty(final Class<X> componentClass,
      final String property)
  {
    List<X> result = _findComponentsForPropertyModelProperty(componentClass, property);
    if (result.size() != 1) {
      throw new IllegalArgumentException("There must be only one result, found: " + result.size());
    }
    return result.get(0);
  }

  protected <X extends Component> List<X> _findComponentsForPropertyModelProperty(final Class<X> componentClass,
      final String property)
  {
    return TpsbWicketTools._findComponentsForPropertyModelProperty(tester, null, componentClass, property);
  }

  protected WicketTester _getWicketTester(WebApplication application)
  {
    return new WicketTester(application);
  }

  public String _getLastResponseAsString()
  {
    return _getWicketTester().getLastResponseAsString();
    //    List<MockHttpServletResponse> prevrps = tester.getPreviousResponses();
    //    String prevDoc = null;
    //    if (prevrps.size() > 2) {
    //      MockHttpServletResponse prevreq = prevrps.get(prevrps.size() - 2);
    //      prevDoc = prevreq.getDocument();
    //    }
    //    String curresp = tester.getLastResponseAsString();
    //    return curresp;
  }

  public String _getLastDocResponseAsString()
  {
    List<MockHttpServletResponse> prevrps = tester.getPreviousResponses();
    for (int i = prevrps.size() - 1; i >= 0; --i) {
      MockHttpServletResponse prevreq = prevrps.get(i);
      String doc = prevreq.getDocument();
      if (doc.contains("<ajax-response>") == false) {
        return doc;
      }
    }
    String curresp = tester.getLastResponseAsString();
    return curresp;
  }

  /**
   * @deprecated validateMatchingLastRenderedResponse(matcher);
   * @param searchString
   * @return
   */
  @Deprecated
  public T _validateLastRenderedResponseContains(String searchString)
  {

    validateTrue(StringUtils.contains(_getLastResponseAsString(), searchString),
        "The given search string was not found, but should. s="
            + searchString);
    return (T) this;
  }

  @Deprecated
  public T _validateLastRenderedResponseContainsNot(String searchString)
  {
    validateLastRenderedResponseOccurrenceOfString(searchString, 0);
    return (T) this;
  }

  /**
   * Prueft, ob der Matcherausdruck auf den HTML-Ausgabe der aktuellen Seite entspricht.
   * 
   * @param matcherExpression Matcherausdruck (BooleanListRulesFactory)
   * @return builder.
   */
  public T validateMatchingLastRenderedResponse(String matcherExpression)
  {
    BooleanListRulesFactory<String> factory = new BooleanListRulesFactory<String>();
    Matcher<String> match = factory.createMatcher(matcherExpression);
    return _validateMatchingLastRenderedResponse(match);
  }

  /**
   * Prueft, ob der Matcherausdruck auf den HTML-Ausgabe der aktuellen Seite entspricht.
   * 
   * @param matcherExpression Matcherausdruck (BooleanListRulesFactory)
   * @return builder.
   */
  public T _validateMatchingLastRenderedResponse(Matcher<String> matcher)
  {
    boolean found = matcher.match(_getLastResponseAsString());
    validateTrue(found, "Cannot match last rendered page with matcher: " + matcher.toString());

    return getBuilder();
  }

  @Deprecated
  public T _validateLastRenderedResponseOccurrenceOfStringABeforeStringB(String stringA, String stringB)
  {
    String lastResponse = _getLastResponseAsString();
    int posA = StringUtils.indexOf(lastResponse, stringA);
    int posB = StringUtils.indexOf(lastResponse, stringB);
    validateNotEquals(-1, posA);
    validateNotEquals(-1, posB);
    validateTrue(posA < posB, "String A must occurr before String B");
    return (T) this;
  }

  @Deprecated
  public T validateLastRenderedResponseOccurrenceOfString(String searchString, int numberOfOccurrence)
  {
    validateEquals(numberOfOccurrence, StringUtils.countMatches(_getLastResponseAsString(), searchString));
    return (T) this;
  }

}
