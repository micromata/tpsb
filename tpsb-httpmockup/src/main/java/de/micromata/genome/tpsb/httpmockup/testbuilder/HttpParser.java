package de.micromata.genome.tpsb.httpmockup.testbuilder;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import de.micromata.genome.tpsb.builder.SimpleTextParser;
import de.micromata.genome.util.types.Pair;

/**
 * Parses a Request/Response from a text
 * 
 * TODO Body als param auswerten wenn Content-Type: application/x-www-form-urlencoded; charset=UTF-8
 * 
 * @author Roger Rene Kommer (r.kommer.extern@micromata.de)
 * 
 */
public class HttpParser extends SimpleTextParser
{

  private String method;

  private String url;

  private int mayorVersion = 1;

  private int minorVersion = 1;

  private List<Pair<String, String>> headers = new ArrayList<Pair<String, String>>();

  private String queryString = "";

  private List<Pair<String, String>> parameters = new ArrayList<Pair<String, String>>();

  private String body = "";

  private String getHeader(String name)
  {
    for (Pair<String, String> p : headers) {
      if (p.getKey().equals(name) == true) {
        return p.getValue();
      }
    }
    return null;
  }

  public HttpParser(String text)
  {
    super(text);
    parse();
  }

  private void parse()
  {
    parseMethod();
    parseHeaders();
    skipNl();
    body = rest();
    checkUrlPost();
  }

  private void checkUrlPost()
  {
    String ct = getHeader("Content-Type");
    if (ct == null) {
      return;
    }
    if (ct.contains("application/x-www-form-urlencoded") == false) {
      return;
    }
    String trimmedbody = StringUtils.trim(body);
    if (trimmedbody.isEmpty() == true) {
      return;
    }
    if (StringUtils.isEmpty(queryString) == false) {
      queryString += "&" + trimmedbody;
    } else {
      queryString = trimmedbody;
    }
  }

  public List<Pair<String, String>> getRequestParameters()
  {
    List<Pair<String, String>> ret = new ArrayList<Pair<String, String>>();
    if (queryString.length() == 0) {
      return ret;
    }
    String rest = queryString;
    int idx = 0;
    while (idx != -1 && rest.length() > 0) {
      idx = rest.indexOf('&');
      String ps = rest;
      if (idx != -1) {
        ps = rest.substring(0, idx);
        rest = rest.substring(idx + 1);
      }
      int pidx = ps.indexOf('=');
      String value = "";
      if (pidx != -1) {
        value = ps.substring(pidx + 1);
        ps = ps.substring(0, pidx);
      }
      ret.add(Pair.make(ps, value));
    }
    return ret;
  }

  private String splitUrl(String url)
  {
    int idx = url.indexOf('?');
    if (idx == -1) {
      return url;
    }
    queryString = url.substring(idx + 1);
    url = url.substring(0, idx);
    return url;
  }

  private void parseMethod()
  {
    // GET /Functional-Testing/getting-started-with-assertions.html HTTP/1.1
    int start = currentIndex();
    skipToWs();
    method = substring(start, currentIndex());
    skipWs();
    start = currentIndex();
    skipToWs();
    url = substring(start, currentIndex());
    url = splitUrl(url);
    skipWs();
    if (startsWith("HTTP") == true) {
      inc(4);
    }
    skipLine();
  }

  private void parseHeaders()
  {
    while (eof() == false) {
      if (parseHeader() == false) {
        break;
      }
      if (isEmptyLine() == true) {
        break;
      }
    }

  }

  private boolean parseHeader()
  {
    String key = "";
    int start = currentIndex();
    char nc = skipUntil(':', '\n', '\r');
    if (nc != ':') {
      reset(start);
      return false;
    }

    key = substring(start, currentIndex());
    inc();
    if (key.length() == 0) {
      reset(start);
      return false;
    }
    start = currentIndex();
    skipEndOfLine();
    String value = substring(start, currentIndex());
    headers.add(Pair.make(key, value));
    skipLine();
    return true;
  }

  public String getMethod()
  {
    return method;
  }

  public void setMethod(String method)
  {
    this.method = method;
  }

  public String getUrl()
  {
    return url;
  }

  public void setUrl(String url)
  {
    this.url = url;
  }

  public int getMayorVersion()
  {
    return mayorVersion;
  }

  public void setMayorVersion(int mayorVersion)
  {
    this.mayorVersion = mayorVersion;
  }

  public int getMinorVersion()
  {
    return minorVersion;
  }

  public void setMinorVersion(int minorVersion)
  {
    this.minorVersion = minorVersion;
  }

  public List<Pair<String, String>> getHeaders()
  {
    return headers;
  }

  public void setHeaders(List<Pair<String, String>> headers)
  {
    this.headers = headers;
  }

  public String getBody()
  {
    return body;
  }

  public void setBody(String body)
  {
    this.body = body;
  }

}
