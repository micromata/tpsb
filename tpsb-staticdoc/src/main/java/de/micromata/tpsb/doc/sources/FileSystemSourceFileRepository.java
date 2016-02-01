package de.micromata.tpsb.doc.sources;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.CharEncoding;

import de.micromata.tpsb.doc.sources.JavaSourceFileHolder.Source;

/**
 * Reads the file from the file system.
 * 
 * @author Roger Rene Kommer (r.kommer.extern@micromata.de)
 * 
 */
public class FileSystemSourceFileRepository extends AbstractSourceFileRepository
{

  public FileSystemSourceFileRepository(String[] locations)
  {
    super(locations);
  }

  public FileSystemSourceFileRepository(String location)
  {
    super(new String[] { location});
  }

  @Override
  public Collection<JavaSourceFileHolder> getSources()
  {
    List<JavaSourceFileHolder> fileHolderList = new ArrayList<JavaSourceFileHolder>();

    for (String loc : getLocations()) {
      Collection<File> foundJavaFiles = FileUtils.listFiles(new File(loc), new String[] { "java"}, true);
      for (File javaFile : foundJavaFiles) {
        try {
          String javaFileContent = FileUtils.readFileToString(javaFile, CharEncoding.UTF_8);
          String javaFilename = javaFile.getName();
          JavaSourceFileHolder fileHolder = new JavaSourceFileHolder(javaFilename, javaFileContent);
          fileHolder.setSource(Source.FileSystem);
          fileHolder.setOrigin(loc);
          fileHolderList.add(fileHolder);
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
    return fileHolderList;
  }
}
