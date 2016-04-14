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

package de.micromata.tpsb.doc.parser;


import japa.parser.ast.comments.Comment;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.CharEncoding;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.micromata.genome.tpsb.builder.PreTextScenarioRenderer;
import de.micromata.genome.tpsb.builder.ScenarioDescriber;
import de.micromata.genome.tpsb.builder.ScenarioRenderer;
import de.micromata.genome.util.types.Pair;
import de.micromata.tpsb.doc.TpsbEnvUtils;
import de.micromata.tpsb.doc.TpsbEnvironment;
import de.micromata.tpsb.doc.renderer.RendererClassUtils;

/**
 * Utility Methoden zum Umgang mit JavaDoc Kommentaren
 * 
 * @author Stefan Stützer (s.stuetzer@micromata.com)
 */
public class JavaDocUtil
{
  static Logger log = Logger.getLogger(JavaDocUtil.class);

  static List<String> tupelTags = Arrays.asList("@author", "@since", "@see", "@return");

  private static final String LINE_FEED = "\n";

  public static JavaDocInfo parseJavaDoc(Comment jDoc)
  {

    if (jDoc == null) {
      return new JavaDocInfo();
    }
    return parseJavaDoc(cleanupJavaDoc(jDoc.getContent()));
  }

  public static JavaDocInfo parseJavaDoc(String cleanedContent)
  {
    JavaDocInfo javaDocInfo = new JavaDocInfo();
    Pair<String, String> titleAndDesc = extractTitleAndDescription(cleanedContent);
    if (StringUtils.isNotBlank(titleAndDesc.getFirst())) {
      javaDocInfo.setTitle(titleAndDesc.getFirst());
    }
    if (StringUtils.isNotBlank(titleAndDesc.getSecond())) {
      javaDocInfo.setDescription(titleAndDesc.getSecond());
    }

    Map<String, List<Pair<String, String>>> tags = extractTags(cleanedContent);
    if (tags != null && tags.size() != 0) {
      javaDocInfo.setTags(tags);
    }
    return javaDocInfo;
  }

  /**
   * Extrahiert Titel und Beschreibung aus dem JavaDoc Kommentar
   * 
   * @param cleanedContent bereinigter Content
   * @return Tupel Titel,Beschreibung
   */
  private static Pair<String, String> extractTitleAndDescription(String cleanedContent)
  {
    String[] lines = StringUtils.split(cleanedContent, LINE_FEED);
    if (lines == null) {
      return new Pair<String, String>();
    }
    List<String> titleAndDesc = new ArrayList<String>();
    for (String line : lines) {
      String trimmedLine = line.trim();
      if (trimmedLine.startsWith("@") == true) {
        break;
      }
      titleAndDesc.add(trimmedLine);
    }
    Pair<String, String> ret = new Pair<String, String>();
    boolean titleFinished = false;
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < titleAndDesc.size(); i++) {
      String s = titleAndDesc.get(i);
      if (StringUtils.isBlank(s) == true) {
        if (sb.length() == 0) {
          continue;
        }
        if (titleFinished == false) {
          ret.setFirst(sb.toString());
          titleFinished = true;
          sb = new StringBuilder();
          continue;
        }
        if (sb.length() > 0) {
          sb.append(s);
        }
      } else {
        if (sb.length() > 0) {
          sb.append("\n");
        }
        sb.append(s);
      }
    }
    if (sb.length() > 0) {
      if (titleFinished == false) {
        ret.setFirst(sb.toString());
      } else {
        ret.setSecond(sb.toString());
      }
    }
    return ret;
  }

  /**
   * Extrahiert alle Tags aus dem JavaDoc
   * 
   * @param cleanedContent bereinigter Inhalt
   * @return Tupel Tagname auf Liste von Key-Value Paaren
   */
  private static Map<String, List<Pair<String, String>>> extractTags(String cleanedContent)
  {
    Map<String, List<Pair<String, String>>> tagMap = new HashMap<String, List<Pair<String, String>>>();
    String[] lines = StringUtils.split(cleanedContent, LINE_FEED);
    if (lines == null) {
      return tagMap;
    }
    // Tag-Bereich aus Javadoc extrahieren
    List<String> tagContent = new ArrayList<String>();
    boolean found = false;
    for (String line : lines) {
      String trimmedLine = line.trim();
      if (trimmedLine.startsWith("@") == false && found == false) {
        continue;
      }
      tagContent.add(trimmedLine);
      found = true;
    }

    for (int i = 0; i < tagContent.size();) {
      String tag = tagContent.get(i++);
      if (StringUtils.trimToNull(tag) == null) {
        continue;
      }
      // Prüfen ob nächste Zeile noch zum aktuellen Tag gehört
      for (int j = i; j < tagContent.size(); j++) {
        i = j;
        String t = tagContent.get(j);
        if (t.startsWith("@") == false && StringUtils.isNotBlank(t) == true) {
          tag += " " + t;
        } else {
          break;
        }
      }
      parseTag(tag, tagMap);
    }
    return tagMap;
  }

  /**
   * Parst einen JavaDoc Tag
   * 
   * @param tag der Tag-String
   * @param tagMap die zu befüllende Tag-Map
   */
  private static void parseTag(String tag, Map<String, List<Pair<String, String>>> tagMap)
  {
    final String TUPEL_PATTERN = "^(%s)(.*)$";
    final String TRIPEL_PATTERN = "^(@\\S*)\\s(\\S*)(.*)$";
    int idx = StringUtils.indexOf(tag, " ");
    String tagName = StringUtils.substring(tag, 0, idx);

    String pattern = tupelTags.contains(tagName) ? TUPEL_PATTERN : TRIPEL_PATTERN;
    Pattern p = Pattern.compile(String.format(pattern, tagName), Pattern.DOTALL);
    Matcher matcher = p.matcher(tag);
    String key = null;
    String val = null;
    if (matcher.matches() == true) {
      switch (matcher.groupCount()) {
        case 2:
          val = matcher.group(2).trim();
          break;
        case 3:
          key = matcher.group(2).trim();
          val = matcher.group(3).trim();
          break;
        default:
          System.out.println("Kein Match");
      }
      if (tagMap.get(tagName) == null) {
        tagMap.put(tagName, new ArrayList<Pair<String, String>>());
      }
      tagMap.get(tagName).add(Pair.make(key, val));
    }
  }

  /**
   * Bereinigt den JavaDoc von * und Tabs und Leerzeichen
   * 
   * @param javaDoc Kommentar
   * @return bereinigter Kommentar
   */
  public static String cleanupJavaDoc(String javaDoc)
  {
    if (StringUtils.isBlank(javaDoc)) {
      return javaDoc;
    }
    javaDoc = javaDoc.replaceAll("\\*", "").replaceAll("\t", "").trim();
    return javaDoc;
  }

  public static List<String> getScenarioFileNames(String projectRoot, AnnotationInfo ai)
  {
    List<String> ret = new ArrayList<String>();
    String pattern = (String) ai.getParams().get("filePattern");
    if (StringUtils.isBlank(pattern) == true) {
      return ret;
    }
    // TODO only support one argument
    pattern = StringUtils.replace(pattern, "${0}", "*");
    int idx = pattern.lastIndexOf('/');
    if (idx == -1) {
      return ret;
    }
    String dirpart = pattern.substring(0, idx);
    String filepart = pattern.substring(idx + 1);
    idx = filepart.indexOf('*');
    if (idx == -1) {
      return ret;
    }
    String fileprefix = filepart.substring(0, idx);
    String filepostfix = filepart.substring(idx + 1);

    File root = new File(projectRoot);
    File parent = new File(root, dirpart);
    for (File f : parent.listFiles()) {
      if (f.getName().startsWith(fileprefix) == false) {
        continue;
      }
      if (f.getName().endsWith(filepostfix) == false) {
        continue;
      }
      String scenid = f.getName().substring(fileprefix.length());
      scenid = scenid.substring(0, scenid.length() - filepostfix.length());
      ret.add(scenid);

    }
    return ret;
  }

  public static String getScenarioSuiteFileContent(String projectRoot, TestStepInfo stepInfo, AnnotationInfo annotation)
  {
    StringBuilder sb = new StringBuilder();
    String fileName = (String) annotation.getParams().get("filePattern");
    if (fileName == null) {
      return null;
    }
    fileName = TypeUtils.unqoteString(fileName);
    String dirPart = StringUtils.substringBefore(fileName, "${0}");
    String suffixPart = StringUtils.substringAfter(fileName, "${0}");

    File dir = lookupFileOrDir(projectRoot, dirPart);
    if (dir == null || dir.isDirectory() == false) {
      return sb.toString();
    }
    File[] files = dir.listFiles();
    if (files == null || files.length == 0) {
      return sb.toString();
    }
    for (File f : files) {
      if (f.getName().endsWith(suffixPart) == false) {
        continue;
      }
      try {
        String content = FileUtils.readFileToString(f);
        AnnotationInfo scenarioAnot = TpsbEnvUtils.getAnnotation(stepInfo.getTestBuilderMethod(), "TpsbScenarioFile");
        content = renderScenarioFile(f, scenarioAnot, content);
        sb.append("\n\n===================================================================================\n")//
            .append("<h3>Testscenario: ").append(StringUtils.substringBefore(f.getName(), suffixPart)).append("</h3>\n\n")//
            .append(content);

      } catch (IOException ex) {
        log.warn("Scanrio file can't be read: " + f.getAbsolutePath() + "; " + ex.getMessage(), ex);
      }
    }
    return sb.toString();
  }

  private static String renderScenarioFile(File file, AnnotationInfo scenarioFilesAnotation, String content)
  {
    ScenarioRenderer renderer = null;
    if (scenarioFilesAnotation != null) {
      Object rendererClass = scenarioFilesAnotation.getParams().get("scenarioRendererClass");
      if (rendererClass != null) {
        renderer = RendererClassUtils.loadClass((String) rendererClass, ScenarioRenderer.class);
      }
    }
    if (renderer == null) {
      renderer = new PreTextScenarioRenderer();
    }
    try {
      ScenarioDescriber describer = new ScenarioDescriber();
      renderer.renderScenarioContent(file, content, describer);

      return describer.toString();
    } catch (NoClassDefFoundError ex) {
      ex.printStackTrace();
      throw ex;
    }

  }

  public static String getScenarioInfo(String projectRoot, TestStepInfo stepInfo, AnnotationInfo annotation)
  {
    MethodInfo testMethod = stepInfo.getTestMethod();
    AnnotationInfo scenarioFilesAnotation = TpsbEnvUtils.getAnnotation(testMethod, "TpsbScenarioSuiteFiles");
    if (scenarioFilesAnotation != null) {
      return getScenarioSuiteFileContent(projectRoot, stepInfo, scenarioFilesAnotation);
    }
    Map<String, String> replMap = new HashMap<String, String>();
    for (int i = 0; i < stepInfo.getParameters().size(); ++i) {
      ParameterInfo param = stepInfo.getParameters().get(i);
      replMap.put("${" + i + "}", TypeUtils.unqoteString(param.getParamValue()));
    }
    String fileName = (String) annotation.getParams().get("filePattern");
    if (fileName == null) {
      return null;
    }
    fileName = TypeUtils.unqoteString(fileName);

    for (Map.Entry<String, String> me : replMap.entrySet()) {
      fileName = StringUtils.replace(fileName, me.getKey(), me.getValue());
    }
    File file = lookupFileOrDir(projectRoot, fileName);
    if (file == null) {
      return null;
    }
    try {
      AnnotationInfo scent = TpsbEnvUtils.getAnnotation(stepInfo.getTestBuilderMethod(), "TpsbScenarioFile");
      String content = FileUtils.readFileToString(file, CharEncoding.UTF_8);
      return renderScenarioFile(file, scent, content);
    } catch (IOException ex) {
      log.warn("Scanrio file can't be read: " + file.getAbsolutePath() + "; " + ex.getMessage(), ex);
      return null;
    }
  }

  public static String getScenarioHtmlFromTestMethod(MethodInfo methodInfo)
  {
    return getScenarioFromTestMethod(methodInfo);
  }

  public static String getScenarioFromTestMethod(MethodInfo methodInfo)
  {
    AnnotationInfo ano = TpsbEnvUtils.getAnnotation(methodInfo, "TpsbScenarioFile");
    if (ano == null) {
      return null;
    }
    String fileName = (String) ano.getParams().get("filePattern");
    if (fileName == null) {
      return null;
    }
    fileName = TypeUtils.unqoteString(fileName);
    File scfile = JavaDocUtil.lookupFileOrDir(null, fileName);
    if (scfile == null) {
      return null;
    }
    try {
      String content = FileUtils.readFileToString(scfile, CharEncoding.UTF_8);
      return renderScenarioFile(scfile, ano, content);
    } catch (IOException ex) {
      log.warn("Scanrio file can't be read: " + scfile.getAbsolutePath() + "; " + ex.getMessage(), ex);
      return null;
    }
  }

  public static File lookupFileOrDir(String projectRoot, String fileName)
  {
    File file;
    if (projectRoot == null) {
      file = TpsbEnvironment.get().lookupFileInProjectRoots(fileName);
    } else {
      file = new File(new File(projectRoot), fileName);
    }
    if (file == null || file.exists() == false) {
      log.warn("Scanrio file doesn't exists: " + fileName);
      return null;
    }
    return file;
  }

  public static String getScenarioInfo(String projectRoot, TestStepInfo stepInfo)
  {
    MethodInfo builderMethod = stepInfo.getTestBuilderMethod();
    if (builderMethod == null) {
      return null;
    }
    AnnotationInfo scenarioAnot = TpsbEnvUtils.getAnnotation(builderMethod, "TpsbScenarioFile");
    if (scenarioAnot == null) {
      return null;
    }
    return getScenarioInfo(projectRoot, stepInfo, scenarioAnot);
    //    List<AnnotationInfo> annotations = builderMethod.getAnnotations();
    //    if (annotations == null) {
    //      return null;
    //    }
    //    for (AnnotationInfo ai : annotations) {
    //      if (StringUtils.equals(ai.getName(), "TpsbScenarioFile") == true) {
    //        
    //      }
    //    }
    //    return null;
  }

  public static List<String> getAvailableScenarios(String projectRoot, TestStepInfo stepInfo)
  {
    List<String> ret = new ArrayList<String>();
    MethodInfo builderMethod = stepInfo.getTestBuilderMethod();
    if (builderMethod == null) {
      return ret;
    }
    List<AnnotationInfo> annotations = builderMethod.getAnnotations();
    if (annotations == null) {
      return ret;
    }
    for (AnnotationInfo ai : annotations) {
      if (StringUtils.equals(ai.getName(), "TpsbScenarioFile") == true) {
        return getScenarioFileNames(projectRoot, ai);
      }
    }
    return null;
  }

}