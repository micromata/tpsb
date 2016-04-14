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

package de.micromata.tpsb.doc.parser.japa;

import japa.parser.JavaParser;
import japa.parser.ParseException;
import japa.parser.ast.CompilationUnit;
import japa.parser.ast.ImportDeclaration;
import japa.parser.ast.PackageDeclaration;
import japa.parser.ast.TypeParameter;
import japa.parser.ast.body.ClassOrInterfaceDeclaration;
import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.body.Parameter;
import japa.parser.ast.type.ClassOrInterfaceType;
import japa.parser.ast.type.ReferenceType;
import japa.parser.ast.type.Type;
import japa.parser.ast.visitor.VoidVisitorAdapter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.micromata.tpsb.doc.ParserContext;
import de.micromata.tpsb.doc.TpsbEnvironment;
import de.micromata.tpsb.doc.parser.FileInfo;
import de.micromata.tpsb.doc.parser.JavaDocInfo;
import de.micromata.tpsb.doc.parser.JavaDocUtil;
import de.micromata.tpsb.doc.parser.MethodInfo;
import de.micromata.tpsb.doc.parser.ParameterInfo;
import de.micromata.tpsb.doc.parser.japa.handler.INodeHandler;
import de.micromata.tpsb.doc.sources.FileSystemSourceFileRepository;
import de.micromata.tpsb.doc.sources.ISourceFileRepository;

/**
 * AST Visitor welche fuer Testbuilder relevante Informationen besucht
 * 
 * @author Stefan Stützer (s.stuetzer@micromata.com)
 */
public class TestBuilderVisitor extends VoidVisitorAdapter<ParserContext>
{

  private final static Logger log = Logger.getLogger(TestBuilderVisitor.class);

  public final static String IND_1 = "...";

  public final static String IND_2 = "......";

  public final static String IND_3 = ".........";

  public final static String IND_4 = "............";

  // Liste von source Packages im Maven Layout
  public static final Set<String> srcPackages = new HashSet<String>(Arrays.asList("", "src/test/java/", "src/main/java/"));

  // Liste "reservierter" Methodennamen
  // TODO das macht das aber gerade kaputt
//  public static final List<String> reservedMethods = Arrays.asList("addComment");
  public static final List<String> reservedMethods = Arrays.asList();
  // Liste spezieller Handler von Nodes
  public static Map<String, INodeHandler< ? >> handlers;

  static {
    handlers = new HashMap<String, INodeHandler< ? >>();
//    handlers.put("addComment", new AddCommentMethodCallHandler());
  }

  @Override
  public void visit(PackageDeclaration n, ParserContext ctx)
  {
    ctx.clearContext();
    if (n.getName() != null) {
      ctx.setCurrentPackage(n.getName().toString());
    }
    super.visit(n, ctx);
  }

  @Override
  public void visit(ImportDeclaration n, ParserContext ctx)
  {
    if (n.isAsterisk() == false) {
      ctx.addImport(n.getName().toString(), n.getName().getName());
    }
    super.visit(n, ctx);
  }

  @Override
  public void visit(ClassOrInterfaceDeclaration n, ParserContext ctx)
  {
    visitClassOrInterfaceDeclaration(n, ctx, true);
  }

  protected void visitClassOrInterfaceDeclaration(ClassOrInterfaceDeclaration n, ParserContext ctx, boolean parseSuperclass)
  {

    FileInfo superClassFileInfo = null;
    String fullqualifiedSuperClassName = null;
    List<String> superTemplateArgs = new ArrayList<String>();
    List<String> templateTypeNames = new ArrayList<String>();
    if (n.getTypeParameters() != null) {
      for (TypeParameter tp : n.getTypeParameters()) {
        templateTypeNames.add(tp.getName());
      }
    }
    if (n.getExtends() != null && n.getExtends().isEmpty() == false) {
      ClassOrInterfaceType superdecl = n.getExtends().iterator().next();
      String superClassName = superdecl.getName();

      // über Imports Superklasse auflösen
      fullqualifiedSuperClassName = ctx.getFullQualifiedNameFromImports(superClassName);
      if (fullqualifiedSuperClassName == null) {
        fullqualifiedSuperClassName = ctx.getCurrentPackage() + "." + superClassName;
      }
      if (parseSuperclass == true) {
        superClassFileInfo = getSuperFileInfo(ctx, fullqualifiedSuperClassName);
        if (superdecl.getTypeArgs() != null) {
          for (Type tp : superdecl.getTypeArgs()) {
            if (tp instanceof ClassOrInterfaceType) {
              ClassOrInterfaceType ct = (ClassOrInterfaceType) tp;
              superTemplateArgs.add(ct.getName());
            } else if (tp instanceof ReferenceType) {
              ReferenceType rt = (ReferenceType) tp;
              Type reft = rt.getType();
              if (reft instanceof ClassOrInterfaceType) {
                superTemplateArgs.add(((ClassOrInterfaceType) reft).getName());
              } else {
                log.warn("Unhandled Superclass Template Reference Argument type: " + tp);
              }
            } else {
              log.warn("Unhandled Superclass Template Argument type: " + tp);
            }
          }
        }
      }
    }
    String className = n.getName();
    if (StringUtils.isNotBlank(ctx.getCurrentPackage()) == true) {
      className = ctx.getCurrentPackage() + "." + className;
    }
    ctx.setCurrentClassName(className);
    if (ctx.getFileInfo(className) != null) {
      super.visit(n, ctx);
      log.debug("Allready parsed: " + className);
      return;
    }

    FileInfo fileInfo = new FileInfo();
    fileInfo.setModifier(n.getModifiers());
    fileInfo.setAnnotations(ParserUtil.parseAnnotation(n.getAnnotations()));
    fileInfo.setSuperClassName(fullqualifiedSuperClassName);
    if (superTemplateArgs.isEmpty() == false) {
      fileInfo.setSuperTemplateArgs(superTemplateArgs);
    }
    if (templateTypeNames.isEmpty() == false) {
      fileInfo.setTypeArgNames(templateTypeNames);
    }
    fileInfo.getImports().addAll(ctx.getImports());
    if (superClassFileInfo != null) {
      fileInfo.setSuperClassFileInfo(superClassFileInfo);
    }
    fileInfo.setClassName(className);
    if (n.getComment() != null) {
      fileInfo.setJavaDocInfo(JavaDocUtil.parseJavaDoc(n.getComment()));
    }
    if (ParserUtil.ignoreClass(fileInfo) == true) {
      super.visit(n, ctx);
      System.err.println("skip " + className);
      return;
    }
    ctx.addFileInfo(fileInfo);
    log.info(IND_1 + "Analysiere Java-Klasse: " + className);
    super.visit(n, ctx);
  }

  private FileInfo getSuperFileInfo(ParserContext ctx, String fullqualifiedSuperClassName)
  {
    // Superklasse schon in geparsten Testbuilder-Daten enthalten?
    if (ctx.getFileInfo(fullqualifiedSuperClassName) != null) {
      return ctx.getFileInfo(fullqualifiedSuperClassName);
    }
    return parseSuperClass(fullqualifiedSuperClassName, ctx);
  }

  /**
   * Versucht die Superklasse zu finden (auch außerhalb zu parsender Packages) und die zu Parsen. Das Ergebnis des Parserlaufs wird in den
   * aktuellen ParserContext kopiert
   * 
   * Hinweis: Die Superklasse muss als Quelldatei vorliegen. Dateien Im Jar werden nicht geparst
   * 
   * @param fullqualifiedSuperClassName fullqualified Klassenname der Superklasse
   * @param ctx der aktuelle Parser-Context
   * @return das FileInfo der Superklasse, falls gefunden und geparst
   */
  private FileInfo parseSuperClass(String fullqualifiedSuperClassName, ParserContext ctx)
  {
    // String[] ctxRootPaths = ctx.getCfg().getSourceFileResolver().getRootPaths();
    // TODO prfen ob auch Jars mit einbezogen werden können

    List<String> ctxRootPaths = new ArrayList<String>();
    for (ISourceFileRepository repo : ctx.getCfg().getSourceFileRepos()) {
      if (repo instanceof FileSystemSourceFileRepository) {
        FileSystemSourceFileRepository fsRepo = (FileSystemSourceFileRepository) repo;
        for (String sp : srcPackages) {
          for (String loc : fsRepo.getLocations()) {
            if (loc.endsWith(sp) == true && StringUtils.isNotEmpty(sp) == true) {
              continue;
            }
            ctxRootPaths.add(ParserUtil.concatFileName(loc, sp));
          }
        }
      }
    }

    String fileName = fullqualifiedSuperClassName.replace(".", File.separator).concat(".java");
    log.info(IND_1 + "Versuche Testbuilder Superklasse '" + fullqualifiedSuperClassName + "' zu parsen.");
    File sourceFile = null;
    for (String srcPackage : ctxRootPaths) {
      String fName = ParserUtil.concatFileName(srcPackage, fileName);
      sourceFile = new File(fName);
      String absPath = sourceFile.getAbsolutePath();
      if (sourceFile.exists() == true) {
        break;
      }
    }

    if (sourceFile == null || sourceFile.exists() == false) {
      FileInfo fi = TpsbEnvironment.get().getTestBuilder(fullqualifiedSuperClassName);
      if (fi != null) {
        return fi;
      }
      log.warn(IND_2 + "Quelldatei der Superklasse konnte nicht gefunden werden: " + fullqualifiedSuperClassName);
      return null;
    }
    try {
      CompilationUnit cu = JavaParser.parse(sourceFile);
      ParserContext _ctx = new ParserContext(ctx.getCfg());
      new TestBuilderVisitor().visit(cu, _ctx);

      // copy parser context to current context
      List<FileInfo> _fileInfos = _ctx.getAllFileInfos();
      ctx.addFileInfos(_fileInfos);
      log.debug(IND_2 + "Kopiere FileInfos in aktuellen ParserContext");
      return _ctx.getFileInfo(fullqualifiedSuperClassName);
    } catch (ParseException e) {
      log.error(IND_2 + e.getMessage());
    } catch (IOException e) {
      log.error(IND_2 + e.getMessage());
    }
    return null;
  }

  @Override
  public void visit(MethodDeclaration n, ParserContext ctx)
  {
    ctx.clearLocalContext();
    if (StringUtils.isBlank(ctx.getCurrentClassName()) == true) {
      return;
    }
    FileInfo fCtx = ctx.getFileInfo(ctx.getCurrentClassName());
    if (fCtx == null) {
      log.warn("Skip probably nested class: " + ctx.getCurrentClassName());
      return;
    }
    MethodInfo mInfo = new MethodInfo();
    mInfo.setModifier(n.getModifiers());
    mInfo.setClassName(fCtx.getClassName());
    mInfo.setClassInfo(fCtx);
    mInfo.setMethodName(n.getName());
    mInfo.setReturnType(n.getType().toString());
    mInfo.setAnnotations(ParserUtil.parseAnnotation(n.getAnnotations()));
    if (mInfo.isTpsbIgnore() == true) {
      return;
    }
    JavaDocInfo methodJavaDoc = null;
    if (n.getComment() != null) {
      methodJavaDoc = JavaDocUtil.parseJavaDoc(n.getComment());
      mInfo.setJavaDocInfo(methodJavaDoc);
    }
    log.info(IND_2 + "Analysiere Methode: " + mInfo.getMethodName());

    if (reservedMethods.contains(n.getName()) == true) {
      log.warn(String.format("Methode '%s' ist ein reservierter Methodenname und wird beim Parsen entsprechend interpretiert.", n.getName()));
    }

    if (n.getParameters() != null) {
      for (Parameter parameter : n.getParameters()) {
        ParameterInfo pInfo = new ParameterInfo();
        pInfo.setAnnotations(ParserUtil.parseAnnotation(parameter.getAnnotations()));
        pInfo.setParamName(parameter.getId().getName());
        pInfo.setParamType(parameter.getType().toString());
        if (n.getComment() != null && methodJavaDoc != null) {
          pInfo.setJavaDoc(methodJavaDoc.getParamDoc(parameter.getId().getName()));
        }
        pInfo.setVarArg(parameter.isVarArgs());
        mInfo.addParamInfo(pInfo);
      }
    }
    fCtx.addMethodInfo(mInfo);
    ctx.setCurrentMethodName(n.getName());
    super.visit(n, ctx);
  }
}