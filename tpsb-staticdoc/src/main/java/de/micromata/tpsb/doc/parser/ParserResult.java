package de.micromata.tpsb.doc.parser;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.collections15.CollectionUtils;
import org.apache.commons.collections15.Predicate;
import org.apache.commons.lang.StringUtils;

/**
 * Ergebnis eines Parser-Laufs. Abbildung aller fuer einen Report relevanten AST Informationen
 * 
 * @author Stefan Stützer (s.stuetzer@micromata.com)
 */
public class ParserResult implements Iterable<FileInfo>, Serializable
{

  private static final long serialVersionUID = -164314799557505351L;

  private List<FileInfo> fileInfos = new ArrayList<FileInfo>();

  @Override
  public Iterator<FileInfo> iterator()
  {
    Collections.sort(fileInfos, new Comparator<FileInfo>() {

      @Override
      public int compare(FileInfo o1, FileInfo o2)
      {
        return o1.getClassName().compareTo(o2.getClassName());
      }

    });
    return fileInfos.iterator();
  }

  public void add(FileInfo fileInfo)
  {
    fileInfos.add(fileInfo);
  }

  public void add(List<FileInfo> fInfos)
  {
    fileInfos.addAll(fInfos);
  }

  // Zugriffsmethoden
  public List<FileInfo> getAllFileInfos()
  {
    return fileInfos;
  }

  public FileInfo getFileInfoForFullQualifiedClassName(final String fullQualifiedClassName)
  {
    return CollectionUtils.find(this, new Predicate<FileInfo>() {
      @Override
      public boolean evaluate(FileInfo fInfo)
      {
        return StringUtils.equals(fullQualifiedClassName, fInfo.getClassName());
      }
    });
  }

  public FileInfo getFileInfoForClassName(final String className)
  {
    return CollectionUtils.find(this, new Predicate<FileInfo>() {
      @Override
      public boolean evaluate(FileInfo fInfo)
      {
        return StringUtils.equals(className, fInfo.getClassName()) || StringUtils.endsWith(fInfo.getClassName(), className);
      }
    });
  }

  public int getMethodCount()
  {
    int methodCount = 0;
    for (FileInfo fInfo : fileInfos) {
      if (fInfo.getMethodInfos() != null) {
        methodCount += fInfo.getMethodInfos().size();
      }
    }
    return methodCount;
  }
}
