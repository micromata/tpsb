package de.micromata.tpsb.project;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import de.micromata.tpsb.doc.TpsbEnvUtils;
import de.micromata.tpsb.doc.TpsbEnvironment;

/**
 * 
 * @author roger
 * 
 */
public class TpsbProject implements Serializable
{

  private static final long serialVersionUID = 7584377419416224764L;

  /**
   * Name of project
   */
  private String name;

  /**
   * Root name of project
   */
  private String projectPath;

  /**
   * tpsbrepository. by default ${projectPath}/tpsbrepo
   */
  private String repository;

  private List<String> includeRepos = new ArrayList<String>();

  private List<String> sourceDirs = new ArrayList<String>();

  private List<String> runtimeCps = new ArrayList<String>();

  private String sourceTarget;

  /**
   * Name of the imported Projects
   */
  private List<String> importedProjects = new ArrayList<String>();
  /**
   * If set true, no testcases will be parsed.
   */
  private boolean noTestCases = false;

  public TpsbProject()
  {

  }

  public TpsbProject(String name, String projectPath, String repo, List<String> includeRepos, List<String> sourceDirs,
      String sourceTarget)
  {
    this.name = name;
    this.projectPath = projectPath;
    this.repository = repo;
    this.includeRepos = includeRepos;
    this.sourceDirs = sourceDirs;
    this.sourceTarget = sourceTarget;
  }

  public TpsbEnvironment resetEnv()
  {
    TpsbEnvironment.reset();
    TpsbEnvironment.setBaseDir(repository);
    TpsbEnvironment env = TpsbEnvironment.get();
    env.setIncludeRepos(includeRepos);
    return env;
  }

  public void scanSources()
  {
    for (String s : sourceDirs) {
      TpsbEnvUtils.scanSources(s, repository, includeRepos);

    }
    resetEnv().loadAll();
  }

  public List<String> getClassPathDirs()
  {
    List<String> ret = new ArrayList<String>();
    File dir = new File(projectPath);
    File tc = new File(dir, "target/test-classes");
    if (tc.exists() == true) {
      ret.add(tc.getAbsolutePath());
    }
    tc = new File(dir, "target/classes");
    if (tc.exists() == true) {
      ret.add(tc.getAbsolutePath());
    }
    return ret;
  }

  public String getName()
  {
    return name;
  }

  public void setName(String name)
  {
    this.name = name;
  }

  public List<String> getSourceDirs()
  {
    return sourceDirs;
  }

  public void setSourceDirs(List<String> sourceDirs)
  {
    this.sourceDirs = sourceDirs;
  }

  public String getSourceTarget()
  {
    return sourceTarget;
  }

  public void setSourceTarget(String sourceTarget)
  {
    this.sourceTarget = sourceTarget;
  }

  public String getRepository()
  {
    return repository;
  }

  public void setRepository(String repository)
  {
    this.repository = repository;
  }

  public List<String> getIncludeRepos()
  {
    return includeRepos;
  }

  public void setIncludeRepos(List<String> includeRepos)
  {
    this.includeRepos = includeRepos;
  }

  public List<String> getRuntimeCps()
  {
    return runtimeCps;
  }

  public void setRuntimeCps(List<String> runtimeCps)
  {
    this.runtimeCps = runtimeCps;
  }

  public List<String> getImportedProjects()
  {
    return importedProjects;
  }

  public void setImportedProjects(List<String> importedProjects)
  {
    this.importedProjects = importedProjects;
  }

  public String getProjectPath()
  {
    return projectPath;
  }

  public void setProjectPath(String projectPath)
  {
    this.projectPath = projectPath;
  }

  public boolean isNoTestCases()
  {
    return noTestCases;
  }

  public void setNoTestCases(boolean noTestCases)
  {
    this.noTestCases = noTestCases;
  }
}
