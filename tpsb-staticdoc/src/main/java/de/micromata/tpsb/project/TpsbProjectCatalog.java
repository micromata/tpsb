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

package de.micromata.tpsb.project;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.micromata.genome.util.runtime.LocalSettings;

/**
 * The Class TpsbProjectCatalog.
 */
public class TpsbProjectCatalog implements Serializable
{

  /**
   * The Constant serialVersionUID.
   */
  private static final long serialVersionUID = 7015113439871010439L;

  /**
   * The log.
   */
  private static Logger log = Logger.getLogger(TpsbProjectCatalog.class);

  /**
   * The projects.
   */
  Map<String, TpsbProject> projects = new TreeMap<String, TpsbProject>();

  /**
   * The vm index.
   */
  private String vmIndex;

  /**
   * The vm test.
   */
  private String vmTest;

  /**
   * Css file will be copied into target folder for html generation.
   */
  private String vmCssFile;

  private String htmlOutputPath;

  private static TpsbProjectCatalog INSTANCE = null;

  /**
   * Instantiates a new tpsb project catalog.
   */
  public TpsbProjectCatalog()
  {
    INSTANCE = this;
    initFromLocalSettings();
  }

  public static TpsbProjectCatalog getInstance()
  {
    if (INSTANCE == null) {
      new TpsbProjectCatalog();
    }
    return INSTANCE;
  }

  /**
   * Gets the dirs if exists.
   * 
   * @param baseDir the base dir
   * @param subdirs the subdirs
   * @return the dirs if exists
   */
  protected static List<String> getDirsIfExists(File baseDir, String... subdirs)
  {
    List<String> ret = new ArrayList<String>();
    for (String s : subdirs) {
      File f = new File(baseDir, s);
      if (f.exists() == false) {
        continue;
      }
      ret.add(f.getAbsolutePath());
    }
    return ret;
  }

  /**
   * Inits the from local settings.
   */
  public void initFromLocalSettings()
  {
    readGlobalOptions();
    LocalSettings ls = LocalSettings.get();
    List<String> pns = ls.getKeysPrefixWithInfix("genome.tpsb.project", "name");
    for (String k : pns) {

      String key = k + ".name";
      String name = ls.get(key);
      key = k + ".projectroot";
      String projectRoot = ls.get(key);
      if (StringUtils.isBlank(projectRoot) == true) {
        log.warn(" No projektroot defined for: " + key);
        continue;
      }
      File projectRootFile = new File(projectRoot);
      if (projectRootFile.exists() == false) {
        log.warn(" Projectroot does not exists: " + projectRootFile.getAbsolutePath());
        continue;
      }
      key = k + ".srcprojectroots";
      String sources = ls.get(key);
      List<String> sourceList = new ArrayList<String>();
      if (StringUtils.isBlank(sources) == false) {
        List<String> tl = Arrays.asList(StringUtils.split(sources, ','));
        for (String s : tl) {
          File srcDir = new File(s);
          if (srcDir.exists() == false) {
            log.warn("Source dir defined in localsettings doesn't exist: " + srcDir.getAbsolutePath());
            continue;
          }
          sourceList.add(s);
        }
      } else {
        sourceList = getDirsIfExists(projectRootFile, "src/test/java", "src/main/java");
      }
      key = k + ".tpsbrepo";
      String repos = ls.get(key);
      if (StringUtils.isBlank(repos) == true) {
        repos = new File(projectRootFile, "tpsbrepo").getAbsolutePath();
      }
      List<String> repoLista = Arrays.asList(StringUtils.split(repos, ','));
      List<String> repoList = new ArrayList<String>();
      repoList.addAll(repoLista);
      for (String repo : repoList) {
        File repoFile = new File(repo);
        if (repoFile.exists() == false) {
          log.warn("Repo defined in localsettings doesn't exist: " + repoFile.getAbsolutePath());
          continue;
        }
      }

      if (repoList.isEmpty() == true) {
        log.warn("No Repositories are defined");
        continue;
      }

      key = k + ".srcgentarget";
      String sourcegen = ls.get(key);
      if (StringUtils.isBlank(sourcegen) == true) {
        sourcegen = new File(projectRootFile, "src/test/java").getAbsolutePath();
      }

      key = k + ".imports";
      String imports = ls.get(key);

      List<String> importList = new ArrayList<String>();
      if (StringUtils.isNotBlank(imports) == true) {
        importList = Arrays.asList(StringUtils.split(imports, ','));
      }

      String first = repoList.get(0);
      repoList.remove(0);
      TpsbProject project = new TpsbProject(name, projectRoot, first, repoList, sourceList, sourcegen);
      project.getImportedProjects().addAll(importList);

      key = k + ".noTestCases";
      project.setNoTestCases(ls.getBooleanValue(key, false));

      key = k + ".addcp";
      String adcp = ls.get(key);
      if (StringUtils.isNotBlank(adcp) == true) {
        List<String> addcp = Arrays.asList(StringUtils.split(adcp, ','));
        for (String a : addcp) {
          a = StringUtils.trim(a);
          File srcDir = new File(a);
          if (srcDir.exists() == false) {
            log.warn("Cp dir or jar defined in localsettings doesn't exist: " + srcDir.getAbsolutePath());
            continue;
          }
          project.getRuntimeCps().add(a);
        }
      }
      projects.put(name, project);
    }
    resolveProjectDeps();
  }

  private void readGlobalOptions()
  {
    LocalSettings ls = LocalSettings.get();
    this.vmTest = ls.get("genome.tpsb.vmTest");
    this.vmIndex = ls.get("genome.tpsb.vmIndex");
    this.vmCssFile = ls.get("genome.tpsb.vmCssFile");
    this.htmlOutputPath = ls.get("genome.tpsb.htmlOutputPath");
  }

  /**
   * Import project.
   * 
   * @param target the target
   * @param source the source
   */
  protected void importProject(TpsbProject target, TpsbProject source)
  {
    for (String imProject : source.getImportedProjects()) {
      TpsbProject s = projects.get(imProject);
      if (s == null) {
        log.warn("Cannot find import project: " + imProject);
        continue;
      }
      importProject(target, s);
    }
    for (String inclRepo : source.getIncludeRepos()) {
      if (target.getIncludeRepos().contains(inclRepo) == false) {
        target.getIncludeRepos().add(inclRepo);
      }
    }
    if (target.getIncludeRepos().contains(source.getRepository()) == false) {
      target.getIncludeRepos().add(source.getRepository());
    }
    for (String rcp : getDirsIfExists(new File(source.getProjectPath()), "target/test-classes", "target/classes",
        "src/main/webapp/WEB-INF/clases")) {
      if (target.getRuntimeCps().contains(rcp) == false) {
        target.getRuntimeCps().add(rcp);
      }
    }
    for (String rcp : source.getRuntimeCps()) {
      if (target.getRuntimeCps().contains(rcp) == false) {
        target.getRuntimeCps().add(rcp);
      }
    }
  }

  /**
   * Resolve project deps.
   */
  protected void resolveProjectDeps()
  {
    for (TpsbProject project : projects.values()) {
      for (String imProject : project.getImportedProjects()) {
        TpsbProject s = projects.get(imProject);
        if (s == null) {
          log.warn("Cannot find import project: " + imProject);
          continue;
        }
        importProject(project, s);
      }

    }
  }

  public TpsbProject getDefaultProject()
  {
    Iterator<String> it = projects.keySet().iterator();
    if (it.hasNext() == true) {
      return getProject(it.next());
    }
    return null;
  }

  public Collection<String> getProjectNames()
  {
    return projects.keySet();
  }

  public List<String> getProjectCps()
  {
    List<String> ret = new ArrayList<String>();
    for (TpsbProject project : projects.values()) {
      ret.addAll(project.getClassPathDirs());
    }
    return ret;
  }

  /**
   * Gets the project.
   * 
   * @param name the name
   * @return the project
   */
  public TpsbProject getProject(String name)
  {
    return projects.get(name);
  }

  public String getVmIndex()
  {
    return vmIndex;
  }

  public void setVmIndex(String vmIndex)
  {
    this.vmIndex = vmIndex;
  }

  public String getVmTest()
  {
    return vmTest;
  }

  public void setVmTest(String vmTest)
  {
    this.vmTest = vmTest;
  }

  public String getHtmlOutputPath()
  {
    return htmlOutputPath;
  }

  public void setHtmlOutputPath(String htmlOutputPath)
  {
    this.htmlOutputPath = htmlOutputPath;
  }

  public String getVmCssFile()
  {
    return vmCssFile;
  }

  public void setVmCssFile(String vmCssFile)
  {
    this.vmCssFile = vmCssFile;
  }
}
