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
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import de.micromata.tpsb.doc.renderer.IParseableResultRenderer;
import de.micromata.tpsb.doc.renderer.IResultRenderer;
import de.micromata.tpsb.doc.renderer.VelocityRenderer;
import de.micromata.tpsb.doc.renderer.XStreamRenderer;
import de.micromata.tpsb.doc.sources.ISourceFileFilter;
import de.micromata.tpsb.doc.sources.ISourceFileRepository;

/**
 * Konfigurationsobjekt zum Parser-Lauf
 * 
 * @author Stefan Stützer (s.stuetzer@micromata.com)
 */
public class ParserConfig
{

  public static final String DEFAULT_WORK_DIR = "target/.tpsb/";

  public static final String DEFAULT_OUTPUT_DIR = "target/tpsb-reports/";

  public static final String DATA_TB_FILENAME = "data_testbuilder";

  public static final String DATA_TC_FILENAME = "data_testreport";

  public static final String DEFAULT_PROJECT_NAME = "Dummy Project";

  private List<ISourceFileRepository> sourceFileRepos;

  /**
   * matches if one of this matches
   */
  private List<ISourceFileFilter> sourceFileFilter;

  private IParseableResultRenderer rawDataRenderer;

  private IResultRenderer reportRenderer;

  private String workDir = DEFAULT_WORK_DIR;

  private String outputDir = DEFAULT_OUTPUT_DIR;

  private String projectName = DEFAULT_PROJECT_NAME;

  /**
   * Create for each test/builder input an individual file
   */
  private boolean generateIndividualFiles = false;

  // TODO: MethodFilter hinzufügen

  public ParserConfig(Builder builder)
  {
    if (StringUtils.isNotBlank(builder.projectName)) {
      this.projectName = builder.projectName;
    }

    if (StringUtils.isNotBlank(builder.workDir)) {
      this.setWorkDir(builder.workDir);
    }

    if (StringUtils.isNotBlank(builder.outputDir)) {
      this.setOutputDir(builder.outputDir);
    }

    // Reportrenderer setzen
    if (builder.reportRenderer == null) {
      this.reportRenderer = new VelocityRenderer(builder.indexTemplate, builder.testTemplate);
    } else {
      this.reportRenderer = builder.reportRenderer;
    }

    // Rohdatenrenderer setzen
    if (builder.rawDataRenderer == null) {
      this.setRawDataRenderer(new XStreamRenderer());
    } else {
      this.rawDataRenderer = builder.rawDataRenderer;
    }

    // SourceFile-Konfig setzen
    this.sourceFileRepos = builder.sourceFileRepos;
    this.sourceFileFilter = builder.sourceFileFilter;
    this.generateIndividualFiles = builder.generateIndividualFiles;
    
    validateRequiredFoldersPresent();
  }

  public void setReportRenderer(IResultRenderer prettyFileRenderer)
  {
    this.reportRenderer = prettyFileRenderer;
  }

  public IResultRenderer getReportRenderer()
  {
    return reportRenderer;
  }

  public void setRawDataRenderer(IParseableResultRenderer rawDataRenderer)
  {
    this.rawDataRenderer = rawDataRenderer;
  }

  public IParseableResultRenderer getRawDataRenderer()
  {
    return rawDataRenderer;
  }

  public void setWorkDir(String workDir)
  {
    this.workDir = workDir;
  }

  public String getWorkDir()
  {
    return workDir;
  }

  public void setOutputDir(String outputDir)
  {
    this.outputDir = outputDir;
  }

  public String getOutputDir()
  {
    return outputDir;
  }

  public void setProjectName(String projectName)
  {
    this.projectName = projectName;
  }

  public String getProjectName()
  {
    return projectName;
  }

  public void setSourceFileRepos(List<ISourceFileRepository> sourceFileRepos)
  {
    this.sourceFileRepos = sourceFileRepos;
  }

  public List<ISourceFileRepository> getSourceFileRepos()
  {
    return sourceFileRepos;
  }

  public void setSourceFileFilter(List<ISourceFileFilter> sourceFileFilter)
  {
    this.sourceFileFilter = sourceFileFilter;
  }

  public List<ISourceFileFilter> getSourceFileFilter()
  {
    return sourceFileFilter;
  }
  
  /**
   * Stellt sicher, dass die für die Testdokumentation benötigten Ordner
   * vorhanden sind. Falls nicht werden diese angelegt.
   */
  private void validateRequiredFoldersPresent() {
      File hiddenTpsbFolder = new File(getWorkDir());
      if (hiddenTpsbFolder.exists() == false || hiddenTpsbFolder.isDirectory() == false) {
          hiddenTpsbFolder.mkdir();
      }
      File tpsbReportFolder = new File(getOutputDir());
      if (tpsbReportFolder.exists() == false || tpsbReportFolder.isDirectory() == false) {
          tpsbReportFolder.mkdir();
      }
  }

  public static class Builder
  {

    private List<ISourceFileRepository> sourceFileRepos = new ArrayList<ISourceFileRepository>();

    private List<ISourceFileFilter> sourceFileFilter = new ArrayList<ISourceFileFilter>();

    private IParseableResultRenderer rawDataRenderer;

    private IResultRenderer reportRenderer;

    private String workDir;

    private String outputDir;

    private String projectName;

    private boolean generateIndividualFiles = false;

    private String indexTemplate = VelocityRenderer.DEFAULT_IDX_TEMPLATE;

    private String testTemplate = VelocityRenderer.DEFAULT_IDX_TEMPLATE;

    public Builder()
    {
    }

    public Builder addSourceFileRespository(ISourceFileRepository repository)
    {
      this.sourceFileRepos.add(repository);
      return this;
    }

    public Builder addSourceFileFilter(ISourceFileFilter filter)
    {
      this.sourceFileFilter.add(filter);
      return this;
    }

    public Builder rawDataRenderer(IParseableResultRenderer renderer)
    {
      this.rawDataRenderer = renderer;
      return this;
    }

    public Builder reportRenderer(IResultRenderer renderer)
    {
      this.reportRenderer = renderer;
      return this;
    }

    public Builder workDir(String workDir)
    {
      this.workDir = workDir;
      return this;
    }

    public Builder outputDir(String outputDir)
    {
      this.outputDir = outputDir;
      return this;
    }

    public Builder projectName(String projectName)
    {
      this.projectName = projectName;
      return this;
    }

    public Builder indexTemplate(String val)
    {
      this.indexTemplate = val;
      return this;
    }

    public Builder testTemplate(String val)
    {
      this.testTemplate = val;
      return this;
    }

    public Builder generateIndividualFiles(boolean val)
    {
      this.generateIndividualFiles = val;
      return this;
    }

    public ParserConfig build()
    {
      if (sourceFileRepos == null || sourceFileRepos.isEmpty() == true) {
        throw new IllegalStateException("Kein Source-File Repository definiert.");
      }
      return new ParserConfig(this);
    }
  }

  public boolean isGenerateIndividualFiles()
  {
    return generateIndividualFiles;
  }

  public void setGenerateIndividualFiles(boolean generateIndividualFiles)
  {
    this.generateIndividualFiles = generateIndividualFiles;
  }

}