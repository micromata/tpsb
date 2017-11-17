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

package de.micromata.tpsb.doc.sources;

import java.lang.annotation.Annotation;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.visitor.GenericVisitorAdapter;

/**
 * Filter, welcher die übergebene JavaSource nah einer Annotation hin untersucht
 * 
 * @author Stefan Stützer (s.stuetzer@micromata.com)
 */
public class AnnotationSourceFileFilter implements ISourceFileFilter
{

  Logger log = Logger.getLogger(AnnotationSourceFileFilter.class);

  private Class<? extends Annotation> annotation;

  boolean isAnnotated = false;

  public AnnotationSourceFileFilter(Class<? extends Annotation> annotation)
  {
    this.annotation = annotation;
  }

  @Override
  public synchronized boolean matches(JavaSourceFileHolder file)
  {
    try {
      isAnnotated = false;
      CompilationUnit cu;
      cu = JavaParser.parse(file.getAsInputStream());
      new ClassAnnotationVisitor().visit(cu, annotation);
      return isAnnotated;
    } catch (ParseException e) {
      log.error("Cannot parse file: " + file.getFilename() + "; " + e.getMessage(), e);
      //e.printStackTrace();
    }
    return false;
  }

  class ClassAnnotationVisitor extends GenericVisitorAdapter<Void, Class<? extends Annotation>>
  {
    @Override
    public Void visit(ClassOrInterfaceDeclaration n, Class<? extends Annotation> clazz)
    {

      if (n.getAnnotations() == null || n.getAnnotations().isEmpty()) {
        return null;
      }
      for (AnnotationExpr a : n.getAnnotations()) {
        if (StringUtils.equals(clazz.getSimpleName(), a.getName().getName()) == true) {
          AnnotationSourceFileFilter.this.isAnnotated = true;
        }
      }
      return null;
    }
  }
}
