package de.micromata.genome.tpsb.httpmockup.testbuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import de.micromata.genome.tpsb.CommonTestBuilder;
import de.micromata.genome.tpsb.builder.IniLikeScenario;
import de.micromata.genome.tpsb.httpmockup.MockHttpServletRequest;
import de.micromata.genome.tpsb.httpmockup.MockHttpServletResponse;
import de.micromata.genome.util.matcher.Matcher;
import de.micromata.genome.util.matcher.norm.NormBooleanMatcherFactory;
import de.micromata.genome.util.types.Pair;

public class HttpScenario extends IniLikeScenario
{
  private static final String HTTP_REQUEST_SECTION = "HttpRequest";

  private static final String HTTP_REQUEST_PARAMETER_SECTION = "HttpRequestParameters";

  private static final String HTTP_RESPONSE_SECTION = "HttpReponse";

  private HttpParser requestparser;

  public HttpScenario(String text)
  {
    super(text);
    requestparser = new HttpParser(getSectionContent(HTTP_REQUEST_SECTION));
  }

  public MockHttpServletRequest getRequest()
  {
    MockHttpServletRequest ret = new MockHttpServletRequest();
    ret.setMethod(requestparser.getMethod());
    ret.setPathInfo(requestparser.getUrl());
    for (Pair<String, String> hp : requestparser.getHeaders()) {
      ret.addHeader(hp.getKey(), hp.getValue());
    }
    ret.setRequestData(org.apache.commons.codec.binary.StringUtils.getBytesUtf8(requestparser.getBody()));
    List<Pair<String, String>> params = new ArrayList<Pair<String, String>>(requestparser.getRequestParameters());
    for (Map.Entry<String, String> me : getSectionAsProperties(HTTP_REQUEST_PARAMETER_SECTION).entrySet()) {
      params.add(Pair.make(me.getKey(), me.getValue()));
    }
    Map<String, List<String>> pm = new HashMap<String, List<String>>();

    for (Pair<String, String> p : params) {
      List<String> val = pm.get(p.getKey());
      if (val == null) {
        val = new ArrayList<String>();
        pm.put(p.getKey(), val);
      }
      val.add(p.getValue());
    }
    for (Map.Entry<String, List<String>> me : pm.entrySet()) {
      ret.getParameterMap().put(me.getKey(), me.getValue().toArray(new String[] {}));
    }
    return ret;
  }

  private <T extends CommonTestBuilder<?>> void checkMatches(String key, String expected, String given, T testBuilder)
  {
    if (StringUtils.isBlank(expected) == true) {
      return;
    }
    if (matches(expected, given) == false) {
      testBuilder.fail(key + " not matches. expected: " + expected + " but is " + given);
    }
  }

  private boolean matches(String expected, String given)
  {
    Matcher<String> matcher = new NormBooleanMatcherFactory<String>().createMatcher(expected);
    return matcher.match(given);
  }

  public <T extends CommonTestBuilder<?>> void checkExpected(T testBuilder, MockHttpServletResponse resp)
  {
    Map<String, String> props = getSectionAsProperties(HTTP_RESPONSE_SECTION);
    String status = props.get("status");
    checkMatches("status", status, "" + resp.getStatus(), testBuilder);

    for (Map.Entry<String, String> me : props.entrySet()) {
      if (me.getKey().startsWith("header_") == true) {
        List<Object> tl = resp.getHeaderMap().get(me.getKey().substring("header_".length()));
        String ts = "";
        if (tl != null && tl.isEmpty() == false) {
          ts = tl.get(0).toString();
        }

        checkMatches(me.getKey(), me.getValue(), ts, testBuilder);
      }
    }
    String body = props.get("body");
    checkMatches("body", body, resp.getOutputString(), testBuilder);
  }
}
