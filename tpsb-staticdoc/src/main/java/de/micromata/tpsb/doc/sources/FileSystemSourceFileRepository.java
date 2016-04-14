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
