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

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import org.apache.commons.lang.CharEncoding;
import org.apache.commons.lang.StringUtils;

import de.micromata.tpsb.doc.sources.JavaSourceFileHolder.Source;

public class JarSourceFileRepository extends AbstractSourceFileRepository
{

  static byte[] buffer = new byte[1024];

  public JarSourceFileRepository(String[] locations)
  {
    super(locations);
  }

  public JarSourceFileRepository(String location)
  {
    super(new String[] { location});
  }

  @Override
  public Collection<JavaSourceFileHolder> getSources()
  {
    List<JavaSourceFileHolder> fileContentList = new ArrayList<JavaSourceFileHolder>();

    for (String jarFileName : getLocations()) {
      try {
        JarFile jarFile;
        jarFile = new JarFile(jarFileName);
        Enumeration< ? extends ZipEntry> jarEntryEnum = jarFile.entries();

        while (jarEntryEnum.hasMoreElements()) {
          JarEntry jarEntry = (JarEntry) jarEntryEnum.nextElement();

          // nur Java-Dateien beachten
          if (jarEntry.isDirectory() || StringUtils.endsWith(jarEntry.getName(), ".java") == false) {
            continue;
          }

          String javaFilename = jarEntry.getName();
          String javaFileContent = getContents(jarFile, jarEntry);

          if (StringUtils.isBlank(javaFileContent) == true) {
            continue;
          }
          JavaSourceFileHolder fileHolder = new JavaSourceFileHolder(javaFilename, javaFileContent);
          fileHolder.setSource(Source.Jar);
          fileHolder.setOrigin(jarFileName);
          fileContentList.add(fileHolder);
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    return fileContentList;
  }

  private String getContents(JarFile jarFile, JarEntry jarEntry)
  {
    try {

      InputStream is = jarFile.getInputStream(jarEntry);
      BufferedInputStream stream = new BufferedInputStream(is);
      int bytesRead = 0;
      StringBuilder sb = new StringBuilder();
      while ((bytesRead = stream.read(buffer)) != -1) {
        String chunk = new String(buffer, 0, bytesRead, Charset.forName(CharEncoding.UTF_8));
        sb.append(chunk);
      }
      return sb.toString();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }

}
