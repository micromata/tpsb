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
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import de.micromata.tpsb.doc.parser.FileInfo;
import de.micromata.tpsb.doc.parser.MethodInfo;
import de.micromata.tpsb.doc.parser.ParserResult;
import de.micromata.tpsb.doc.parser.TypeUtils;
import de.micromata.tpsb.doc.renderer.IParseableResultRenderer;
import de.micromata.tpsb.doc.renderer.XStreamParser;
import de.micromata.tpsb.doc.renderer.XStreamRenderer;

/*
 * Set genome.tpsb.reporoot in local-settings where to find the XML files.
 * 
 * @author roger
 * 
 */
public class TpsbEnvironment
{
  private static final Logger log = Logger.getLogger(TpsbEnvironment.class);

  private Map<String, FileInfo> testBuilder = new TreeMap<String, FileInfo>();

  private Map<String, FileInfo> testCases = new TreeMap<String, FileInfo>();

  private List<String> includeRepos = new ArrayList<String>();

  private List<File> projectRoots = new ArrayList<File>();

  /**
   * fqClass & shortClass to fqfile.
   */
  private Map<String, String> testBuilderFiles = new TreeMap<String, String>();

  private static String baseDir;

  private static TpsbEnvironment INSTANCE;

  public static String testFileSuffix = "_test.xml";

  public static String testBuilderSuffix = "_testbuilder.xml";

  public static TpsbEnvironment get()
  {
    if (INSTANCE == null) {
      INSTANCE = new TpsbEnvironment();
    }
    return INSTANCE;
  }

  public TpsbEnvironment()
  {
    if (baseDir != null) {
      includeRepos.add(baseDir);
    }
    init();
  }

  protected void init()
  {

    loadBuilderFileNames();
  }

  public static void reset()
  {
    INSTANCE = null;
  }

  public FileInfo findTestSuite(String name)
  {
    return testCases.get(name);
  }

  public FileInfo getTestSuiteCopy(String name)
  {
    FileInfo mi = testCases.get(name);
    if (mi == null) {
      return null;
    }
    return new FileInfo(mi);
  }

  public MethodInfo getResolvedTestMethod(String name)
  {
    int idx = name.lastIndexOf('.');
    if (idx == -1) {
      return null;
    }
    String className = name.substring(0, idx);
    String methodName = name.substring(idx + 1);
    FileInfo fi = findTestSuite(className);
    if (fi == null) {
      return null;
    }
    MethodInfo mi = fi.findMethodInfo(methodName);
    if (mi == null) {
      return null;
    }
    return TpsbEnvUtils.resolveTestSteps(this, mi);
  }

  public List<String> getTestSuiteNames()
  {
    List<String> ret = new ArrayList<String>();
    File dir = new File(getBaseDir());
    String[] list = dir.list();
    String testSuffix = testFileSuffix;
    if (list != null) {
      for (String f : list) {
        if (f.endsWith(testSuffix) == true) {
          ret.add(f.substring(0, f.length() - testSuffix.length()));
        }
      }
    }
    Collections.sort(ret);
    return ret;
  }

  public Collection<FileInfo> getAllTestSuites()
  {
    return testCases.values();
  }

  public void loadAll()
  {
    loadAllTestBuilderFileInfos();
    loadAllTestFileInfos();
  }

  public void loadAllTestFileInfos()
  {
    if (getBaseDir() == null) {
      return;
    }
    File dir = new File(getBaseDir());
    String[] list = dir.list();
    if (list != null) {
      for (String f : list) {
        if (f.endsWith(testFileSuffix) == true) {
          loadTestCases(new File(dir, f).getAbsolutePath());
        }
      }
    }
  }

  public void loadAllTestBuilderFileInfos()
  {
    if (getBaseDir() == null) {
      return;
    }
    testBuilder.clear();
    loadAllTestBuilderFileInfos(getBaseDir());
    for (String addRepo : includeRepos) {
      loadAllTestBuilderFileInfos(addRepo);
    }
  }

  public void loadAllTestBuilderFileInfos(String dirname)
  {
    File dir = new File(dirname);
    String[] list = dir.list();
    String suffix = testBuilderSuffix;
    if (list != null) {
      for (String f : list) {
        if (f.endsWith(suffix) == false) {
          continue;
        }
        String absFileName = new File(dir, f).getAbsolutePath();

        String cf = f.substring(0, f.length() - suffix.length());
        String sname = TypeUtils.getShortClassName(cf);
        loadTestBuilder(absFileName);
        // testBuilder.put(sname, absFileName);
        // testBuilder.put(cf, absFileName);
      }
    }
  }

  public void loadBuilderFileNames()
  {
    if (getBaseDir() == null) {
      return;
    }
    testBuilderFiles.clear();
    loadBuilderFileNames(getBaseDir());
    for (String n : includeRepos) {
      loadBuilderFileNames(n);
    }
  }

  public void loadBuilderFileNames(String dirn)
  {
    File dir = new File(dirn);
    String[] list = dir.list();
    String suffix = testBuilderSuffix;
    if (list != null) {
      for (String f : list) {
        if (f.endsWith(suffix) == false) {
          continue;
        }
        String absFileName = new File(dir, f).getAbsolutePath();
        String cf = f.substring(0, f.length() - suffix.length());
        testBuilderFiles.put(TypeUtils.getShortClassName(cf), absFileName);
        testBuilderFiles.put(cf, absFileName);
      }
    }
  }

  public FileInfo getDefaultBuilder()
  {
    return findTestBuilder(TpsbEnvUtils.DefaultBaseBuilderName);
  }

  public void store(FileInfo fi, String name)
  {
    IParseableResultRenderer xmlRenderer = new XStreamRenderer();
    File dir = new File(getBaseDir());
    File f = new File(dir, name);
    xmlRenderer.setOutputFilename(f.getAbsolutePath());
    xmlRenderer.renderObject(fi, null);
  }

  public void storeTestBuilder(FileInfo fi)
  {
    store(fi, fi.getClassName() + testBuilderSuffix);
    registerTestBuilder(fi);
  }

  public void storeTestCase(FileInfo fi)
  {
    store(fi, fi.getClassName() + testFileSuffix);
    registerTestCases(fi);
  }

  public void storeBuilders(ParserResult ps)
  {
    if (ps == null) {
      return;
    }
    for (FileInfo fi : ps.getAllFileInfos()) {
      storeTestBuilder(fi);
    }
  }

  public void storeTestCases(ParserResult ps)
  {
    if (ps == null) {
      return;
    }
    for (FileInfo fi : ps.getAllFileInfos()) {
      storeTestCase(fi);
    }
  }

  public FileInfo resolveSuper(FileInfo fi)
  {
    if (fi == null || fi.getSuperClassFileInfo() != null || StringUtils.isEmpty(fi.getSuperClassName()) == true) {
      return fi;
    }

    FileInfo s = findTestBuilder(fi.resolveFqClassInfoFromImports(fi.getSuperClassName()));
    fi.setSuperClassFileInfo(s);
    return fi;
  }

  public File findTestBuilderFile(String fileName)
  {
    File f = new File(new File(getBaseDir()), fileName);
    if (f.exists() == true) {
      return f;
    }
    for (String d : includeRepos) {
      f = new File(new File(d), fileName);
      if (f.exists() == true) {
        return f;
      }
    }
    return null;
  }

  public FileInfo findTestBuilder(String className)
  {
    className = TypeUtils.stripClassEnd(className);
    FileInfo fi = testBuilder.get(className);
    if (fi != null) {
      return fi;
    }
    String fqFile = testBuilderFiles.get(className);
    if (fqFile != null) {
      loadTestBuilder(fqFile);
      fi = testBuilder.get(className);
      if (fi != null) {
        return resolveSuper(fi);
      }
    }
    File f = findTestBuilderFile(className + "_testbuilder.xml");
    if (f == null) {
      return null;
    }
    loadTestBuilder(f.getAbsolutePath());
    return resolveSuper(testBuilder.get(className));
  }

  public void loadTestCases(String fileName)
  {
    Object parsedTest = loadParse(fileName);
    if (parsedTest instanceof ParserResult) {
      loadTestCases((ParserResult) parsedTest);
    } else if (parsedTest instanceof FileInfo) {
      FileInfo fi = (FileInfo) parsedTest;
      registerTestCases(fi);
    } else {
      throw new RuntimeException("Cannot handle parsed file type: " + parsedTest.getClass().getName());
    }
  }

  public FileInfo loadTestBuilder(String fileName)
  {
    Object parsedTest = loadParse(fileName);
    if (parsedTest instanceof ParserResult) {
      loadBuilder((ParserResult) parsedTest);
      return null;
    } else if (parsedTest instanceof FileInfo) {
      FileInfo fi = (FileInfo) parsedTest;
      registerTestBuilder(fi);
      return fi;
    } else {
      throw new RuntimeException("Cannot handle parsed file type: " + parsedTest.getClass().getName());
    }
  }

  public FileInfo getTestBuilder(String className)
  {
    FileInfo fi = testBuilder.get(className);
    if (fi != null) {
      return fi;
    }
    return findTestBuilder(className);
  }

  public Collection<String> getTestBuilderNames()
  {
    return testBuilder.keySet();
  }

  public Collection<String> getFqTestBuilderNames()
  {
    Set<String> ret = new TreeSet<String>();
    for (FileInfo fi : testBuilder.values()) {
      ret.add(fi.getClassName());
    }
    return ret;
  }

  private void loadBuilder(ParserResult pt)
  {
    for (FileInfo fi : pt.getAllFileInfos()) {
      registerTestBuilder(fi);
    }
  }

  private Object loadParse(String fqFileName)
  {
    File f = new File(fqFileName);
    if (f.exists() == false) {
      throw new RuntimeException("File does not exists: " + f.getAbsolutePath());
    }
    return new XStreamParser().parseRawObjectFile(f.getAbsolutePath());
  }

  public void registerTestCases(FileInfo fi)
  {
    testCases.put(fi.getClassName(), fi);
    for (MethodInfo mi : fi.getMethodInfos()) {
      TpsbEnvUtils.resolveTestSteps(this, mi);
    }
  }

  public void registerTestBuilder(FileInfo fi)
  {
    testBuilder.put(fi.getClassName(), fi);
    testBuilder.put(fi.getShortClassName(), fi);
  }

  public void loadTestCases(ParserResult pt)
  {
    for (FileInfo fi : pt.getAllFileInfos()) {
      registerTestCases(fi);
    }
  }

  public void addProjectRoots(File dir)
  {
    projectRoots.add(dir);
  }

  public File lookupFileInProjectRoots(String name)
  {
    for (File pr : projectRoots) {
      File f = new File(pr, name);
      if (f.exists() == true) {
        return f;
      }

    }
    return null;
  }

  public static String getBaseDir()
  {
    if (baseDir != null) {
      return baseDir;
    }
    return baseDir;
  }

  public static void setBaseDir(String tbaseDir)
  {
    String oldBasedir = baseDir;
    baseDir = tbaseDir;
    if (INSTANCE != null) {
      INSTANCE.includeRepos.add(baseDir);
    }
  }

  public Map<String, String> getTestBuilderFiles()
  {
    return testBuilderFiles;
  }

  public void setTestBuilderFiles(Map<String, String> testBuilderFiles)
  {
    this.testBuilderFiles = testBuilderFiles;
  }

  public List<String> getIncludeRepos()
  {
    return includeRepos;
  }

  public void setIncludeRepos(List<String> includeRepos)
  {
    this.includeRepos = new ArrayList<String>();
    this.includeRepos.addAll(includeRepos);
  }
}
