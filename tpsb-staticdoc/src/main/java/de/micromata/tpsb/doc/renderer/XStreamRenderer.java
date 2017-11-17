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
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.CharEncoding;
import org.apache.log4j.Logger;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

import de.micromata.tpsb.doc.ParserConfig;
import de.micromata.tpsb.doc.ParserContext;
import de.micromata.tpsb.doc.parser.AnnotationInfo;
import de.micromata.tpsb.doc.parser.FileInfo;
import de.micromata.tpsb.doc.parser.JavaDocInfo;
import de.micromata.tpsb.doc.parser.MethodInfo;
import de.micromata.tpsb.doc.parser.ParameterInfo;
import de.micromata.tpsb.doc.parser.ParserResult;
import de.micromata.tpsb.doc.parser.TestStepInfo;

/**
 * Renderer auf Basis der XStream Library, welcher einen entsprechenden Parser anbietet
 * 
 * @author Stefan Stützer (s.stuetzer@micromata.com)
 */
public class XStreamRenderer extends AbstractResultRenderer implements IParseableResultRenderer
{

  Logger log = Logger.getLogger(XStreamRenderer.class);

  public XStreamRenderer()
  {
  }

  public XStreamRenderer(String outputFilename)
  {
    super(outputFilename);
  }

  public static XStream createXStream()
  {
    XStream xStream = new XStream(new DomDriver());
    xStream.alias("parserResult", ParserResult.class);
    xStream.alias("fileInfo", FileInfo.class);
    xStream.alias("methodInfo", MethodInfo.class);
    xStream.alias("testStepInfo", TestStepInfo.class);
    xStream.alias("parameterInfo", ParameterInfo.class);
    xStream.alias("javaDocInfo", JavaDocInfo.class);
    xStream.alias("annotationInfo", AnnotationInfo.class);
    xStream.alias("annotationInfo", AnnotationInfo.class);
    return xStream;
  }

  private byte[] toBytes(String text)
  {
    try {
      return text.getBytes(CharEncoding.UTF_8);
    } catch (UnsupportedEncodingException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void renderResult(ParserContext ctx, ParserConfig cfg)
  {
    log.info("Render Parser-Ergebnis im XML-Format");
    XStream xStream = createXStream();
    String xml = xStream.toXML(ctx.getCurrentParserResult());
    byte[] bytes = toBytes(xml);
    save(bytes);
  }

  @Override
  public void renderObject(Object obj, ParserConfig cfg)
  {

    XStream xStream = createXStream();
    String xml = xStream.toXML(obj);
    byte[] bytes = toBytes(xml);
    save(bytes);
  }

  public void save(byte[] data)
  {
    try {
      log.info("Schreibe Datei " + getOutputFilename());
      File file = new File(getOutputFilename());
      FileUtils.writeByteArrayToFile(file, data);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public byte[] renderResult(Map<String, Object> ctx)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public IResultParser getParser()
  {
    return new XStreamParser();
  }

  @Override
  public String getFileExtension()
  {
    return "xml";
  }
}
