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

import de.micromata.genome.tpsb.TpsbException;

public class HtmlPageException extends TpsbException
{

  private static final long serialVersionUID = 6859424038268631311L;

  private HtmlPageBase< ? > page;

  public HtmlPageException(String message, HtmlPageBase< ? > page)
  {
    super(message, page);
    this.page = page;
  }

  public HtmlPageException(String message, HtmlPageBase< ? > page, Throwable cause)
  {
    super(message, cause, page);
    this.page = page;
  }

  public String getFailureDump()
  {
    StringBuilder sb = new StringBuilder();

    sb.append(getMessage()).append(":\n");
    sb.append("Request:\n").append(page.getRequestDump()).append("\n\nResponse:\n").append(page.getResponseDump());
    return sb.toString();
  }

  public HtmlPageBase< ? > getPage()
  {
    return page;
  }

  public void setPage(HtmlPageBase< ? > page)
  {
    this.page = page;
  }
}
