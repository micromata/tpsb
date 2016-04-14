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

import java.io.File;

import org.apache.log4j.Logger;

import com.thoughtworks.xstream.XStream;

import de.micromata.tpsb.doc.parser.FileInfo;
import de.micromata.tpsb.doc.parser.ParserResult;

/**
 * Parser fuer ein durch den XStream Renderer gerendertes Ergebnis
 * 
 * @author Stefan Stützer (s.stuetzer@micromata.com)
 */
public class XStreamParser implements IResultParser
{

  private final static Logger log = Logger.getLogger(XStreamParser.class);

  @Override
  public ParserResult parseRawFile(String rawDataFileName)
  {
    log.info("Lese XML-Rohdatendatei: " + rawDataFileName);
    File rawDataFile = new File(rawDataFileName);
    if (rawDataFile.exists() == false) {
      log.error("Rohdatendatei existiert nicht: " + rawDataFileName);
    }
    XStream xStream = XStreamRenderer.createXStream();
    ParserResult fromXML = (ParserResult) xStream.fromXML(rawDataFile);
    return fromXML;
  }

  @Override
  public Object parseRawObjectFile(String rawDataFileName)
  {
    log.info("Lese XML-Rohdatendatei: " + rawDataFileName);
    File rawDataFile = new File(rawDataFileName);
    if (rawDataFile.exists() == false) {
      log.error("Rohdatendatei existiert nicht: " + rawDataFileName);
    }
    XStream xStream = XStreamRenderer.createXStream();
    Object o = xStream.fromXML(rawDataFile);
    if (o instanceof FileInfo) {
      FileInfo fi = (FileInfo) o;
      fi.updateMethodToParentReferences();
    }
    return o;
  }
}
