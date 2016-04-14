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

package de.micromata.genome.tpsb.httpmockup;

import de.micromata.genome.util.matcher.BooleanListRulesFactory;
import de.micromata.genome.util.matcher.Matcher;

/**
 * web.xml config part
 * 
 * @author roger@micromata.de
 * 
 */
public class MockMapDef
{
  private String name;

  private String urlPattern;

  private Matcher<String> urlMatcher;

  private String dispatcher; // TODO genome???

  public MockMapDef()
  {

  }

  public String getUrlPattern()
  {
    return urlPattern;
  }

  public void setUrlPattern(String urlPattern)
  {
    this.urlPattern = urlPattern;
    String matcherPattern = this.urlPattern;
    // TODO bug "/*,-/SoapConnector,-/schema/*" matches this pattern! don't know for what this if is.
    if (this.urlPattern.endsWith("/*") == true && this.urlPattern.indexOf(' ') == -1) {
      matcherPattern = this.urlPattern + "," + this.urlPattern.substring(0, this.urlPattern.length() - 2);
    }
    urlMatcher = new BooleanListRulesFactory<String>().createMatcher(matcherPattern);
  }

  public String getName()
  {
    return name;
  }

  public void setName(String name)
  {
    this.name = name;
  }

  public String getDispatcher()
  {
    return dispatcher;
  }

  public void setDispatcher(String dispatcher)
  {
    this.dispatcher = dispatcher;
  }

  public Matcher<String> getUrlMatcher()
  {
    return urlMatcher;
  }

  public void setUrlMatcher(Matcher<String> urlMatcher)
  {
    this.urlMatcher = urlMatcher;
  }

}
