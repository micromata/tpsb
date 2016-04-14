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

package de.micromata.genome.tpsb.builder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.CharEncoding;
import org.apache.commons.lang.StringUtils;

import de.micromata.genome.util.runtime.LocalSettings;
import de.micromata.genome.util.runtime.RuntimeIOException;

/**
 * Context for loading scenarios.
 * 
 * @author Roger Rene Kommer (r.kommer.extern@micromata.de)
 * 
 */
public class ScenarioLoaderContext
{
  private File baseDir;

  private String relPath;

  private String suffix = "";

  public ScenarioLoaderContext()
  {
  }

  public ScenarioLoaderContext(String relPath, String suffix)
  {
    this.relPath = relPath;
    this.suffix = suffix;

  }

  public String getScenarioFileName()
  {
    return resolveBaseDir().getAbsolutePath();
  }

  public String getScenarioShortFileName()
  {
    return resolveBaseDir().getName();
  }

  protected File resolveBaseDir()
  {
    File relpathFile = new File(relPath);
    if (relpathFile.isAbsolute() == true) {
      return relpathFile;
    }
    String genomeHome = LocalSettings.get().getGenomeHome();
    if (StringUtils.isBlank(genomeHome) == true) {
      genomeHome = ".";
    }
    File tbaseDir = new File(new File(genomeHome).getAbsoluteFile(), relPath);
    return tbaseDir;
  }

  public File getBaseDir()
  {
    if (baseDir == null) {
      baseDir = resolveBaseDir();
    }
    return baseDir;
  }

  public String getBaseDirName()
  {
    return getBaseDir().getAbsolutePath();
  }

  public File getScenarioFile(String id)
  {
    return getScenarioFile(id, suffix);
  }

  public File getScenarioFile(String id, String suffix)
  {
    File file = new File(getBaseDir(), id + suffix);
    if (file.exists() == false) {
      throw new IllegalArgumentException("Scenario does not exists: " + id + "; " + file.getAbsolutePath());
    }
    return file;
  }

  /**
   * Load a scenario property file.
   * 
   * @param id
   * @return
   */
  public Map<String, String> loadLocalPropertiesScenario(String id)
  {
    File file = getScenarioFile(id, ".properties");

    Map<String, String> map = new HashMap<String, String>();
    try {
      Properties props = new Properties();
      FileInputStream fin = new FileInputStream(file);
      props.load(fin);
      for (Object k : props.keySet()) {
        String key = (String) k;
        map.put(key, props.getProperty((String) k));
      }
      return map;
    } catch (IOException ex) {
      throw new RuntimeIOException(ex);
    }

  }

  public String getScenarioFileContent(String file, String encoding, int maxLength)
  {
    try {
      InputStream instr = openInputStream(file);
      InputStreamReader sr = new InputStreamReader(instr, encoding);
      StringBuilder ret = new StringBuilder(maxLength);
      int ch;
      int count = 0;
      while ((ch = sr.read()) != -1) {
        ret.append((char) ch);
        ++count;
        if (count > maxLength) {
          break;
        }
      }
      return ret.toString();
    } catch (RuntimeException ex) {
      throw ex;
    } catch (Exception ex) {
      throw new RuntimeException(ex);
    }
  }

  public String loadScenarioPropertyFile(String id)
  {
    File file = getScenarioFile(id, ".properties");
    try {
      String ret = FileUtils.readFileToString(file, CharEncoding.ISO_8859_1);
      return ret;
    } catch (IOException ex) {
      throw new IllegalArgumentException("Cannot load scenario file: " + file.getAbsolutePath() + ": " + ex.getMessage(), ex);
    }
  }

  public String loadScenarioTextFile(String id)
  {
    File file = getScenarioFile(id, ".txt");
    try {
      String ret = FileUtils.readFileToString(file, CharEncoding.UTF_8);
      return ret;
    } catch (IOException ex) {
      throw new IllegalArgumentException("Cannot load scenario file: " + file.getAbsolutePath() + ": " + ex.getMessage(), ex);
    }
  }

  public InputStream openInputStream(String fileName)
  {
    File file = new File(getBaseDir(), fileName);
    if (file.exists() == false) {
      throw new IllegalArgumentException("Scenario file does not exists: " + fileName + "; " + file.getAbsolutePath());
    }
    try {
      return new FileInputStream(file);
    } catch (FileNotFoundException ex) {
      throw new IllegalArgumentException("Scenario file does not exists: " + fileName + "; " + file.getAbsolutePath(), ex);
    }
  }

  /**
   * load all matching scenario files.
   * 
   * @return id (file name withouth path and suffix) -> testfile.
   */
  public Map<String, File> scenarioFiles()
  {
    File dir = getBaseDir();
    Map<String, File> ret = new TreeMap<String, File>();
    for (File file : dir.listFiles()) {
      if (file.isFile() == false) {
        continue;
      }
      if (file.getName().endsWith(suffix) == false) {
        continue;
      }
      String name = file.getName();
      String id = name.substring(0, name.length() - suffix.length());
      ret.put(id, file);
    }
    return ret;

  }

  public void setBaseDir(File baseDir)
  {
    this.baseDir = baseDir;
  }

}
