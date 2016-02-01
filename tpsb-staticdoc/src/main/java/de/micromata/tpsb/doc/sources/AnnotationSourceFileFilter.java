package de.micromata.tpsb.doc.sources;

import japa.parser.JavaParser;
import japa.parser.ParseException;
import japa.parser.ast.CompilationUnit;
import japa.parser.ast.body.ClassOrInterfaceDeclaration;
import japa.parser.ast.expr.AnnotationExpr;
import japa.parser.ast.visitor.GenericVisitorAdapter;

import java.lang.annotation.Annotation;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 * Filter, welcher die übergebene JavaSource nah einer Annotation hin untersucht
 * 
 * @author Stefan Stützer (s.stuetzer@micromata.com)
 */
public class AnnotationSourceFileFilter implements ISourceFileFilter
{

  Logger log = Logger.getLogger(AnnotationSourceFileFilter.class);

  private Class< ? extends Annotation> annotation;

  boolean isAnnotated = false;

  public AnnotationSourceFileFilter(Class< ? extends Annotation> annotation)
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

  class ClassAnnotationVisitor extends GenericVisitorAdapter<Void, Class< ? extends Annotation>>
  {
    @Override
    public Void visit(ClassOrInterfaceDeclaration n, Class< ? extends Annotation> clazz)
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
