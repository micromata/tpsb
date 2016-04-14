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
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.CharEncoding;
import org.apache.log4j.Logger;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.ResourceNotFoundException;

import de.micromata.tpsb.doc.ParserConfig;
import de.micromata.tpsb.doc.ParserContext;
import de.micromata.tpsb.doc.parser.FileInfo;
import de.micromata.tpsb.doc.parser.ParserResult;

/**
 * Renderer auf Basis der Velocity Template Engine
 * 
 * @author Stefan Stützer (s.stuetzer@micromata.com)
 */
public class VelocityRenderer extends AbstractResultRenderer
{

  private static final String ENCODING = CharEncoding.UTF_8;

  private final static Logger log = Logger.getLogger(VelocityRenderer.class);

  private String stdTemplate;

  private String idxTemplate;

  VelocityEngine ve = new VelocityEngine();

  public final static String DEFAULT_TEMPLATE = "defaultTemplate.vm";

  public final static String DEFAULT_IDX_TEMPLATE = "defaultIdxTemplate.vm";

  public VelocityRenderer()
  {
    this(DEFAULT_IDX_TEMPLATE, DEFAULT_TEMPLATE);
  }

  public VelocityRenderer(String indexTemplate, String stdTemplate)
  {
    this.idxTemplate = indexTemplate;
    this.stdTemplate = stdTemplate;

    // ResourceLoader registrieren
    ve.addProperty("resource.loader", "class");
    ve.addProperty("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
    ve.addProperty("resource.loader", "file");
    ve.addProperty("file.resource.loader.class", "org.apache.velocity.runtime.resource.loader.FileResourceLoader");
    ve.addProperty("resource.loader", "string");
    ve.addProperty("string.resource.loader.repository.class", "org.apache.velocity.runtime.resource.util.StringResourceRepositoryImpl");
    ve.init();
  }

  @Override
  public void renderResult(ParserContext ctx, ParserConfig cfg)
  {
    ParserResult parserResult = ctx.getCurrentParserResult();

    // Rendern einer Index-Seite falls konfiguriert
    if (idxTemplate != null) {
      VelocityContext velocityCtx = new VelocityContext();
      velocityCtx.put("testCount", parserResult.getMethodCount());
      velocityCtx.put("fInfos", parserResult);
      renderResultInFile(velocityCtx, idxTemplate, cfg, "index");
    }

    // Rendern einer Datei pro TestCase
    for (FileInfo fInfo : parserResult) {
      VelocityContext velocityCtx = new VelocityContext();
      velocityCtx.put("fInfo", fInfo);
      renderResultInFile(velocityCtx, stdTemplate, cfg, fInfo.getShortClassName());
    }
  }

  private void renderResultInFile(VelocityContext ctx, String tplName, ParserConfig cfg, String filename)
  {
    try {
      log.info("Verwende Report-Template: " + tplName);

      Template t = null;
      t = ve.getTemplate(tplName, ENCODING);
      ctx.put("date", new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(new Date()));
      ctx.put("projectName", cfg.getProjectName());
      StringWriter writer = new StringWriter();
      t.merge(ctx, writer);
      byte[] bytes = writer.toString().getBytes(ENCODING);
      save(bytes, cfg, filename);
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    } catch (ResourceNotFoundException e) {
      log.warn("Template kann nicht geladen werden. " + tplName);
    }
  }

  public void save(byte[] data, ParserConfig cfg, String filename)
  {
    try {
      String fName = new File(new File(cfg.getOutputDir()), filename).getAbsolutePath();
      if (fName.endsWith(getFileExtension()) == false) {
        fName = fName + "." + getFileExtension();
      }
      log.info("Schreibe Datei: " + fName);
      File file = new File(fName);
      FileUtils.writeByteArrayToFile(file, data);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Override
  public String getFileExtension()
  {
    return "html";
  }
}