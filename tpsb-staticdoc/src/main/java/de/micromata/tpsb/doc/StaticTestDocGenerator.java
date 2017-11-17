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

package de.micromata.tpsb.doc;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.CharEncoding;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.velocity.runtime.resource.loader.StringResourceLoader;
import org.apache.velocity.runtime.resource.util.StringResourceRepository;
import org.apache.velocity.runtime.resource.util.StringResourceRepositoryImpl;

import de.micromata.genome.tpsb.annotations.TpsbApplication;
import de.micromata.genome.tpsb.annotations.TpsbBuilder;
import de.micromata.genome.tpsb.annotations.TpsbTestSuite;
import de.micromata.tpsb.doc.ParserConfig.Builder;
import de.micromata.tpsb.doc.parser.ParserResult;
import de.micromata.tpsb.doc.parser.japa.JapaParser;
import de.micromata.tpsb.doc.renderer.IParseableResultRenderer;
import de.micromata.tpsb.doc.renderer.IResultParser;
import de.micromata.tpsb.doc.renderer.IResultRenderer;
import de.micromata.tpsb.doc.sources.AnnotationSourceFileFilter;
import de.micromata.tpsb.doc.sources.FileSystemSourceFileRepository;
import de.micromata.tpsb.project.TpsbProject;
import de.micromata.tpsb.project.TpsbProjectCatalog;

/**
 * Main, welches das Parsen und Rendern von Testbuildern und Unit-Tests vornimmt
 * 
 * @author Stefan Stützer (s.stuetzer@micromata.com), Roger Kommer
 */
public class StaticTestDocGenerator
{

  static Logger log = Logger.getLogger(StaticTestDocGenerator.class);

  /** Testbuilder Config */
  private ParserConfig builderCfg;

  private ParserConfig testCaseCfg;

  private JapaParser javaParser = new JapaParser();

  public StaticTestDocGenerator(ParserConfig builderCfg, ParserConfig testCaseCfg)
  {
    super();
    this.builderCfg = builderCfg;
    this.testCaseCfg = testCaseCfg;
  }

  public void parseTestBuilders()
  {
    ParserContext ctx = new ParserContext(builderCfg);
    javaParser.parseTestBuilders(ctx);
    renderRawResult(ctx, builderCfg, ParserConfig.DATA_TB_FILENAME, true);
  }

  public void parseTestCases()
  {
    // Rohdaten-Datei der TestBuilder einlesen
    if (builderCfg.getRawDataRenderer() instanceof IParseableResultRenderer == false) {
      log.error("Rohdatendatei der TestBuilder kann nicht geparst werden.");
      return;
    }
    ParserContext ctx;
    if (builderCfg.isGenerateIndividualFiles() == false) {
      IParseableResultRenderer tbRenderer = builderCfg.getRawDataRenderer();
      IResultParser rawDataParser = tbRenderer.getParser();
      String testBuilderFileName = getRawDataFileName(builderCfg, ParserConfig.DATA_TB_FILENAME);
      ParserResult tbParserResult = rawDataParser.parseRawFile(testBuilderFileName);
      ctx = new ParserContext(testCaseCfg, tbParserResult);
    } else {
      ctx = new ParserContext(testCaseCfg);
    }

    this.javaParser.parseTestCases(ctx);
    renderRawResult(ctx, testCaseCfg, ParserConfig.DATA_TC_FILENAME, false);
    renderTestReport(ctx, testCaseCfg);
  }

  private void renderRawResult(ParserContext ctx, ParserConfig cfg, String filename, boolean isBuilder)
  {
    IParseableResultRenderer rawDataRenderer = cfg.getRawDataRenderer();
    if (cfg.isGenerateIndividualFiles() == true) {
      if (isBuilder == true) {
        TpsbEnvironment.get().storeBuilders(ctx.getCurrentParserResult());
      } else {
        TpsbEnvironment.get().storeTestCases(ctx.getCurrentParserResult());
      }
    } else {
      String rawDataFileName = getRawDataFileName(cfg, filename);
      rawDataRenderer.setOutputFilename(rawDataFileName);
      rawDataRenderer.renderResult(ctx, cfg);
    }
  }

  private void renderTestReport(ParserContext ctx, ParserConfig cfg)
  {
    if (cfg.getReportRenderer() == null) {
      log.info("Kein Report-Renderer definiert.");
      return;
    }
    IResultRenderer reportRenderer = cfg.getReportRenderer();
    reportRenderer.renderResult(ctx, cfg);
  }

  private String getRawDataFileName(ParserConfig cfg, String rawDataFilename)
  {
    String fileExtension = cfg.getRawDataRenderer().getFileExtension();
    if (rawDataFilename.endsWith(fileExtension) == false) {
      rawDataFilename = rawDataFilename + "." + fileExtension;
    }
    return cfg.getWorkDir() + rawDataFilename;
  }

  public void setBuilderCfg(ParserConfig builderCfg)
  {
    this.builderCfg = builderCfg;
  }

  public ParserConfig getBuilderCfg()
  {
    return builderCfg;
  }

  public void setTestCaseCfg(ParserConfig testCaseCfg)
  {
    this.testCaseCfg = testCaseCfg;
  }

  public ParserConfig getTestCaseCfg()
  {
    return testCaseCfg;
  }

  static String getArgumentOption(Iterator<String> it, String current, String... variants)
  {
    boolean found = false;
    for (String var : variants) {
      if (var.equals(current) == true) {
        found = true;
        break;
      }
    }
    if (found == false) {
      return null;
    }
    if (it.hasNext() == false) {
      throw new RuntimeException(variants[0] + " need argument");
    }
    String v = it.next();
    if (v.startsWith("-") == true) {
      throw new RuntimeException(variants[0] + " need argument");
    }
    return v;
  }

  public static void main(String[] args)
  {

    ParserConfig.Builder bCfg = new ParserConfig.Builder();
    ParserConfig.Builder tCfg = new ParserConfig.Builder();
    tCfg.generateIndividualFiles(true);
    bCfg.generateIndividualFiles(true);
    List<String> la = Arrays.asList(args);
    Iterator<String> it = la.iterator();
    boolean baseDirSet = false;
    boolean ignoreLocalSettings = false;
    List<String> addRepos = new ArrayList<String>();
    StringResourceLoader
        .setRepository(StringResourceLoader.REPOSITORY_NAME_DEFAULT, new StringResourceRepositoryImpl());
    try {
      while (it.hasNext()) {
        String arg = it.next();
        String value = null;

        if ((value = getArgumentOption(it, arg, "--project-root", "-pr")) != null) {
          File f = new File(value);
          if (f.exists() == false) {
            System.err.print("project root doesn't exists: " + f.getAbsolutePath());
            continue;
          }
          TpsbEnvironment.get().addProjectRoots(f);
          File ts = new File(f, "src/test");
          if (ts.exists() == true) {
            tCfg.addSourceFileRespository(new FileSystemSourceFileRepository(ts.getAbsolutePath()));
            bCfg.addSourceFileRespository(new FileSystemSourceFileRepository(ts.getAbsolutePath()));
          }
          continue;
        }
        if ((value = getArgumentOption(it, arg, "--test-input", "-ti")) != null) {
          File f = new File(value);
          if (f.exists() == false) {
            System.err.print("test-input doesn't exists: " + f.getAbsolutePath());
          }
          tCfg.addSourceFileRespository(new FileSystemSourceFileRepository(value));
          bCfg.addSourceFileRespository(new FileSystemSourceFileRepository(value));
          continue;
        }
        if ((value = getArgumentOption(it, arg, "--output-path", "-op")) != null) {
          if (baseDirSet == false) {
            tCfg.outputDir(value);
            bCfg.outputDir(value);
            TpsbEnvironment.setBaseDir(value);
            baseDirSet = true;
          } else {
            addRepos.add(value);
          }
          continue;
        }
        if ((value = getArgumentOption(it, arg, "--index-vmtemplate", "-ivt")) != null) {
          try {
            String content = FileUtils.readFileToString(new File(value), CharEncoding.UTF_8);
            StringResourceRepository repo = StringResourceLoader.getRepository();
            repo.putStringResource("customIndexTemplate", content, CharEncoding.UTF_8);
            tCfg.indexTemplate("customIndexTemplate");
          } catch (IOException ex) {
            throw new RuntimeException(
                "Cannot load file " + new File(value).getAbsolutePath() + ": " + ex.getMessage(), ex);
          }
          continue;
        }
        if ((value = getArgumentOption(it, arg, "--test-vmtemplate", "-tvt")) != null) {
          try {
            String content = FileUtils.readFileToString(new File(value), CharEncoding.UTF_8);
            StringResourceRepository repo = StringResourceLoader.getRepository();
            repo.putStringResource("customTestTemplate", content, CharEncoding.UTF_8);
            tCfg.testTemplate("customTestTemplate");
          } catch (IOException ex) {
            throw new RuntimeException(
                "Cannot load file " + new File(value).getAbsolutePath() + ": " + ex.getMessage(), ex);
          }
          continue;
        }
        if (arg.equals("--singlexml") == true) {
          tCfg.generateIndividualFiles(false);
          bCfg.generateIndividualFiles(false);
        } else if (arg.equals("--ignore-local-settings") == true) {
          ignoreLocalSettings = true;
          continue;
        }
      }
    } catch (RuntimeException ex) {
      System.err.print(ex.getMessage());
      return;
    }
    if (ignoreLocalSettings == false) {
      readLocalSettings(bCfg, tCfg);
    }
    bCfg// .addSourceFileFilter(new MatcherSourceFileFilter("*Builder,*App,*builder")) //
    .addSourceFileFilter(new AnnotationSourceFileFilter(TpsbBuilder.class)) //
        .addSourceFileFilter(new AnnotationSourceFileFilter(TpsbApplication.class)) //
    ;
    tCfg// .addSourceFileFilter(new MatcherSourceFileFilter("*Test,*TestCase")) //
    .addSourceFileFilter(new AnnotationSourceFileFilter(TpsbTestSuite.class)) //
    ;

    StaticTestDocGenerator docGenerator = new StaticTestDocGenerator(bCfg.build(), tCfg.build());
    TpsbEnvironment env = TpsbEnvironment.get();
    if (addRepos.isEmpty() == false) {
      env.setIncludeRepos(addRepos);
    }
    docGenerator.parseTestBuilders();
    docGenerator.parseTestCases();
  }

  private static String getUtf8Content(File file)
  {
    try {
      String content = FileUtils.readFileToString(file, CharEncoding.UTF_8);
      return content;
    } catch (IOException ex) {
      throw new RuntimeException("Cannot read file " + file.getAbsolutePath());

    }
  }

  private static void readLocalSettings(Builder bCfg, Builder tCfg)
  {
    TpsbProjectCatalog catalog = new TpsbProjectCatalog();
    //    catalog.initFromLocalSettings();
    if (StringUtils.isNotEmpty(catalog.getHtmlOutputPath()) == true) {
      tCfg.outputDir(catalog.getHtmlOutputPath());
      bCfg.outputDir(catalog.getHtmlOutputPath());
      TpsbEnvironment.setBaseDir(catalog.getHtmlOutputPath());
    }
    if (StringUtils.isNotEmpty(catalog.getVmIndex()) == true) {
      StringResourceRepository repo = StringResourceLoader.getRepository();
      repo.putStringResource("customIndexTemplate", getUtf8Content(new File(catalog.getVmIndex())), CharEncoding.UTF_8);
      tCfg.indexTemplate("customIndexTemplate");
    }
    if (StringUtils.isNotEmpty(catalog.getVmTest()) == true) {
      String content = getUtf8Content(new File(catalog.getVmTest()));
      StringResourceRepository repo = StringResourceLoader.getRepository();
      repo.putStringResource("customTestTemplate", content, CharEncoding.UTF_8);
      tCfg.testTemplate("customTestTemplate");
    }
    if (StringUtils.isNotEmpty(catalog.getVmCssFile()) == true) {
      File sf = new File(catalog.getVmCssFile());
      if (sf.exists() == false) {
        log.warn("vmCssFile does not exists: " + sf.getAbsolutePath());
      } else {
        File targetFile = new File(new File(catalog.getHtmlOutputPath()), sf.getName());
        try {
          FileUtils.copyFile(sf, targetFile);
        } catch (IOException ex) {
          log.warn(
              "Cannot copy file " + sf.getAbsolutePath() + " to " + targetFile.getAbsolutePath() + ": "
                  + ex.getMessage(), ex);
        }
      }

    }
    Set<String> importedProjects = new HashSet<String>();
    for (String pn : catalog.getProjectNames()) {
      TpsbProject project = catalog.getProject(pn);
      importedProjects.addAll(project.getImportedProjects());
      File f = new File(project.getProjectPath());
      TpsbEnvironment.get().addProjectRoots(f);
      if (project.getSourceDirs().isEmpty() == false) {
        for (String sourceDir : project.getSourceDirs()) {
          if (project.isNoTestCases() == false) {
            tCfg.addSourceFileRespository(new FileSystemSourceFileRepository(sourceDir));
          }
          bCfg.addSourceFileRespository(new FileSystemSourceFileRepository(sourceDir));
        }
      } else {
        File ts = new File(f, "src/test");
        if (ts.exists() == true) {
          if (project.isNoTestCases() == false) {
            tCfg.addSourceFileRespository(new FileSystemSourceFileRepository(ts.getAbsolutePath()));
          }
          bCfg.addSourceFileRespository(new FileSystemSourceFileRepository(ts.getAbsolutePath()));
        } else if (f.exists() == true) {
          if (project.isNoTestCases() == false) {
            tCfg.addSourceFileRespository(new FileSystemSourceFileRepository(f.getAbsolutePath()));
          }
          bCfg.addSourceFileRespository(new FileSystemSourceFileRepository(f.getAbsolutePath()));
        } else {
          log.error("Cannot find directory: " + ts.getAbsolutePath() + " for TPSB project " + pn);
        }
      }
    }
    log.info("TPSB Projects: " + catalog.getProjectNames());
  }
}