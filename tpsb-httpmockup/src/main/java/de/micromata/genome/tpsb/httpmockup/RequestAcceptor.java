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

import java.io.IOException;

import javax.servlet.ServletException;

/**
 * Accepts a servlet request.
 * 
 * @author Roger Rene Kommer (r.kommer.extern@micromata.de)
 * 
 */
public interface RequestAcceptor
{
  /**
   * Accept
   * 
   * @param request the mock implementation of a request
   * @param response the mock implementation of the response
   * @throws IOException when an error happened during accepting the request
   */
  public void acceptRequest(final MockHttpServletRequest request, final MockHttpServletResponse response) throws IOException,
      ServletException;
}
