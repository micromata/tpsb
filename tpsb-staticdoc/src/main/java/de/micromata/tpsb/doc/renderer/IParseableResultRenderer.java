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

package de.micromata.tpsb.doc.renderer;

import de.micromata.tpsb.doc.ParserConfig;

/**
 * Render-Interface, welches eines entsprechenden Parser zum gerenderten Ergebnis bereitstellt
 * 
 * @author Stefan Stützer (s.stuetzer@micromata.com)
 */
public interface IParseableResultRenderer extends IResultRenderer
{

  public IResultParser getParser();
  /**
   * Render a single FileInfo
   * @param obj the obj
   * @param cfg the parser configuration
   */
  void renderObject(Object obj, ParserConfig cfg);

}
