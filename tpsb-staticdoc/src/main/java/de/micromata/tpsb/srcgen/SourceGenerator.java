package de.micromata.tpsb.srcgen;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.CharEncoding;
import org.apache.commons.lang.StringUtils;

import de.micromata.genome.util.types.Converter;
import de.micromata.genome.util.types.Pair;
import de.micromata.tpsb.doc.parser.AnnotationInfo;
import de.micromata.tpsb.doc.parser.FileInfo;
import de.micromata.tpsb.doc.parser.JavaDocInfo;
import de.micromata.tpsb.doc.parser.MethodInfo;
import de.micromata.tpsb.doc.parser.ParameterInfo;
import de.micromata.tpsb.doc.parser.TestStepInfo;
import de.micromata.tpsb.doc.parser.TypeUtils;

/**
 * Creates a source from a testcase
 * 
 * @author roger
 * 
 */
public class SourceGenerator
{

  private FileInfo fileInfo;

  private SourceCodeBuffer sb = new SourceCodeBuffer();

  private static final String indent = "  ";

  private static final String sind = "    ";

  private static final String metodClassInd = "        ";

  private boolean generateStatements = false;

  private SourceGenListener sourceGenListener = new SourceGenListener();

  public SourceGenerator()
  {

  }

  public SourceGenerator(FileInfo fileInfo, boolean generateStatements)
  {
    this.fileInfo = fileInfo;
    this.generateStatements = generateStatements;
  }

  public String generate()
  {
    generateHeader();
    generateMethods();
    generateFooter();
    return sb.toString();
  }

  public String writeToDisk(String baseDir)
  {
    if (sb.getBuffer().length() == 0) {
      generate();
    }
    File dir = new File(baseDir);
    if (dir.exists() == false) {
      throw new RuntimeException("Source generation; target dir doesn't exists: " + dir.getAbsolutePath());
    }
    String packageN = StringUtils.replace(TypeUtils.getPackageFromFqClassName(fileInfo.getClassName()), ".", "/");
    File packageDir = new File(dir, packageN);
    if (packageDir.exists() == false) {
      if (packageDir.mkdirs() == false) {
        throw new RuntimeException("Source generation; cannot create class package directory: " + packageDir.getAbsolutePath());
      }
    }
    String pureClassName = TypeUtils.getShortClassName(fileInfo.getClassName());
    File f = new File(packageDir, pureClassName + ".java");
    try {
      FileUtils.write(f, sb.getBuffer().toString(), CharEncoding.UTF_8);
      return f.getAbsolutePath();
    } catch (Exception ex) {
      throw new RuntimeException("Source generation; Error writing file " + f.getAbsolutePath() + "; " + ex.getMessage(), ex);
    }
  }

  public StringBuilder getBuffer()
  {
    return sb.getBuffer();
  }

  @Override
  public String toString()
  {
    return sb.toString();
  }

  protected void generateMethods()
  {
    for (MethodInfo mi : fileInfo.getMethodInfos()) {
      generateMethod(mi);
    }
  }

  public SourceGenerator generateMethod(MethodInfo mi)
  {
    generateJavaDoc(indent, mi.getJavaDocInfo());
    generateAnnotation(indent, mi.getAnnotations());
    sb.append(indent).append("public ").append(mi.getReturnType()).append(" ").append(mi.getMethodName()).append("(");
    // TODO parameters?
    sb.append(")\n").append(indent).append("{\n");
    generateSteps(mi);
    sb.append(indent).append("}\n");
    return this;
  }

  protected void generateSteps(MethodInfo mi)
  {
    sourceGenListener.beforeGenerateTestMethod(this, mi);
    if (generateStatements == true) {
      sb.append(sind).append("StartTestBuilder commonTestBuilder = new StartTestBuilder();\n");
    } else {
      sb.append(sind).append("new StartTestBuilder() //\n");
    }
    int currentStepCount = 0;
    String previusBuilder = "commonTestBuilder";
    for (TestStepInfo ts : mi.getTestSteps()) {
      ++currentStepCount;
      previusBuilder = generateTestStep(currentStepCount, mi, ts, previusBuilder);
    }
    sb.append(sind).append(";\n");
    sourceGenListener.afterGenerateTestMethod(this, mi);
  }

  protected String generateTestStep(int currentStepCount, MethodInfo mi, TestStepInfo ts, String previusBuilder)
  {
    ts.setLineNo(sb.getCurrentLineNo());
    sourceGenListener.beforeGenerateTestStep(this, currentStepCount, mi, ts, previusBuilder);

    String lineIdent = generateStatements ? sind : metodClassInd;
    generateInlineJavaDoc(lineIdent, ts.getInlineJavaDocInfo());
    sb.append(lineIdent);
    String nextBuilder = "builder" + currentStepCount;
    if (generateStatements == true) {
      if (ts.getReturnType() == null) {
        throw new RuntimeException("Cannot generate teststep, because teststep has no returntype: "
            + mi.getMethodName()
            + "; step: "
            + currentStepCount);
      }
      sb.append(ts.getReturnType().getClassName()).append(" ").append(nextBuilder).append(" = ").append(previusBuilder);
    }
    sb.append(".").append(ts.getTbMethodName()).append("(");
    boolean isFirst = true;
    for (ParameterInfo pi : ts.getParameters()) {
      if (isFirst == false) {
        sb.append(", ");
      }
      isFirst = false;
      sb.append(pi.getParamValue());
    }
    if (generateStatements == true) {
      sb.append(");\n");
    } else {
      sb.append(") //\n");
    }
    sourceGenListener.afterGenerateTestStep(this, currentStepCount, mi, ts, nextBuilder);
    return nextBuilder;
  }

  protected void generateJavaDocText(String indent, String content)
  {
    String[] lines = StringUtils.split(content, '\n');
    for (String l : lines) {
      sb.append(indent).append(l).append("\n");
    }
  }

  protected void generateJavaDoc(String indent, JavaDocInfo jdoc)
  {
    if (jdoc == null) {
      return;
    }
    sb.append(indent).append("/**\n");
    if (StringUtils.isNotBlank(jdoc.getTitle()) == true) {
      generateJavaDocText(indent + " * ", jdoc.getTitle());
      sb.append(indent).append(" *\n");
    }
    if (StringUtils.isNotBlank(jdoc.getDescription()) == true) {
      generateJavaDocText(indent + " * ", jdoc.getDescription());
      sb.append(indent).append(" *\n");
    }
    for (Map.Entry<String, List<Pair<String, String>>> te : jdoc.getTags().entrySet()) {
      for (Pair<String, String> p : te.getValue()) {
        sb.append(indent).append(" * ").append(te.getKey());
        if (p.getFirst() != null) {
          sb.append(" ").append(p.getFirst());
        }
        if (p.getSecond() != null) {
          String[] lines = StringUtils.split(p.getSecond(), '\n');
          if (lines.length > 0) {
            sb.append(" ").append(lines[0]).append("\n");
          }
          for (int i = 1; i < lines.length; ++i) {
            sb.append(indent).append(" * ").append("           ").append(lines[i]).append("\n");
          }
        }
      }
    }
    sb.append(indent).append(" */\n");
  }

  protected void generateAnnotation(String indent, AnnotationInfo ai)
  {
    if (ai == null) {
      return;
    }
    sb.append(indent).append("@").append(ai.getName());
    // TODO generate annoation arguments
    sb.append("\n");
  }

  protected void generateAnnotation(String indent, List<AnnotationInfo> ailist)
  {
    if (ailist == null) {
      return;
    }
    for (AnnotationInfo ai : ailist) {
      generateAnnotation(indent, ai);
    }

  }

  protected void generateInlineJavaDoc(String indent, JavaDocInfo jdoc)
  {
    if (jdoc == null) {
      return;
    }
    if (StringUtils.isNotBlank(jdoc.getTitle()) == true) {
      generateJavaDocText(sind + "// ", jdoc.getTitle());
    }
    if (StringUtils.isNotBlank(jdoc.getDescription()) == true) {
      generateJavaDocText(sind + "// ", jdoc.getDescription());
    }
  }

  protected void generateFileHeader()
  {
    sb.append("///////////////////////////////////////////////////////////////////////////////\n").append("//\n") //
        .append("// Micromata Genome TPSB TestSuite\n") //
        .append("//\n") //
        .append("// Generated ").append(Converter.formatByIsoDateFormat(new Date())).append("\n")//
        .append("// Copyright Micromata\n") //
        .append("//\n") //
        .append("/////////////////////////////////////////////////////////////////////////////\n\n");
  }

  protected void generateHeader()
  {
    generateFileHeader();
    String className = fileInfo.getClassName();
    String packageName = TypeUtils.getPackageFromFqClassName(className);
    String cname = TypeUtils.getShortClassName(className);
    if (packageName != null) {
      sb.append("package ").append(packageName).append(";\n");
    }
    sb.append("\n");

    generateImports();
    generateJavaDoc("", fileInfo.getJavaDocInfo());
    generateAnnotation("", fileInfo.getAnnotations());
    sb.append("public class ").append(cname);
    if (fileInfo.getSuperClassFileInfo() != null) {
      sb.append(" extends ").append(fileInfo.getSuperClassFileInfo().getClassName());
    } else if (fileInfo.getSuperClassName() != null) {
      sb.append(" extends ").append(fileInfo.getSuperClassName());
    }
    sb.append("\n{\n");
  }

  protected void generateFooter()
  {
    sb.append("}\n");
  }

  protected void generateImports()
  {

    fileInfo.addImport("de.micromata.genome.tpsb.StartTestBuilder");

    for (String imp : fileInfo.getImports()) {
      sb.append("import ").append(imp).append(";\n");
    }
    sb.append("\n");
  }

  public boolean isGenerateStatements()
  {
    return generateStatements;
  }

  public void setGenerateStatements(boolean generateStatements)
  {
    this.generateStatements = generateStatements;
  }

  public SourceGenListener getSourceGenListener()
  {
    return sourceGenListener;
  }

  public void setSourceGenListener(SourceGenListener sourceGenListener)
  {
    this.sourceGenListener = sourceGenListener;
  }

  public SourceCodeBuffer getSourceBuffer()
  {
    return sb;
  }
}
