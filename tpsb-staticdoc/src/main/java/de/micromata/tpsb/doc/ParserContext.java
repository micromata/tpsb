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

import de.micromata.tpsb.doc.parser.FileInfo;
import de.micromata.tpsb.doc.parser.MethodInfo;
import de.micromata.tpsb.doc.parser.ParameterInfo;
import de.micromata.tpsb.doc.parser.ParserResult;
import de.micromata.tpsb.doc.parser.TestStepInfo;
import de.micromata.tpsb.doc.parser.TypeUtils;
import de.micromata.tpsb.doc.parser.japa.TestBuilderVisitor;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.log4j.Logger;

/**
 * Context zum Parserlauf
 * 
 * @author Stefan Stützer (s.stuetzer@micromata.com)
 */
public class ParserContext
{

  private final static Logger log = Logger.getLogger(ParserContext.class);

  /** Konfiguration zum Parserlauf */
  private ParserConfig cfg;

  /** Aktuelles Java-Package */
  private String currentPackage;

  /** Aktuell geparste Klasse */
  private String currentClassName;

  private String sourceText;

  /**
   * Map aller aktuellen Imports
   * 
   * fullqualified -> simple classname
   */
  private BidiMap<String, String> currentImports = new DualHashBidiMap<String, String>();

  /**
   * Member Variablen in aktuell geparster Klasse
   * 
   * field name -> fullqualified type
   */
  private Map<String, String> currentFields = new HashMap<String, String>();

  /** Name der aktuell geparsten Methode */
  private String currentMethodName;

  /**
   * Map mit allen lokalen Variablen der aktuellen Methode
   * 
   * field name -> fullqualified type
   */
  private Map<String, String> currentLocalVars = new HashMap<String, String>();

  /**
   * aktueller Scope (Klassenname)
   */
  private String currentScope;

  /**
   * Aktueller Inline Kommentar
   */
  private String currentInlineComment;

  /** Das Ergebnis des Parserlaufs */
  private ParserResult currentParserResult = new ParserResult();

  /** Ergebnis des Laufs des Testbuilder-Parsens */
  private ParserResult parsedTestBuilders;

  public ParserContext(ParserConfig cfg, ParserResult prevParserResult)
  {
    this.parsedTestBuilders = prevParserResult;
    this.cfg = cfg;
  }

  public ParserContext(ParserConfig cfg)
  {
    this.cfg = cfg;
  }

  public ParserContext()
  {
  }

  public FileInfo getFileInfo(final String fullQualifiedClassName)
  {
    return currentParserResult.getFileInfoForFullQualifiedClassName(fullQualifiedClassName);
  }

  public List<FileInfo> getAllFileInfos()
  {
    return currentParserResult.getAllFileInfos();
  }

  public MethodInfo getMethodInfo(final String className, final String methodName)
  {
    FileInfo fileInfo = getFileInfo(className);
    return fileInfo.getMethodInfo(methodName);
  }

  public MethodInfo findMethodInfo(final String className, final String methodName)
  {
    FileInfo fileInfo = getFileInfo(className);
    return fileInfo.findMethodInfo(methodName, Collections.EMPTY_LIST);
  }

  public void addFileInfo(FileInfo fileInfo)
  {
    this.currentParserResult.add(fileInfo);
  }

  public void addFileInfos(List<FileInfo> fileInfos)
  {
    this.currentParserResult.add(fileInfos);
  }

  public void setCurrentClassName(String currentClass)
  {
    this.currentClassName = currentClass;
  }

  public String getCurrentClassName()
  {
    return currentClassName;
  }

  public void setCfg(ParserConfig cfg)
  {
    this.cfg = cfg;
  }

  public ParserConfig getCfg()
  {
    return cfg;
  }

  public void setCurrentPackage(String currentPackage)
  {
    this.currentPackage = currentPackage;
  }

  public String getCurrentPackage()
  {
    return currentPackage;
  }

  public void setCurrentParserResult(ParserResult parserResult)
  {
    this.currentParserResult = parserResult;
  }

  public ParserResult getCurrentParserResult()
  {
    return currentParserResult;
  }

  public void setParsedTestBuilders(ParserResult prevParserResult)
  {
    this.parsedTestBuilders = prevParserResult;
  }

  public ParserResult getParsedTestBuilders()
  {
    return parsedTestBuilders;
  }

  public void setCurrentMethodName(String currentMethod)
  {
    this.currentMethodName = currentMethod;
  }

  public String getCurrentMethodName()
  {
    return currentMethodName;
  }

  public void setCurrentScope(String currentScope)
  {
    this.currentScope = currentScope;
  }

  public String getCurrentScope()
  {
    return currentScope;
  }

  public void addImport(String fullQualifiedImportStmt, String unqualifiedClass)
  {
    currentImports.put(fullQualifiedImportStmt, unqualifiedClass);

  }

  public void addField(String fieldName, String fieldType)
  {
    currentFields.put(fieldName, fieldType);
  }

  public void addLocalVar(String varName, String varType)
  {
    currentLocalVars.put(varName, varType);
    // TODO some in XML
    MethodInfo currentMethodInfo = findMethodInfo(getCurrentClassName(), getCurrentMethodName());
    if (currentMethodInfo != null) {
      TestStepInfo ts = new TestStepInfo();
      ts.setTestMethod(currentMethodInfo);
      ts.setTbMethodName("createBuilder");
      ts.setTbClassName(TpsbEnvUtils.DefaultBaseBuilderName);
      ParameterInfo pi = new ParameterInfo();
      pi.setParamName("clazz");
      pi.setParamType("Class<T>");
      pi.setParamValue(varType);
      ts.getParameters().add(pi);
      // ts.setReturnType(returnType)
      currentMethodInfo.addTestStepInfo(ts);
    }
  }

  public Collection<String> getImports()
  {
    return currentImports.keySet();
  }

  public void setCurrentInlineComment(String currentInlineComment)
  {
    this.currentInlineComment = currentInlineComment;
  }

  public String getCurrentInlineComment()
  {
    return currentInlineComment;
  }

  public void resetCurrentInlineComment()
  {
    this.currentInlineComment = "";
  }

  /**
   * Liefert anhand des übergebenen Variablennames den Typ der Variablen zurück (anhand lokalen Variablen deklaration oder
   * Felddeklarationen)
   * 
   * @param varName Name der Variablen
   * @return
   */
  public String getTypeOfVariable(String varName)
  {
    String internScope = TypeUtils.getShortClassName(varName);
    String typeDec = currentLocalVars.get(internScope) != null ? currentLocalVars.get(internScope) : currentFields.get(internScope);
    if (StringUtils.isBlank(typeDec) == true) {
      log.debug(TestBuilderVisitor.IND_4 + "Scope nicht gefunden: " + internScope);
      return null;
    }
    return typeDec;

  }

  public FileInfo getTestBuilderInfoFromCurrentScope(String scope)
  {
    // 1. Testbuilderdaten anhand normalen Klassennamen suchen
    if (getParsedTestBuilders() != null) {
      FileInfo infoForClassName = getParsedTestBuilders().getFileInfoForClassName(scope);
      if (infoForClassName != null) {
        return infoForClassName;
      }
    }
    FileInfo fileInfo = null;
    if (parsedTestBuilders != null) {
      // 2. Fallback: Testbuilder-aten anhand fullqualified Klassennamen suchen
      String fullQualifiedClassName = getFullQualifiedNameFromImports(scope);

      fileInfo = parsedTestBuilders.getFileInfoForFullQualifiedClassName(fullQualifiedClassName);
    }
    if (fileInfo == null) {
      String fullQualifiedClassName = getFullQualifiedNameFromImports(scope);
      fileInfo = TpsbEnvironment.get().findTestBuilder(fullQualifiedClassName);
    }
    if (fileInfo == null) {
      log.error("No TestBuilder type found: " + scope);
    }
    return fileInfo;
  }

  public String getFullQualifiedNameFromImports(String className)
  {
    if (StringUtils.isEmpty(className) == true) {
      return className;
    }

    // Generics enthalten?
    className = removeGenerics(className);

    // es ist ein generischer Typ
    if (StringUtils.isAllUpperCase(className)) {
      return className;
    }

    // Klassenname ist leer oder bereits fullqualified
    if (className.indexOf(".") > -1) {
      return className;
    }
    String importedClass = currentImports.getKey(className);
    if (StringUtils.isNotEmpty(importedClass)) {
      return importedClass;
    }
    return currentPackage + "." + className;
  }

  private String removeGenerics(String type)
  {
    // Generics enthalten?
    if (StringUtils.indexOf(type, "<") > 0) {
      type = StringUtils.substringBefore(type, "<");
    }
    return type;
  }

  public void clearContext()
  {
    currentPackage = "";
    currentImports.clear();
    currentClassName = "";
    currentFields.clear();
    clearLocalContext();
  }

  public void clearLocalContext()
  {
    currentMethodName = "";
    currentLocalVars.clear();
    currentScope = "";
    setCurrentInlineComment("");
  }

  @Override
  public String toString()
  {
    ToStringBuilder tb = new ToStringBuilder(this);
    tb.append(currentParserResult);
    String ret = tb.toString();
    if (StringUtils.isBlank(ret) == true) {
      ret = "UNKOWN ParseContext";
    }
    return ret;
  }

  public String getSourceText()
  {
    return sourceText;
  }

  public void setSourceText(String sourceText)
  {
    this.sourceText = sourceText;
  }
}