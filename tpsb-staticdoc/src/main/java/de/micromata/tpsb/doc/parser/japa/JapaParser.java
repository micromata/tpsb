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

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;
import de.micromata.tpsb.doc.ParserConfig;
import de.micromata.tpsb.doc.ParserContext;
import de.micromata.tpsb.doc.sources.ISourceFileFilter;
import de.micromata.tpsb.doc.sources.ISourceFileRepository;
import de.micromata.tpsb.doc.sources.JavaSourceFileHolder;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.Transformer;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.CharEncoding;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

/**
 * Java-Code Parser unter Verwendung der japa-library
 * 
 * @author Stefan Stützer (s.stuetzer@micromata.com)
 */
public class JapaParser
{

  private static final Logger log = Logger.getLogger(JapaParser.class);

  private TestBuilderVisitor testBuilderVisitor = new TestBuilderVisitor();

  private TestCaseVisitor testCaseVisitor = new TestCaseVisitor();

  public void parseTestBuilders(ParserContext ctx)
  {
    try {
      log.info("\n---------------------------------------------------");
      log.info("Parse TestBuilder");
      Collection<JavaSourceFileHolder> sourceFiles = getSourceFiles(ctx.getCfg());
      for (JavaSourceFileHolder sourceFile : sourceFiles) {
        log.info("Parse Datei " + sourceFile.getFilename());
        CompilationUnit cu = JavaParser.parse(sourceFile.getAsInputStream());
        testBuilderVisitor.visit(cu, ctx);
      }
    } catch (ParseException e) {
      log.error("Fehler beim Parsen", e);
    }
  }

  public void parseTestCases(ParserContext ctx)
  {
    try {
      log.info("\n---------------------------------------------------");
      log.info("Parse JUnit Tests");
      Collection<JavaSourceFileHolder> sourceFiles = getSourceFiles(ctx.getCfg());
      for (JavaSourceFileHolder sourceFile : sourceFiles) {
        log.info("Parse Datei " + sourceFile.getFilename());
        try {
          byte[] data = IOUtils.toByteArray(sourceFile.getAsInputStream());
          String sources = new String(data, CharEncoding.UTF_8);
          ctx.setSourceText(sources);
          CompilationUnit cu = JavaParser.parse(new ByteArrayInputStream(data));
          testCaseVisitor.visit(cu, ctx);
        } catch (IOException ex) {
          ex.printStackTrace();
        }
      }
    } catch (ParseException e) {
      e.printStackTrace();
    }
  }

  private Collection<JavaSourceFileHolder> getSourceFiles(ParserConfig cfg)
  {
    // 1. alle Repositories nach Java-Sourcen durchsuchen
    List<JavaSourceFileHolder> javaSources = new ArrayList<JavaSourceFileHolder>();
    loadSourcesFromRepositories(cfg, javaSources);

    // 2. Filtern mit SourceFileFilters
    filterSoures(cfg, javaSources);

    if (javaSources == null || javaSources.isEmpty() == true) {
      log.warn("Keine Java-Dateien gefunden.");
      return Collections.emptyList();
    }

    Collection sourceFileNames = CollectionUtils.transformingCollection(javaSources,
        (Transformer) input -> input.toString());
    log.info("Java Dateien gefunden: " + javaSources.size() + ": \n\t" + StringUtils.join(sourceFileNames, "\n\t"));
    return javaSources;
  }

  private void loadSourcesFromRepositories(ParserConfig cfg, List<JavaSourceFileHolder> javaSources)
  {
    for (ISourceFileRepository srcRepo : cfg.getSourceFileRepos()) {
      Collection<JavaSourceFileHolder> javaSourcesFromRepo = srcRepo.getSources();
      if (javaSourcesFromRepo != null && javaSourcesFromRepo.isEmpty() == false) {
        javaSources.addAll(javaSourcesFromRepo);
      }
    }
  }

  private void filterSoures(ParserConfig cfg, List<JavaSourceFileHolder> javaSources)
  {
    List<ISourceFileFilter> sourceFileFilter = cfg.getSourceFileFilter();
    if (sourceFileFilter == null || sourceFileFilter.isEmpty() == true) {
      return;
    }

    Iterator<JavaSourceFileHolder> iter = javaSources.iterator();
    while (iter.hasNext()) {
      JavaSourceFileHolder javaSrc = iter.next();
      boolean matches = false;
      for (ISourceFileFilter filter : sourceFileFilter) {
        if (filter.matches(javaSrc) == true) {
          matches = true;
          break;
        }
      }
      if (matches == false) {
        iter.remove();
      }
    }

  }
}