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

package de.micromata.tpsb.doc.renderer;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.micromata.genome.util.runtime.LocalSettings;
import de.micromata.tpsb.project.TpsbProjectCatalog;

/**
 * Utils to load class to render.
 * 
 * @author Roger Kommer (roger.kommer.extern@micromata.de)
 * 
 */
public class RendererClassUtils
{
  private static final Logger log = Logger.getLogger(RendererClassUtils.class);

  static ClassLoader rendererClassLoader = null;

  public static ClassLoader createClassLoaderFromPath(List<String> pathes)
  {
    List<URL> urls = new ArrayList<URL>();

    try {
      for (String path : pathes) {
        File f = new File(path);
        if (f.exists() == false) {
          log.error("tpsbrendercp class path doesn't exist: " + path + " in " + f.getAbsolutePath());
          continue;
        }
        URL url = new File(path).toURI().toURL();
        urls.add(url);
      }
      URLClassLoader cl = new URLClassLoader(urls.toArray(new URL[] {}),
          RendererClassUtils.class.getClassLoader(), null)
      {
        @Override
        public Class< ? > loadClass(String name) throws ClassNotFoundException
        {
          try {
            return super.loadClass(name);
          } catch (ClassNotFoundException ex) {
            throw ex;
          }
        }
      };
      return cl;
    } catch (RuntimeException ex) {
      throw ex;
    } catch (Exception ex) {
      throw new RuntimeException(ex);
    }
  }

  private static void initProjectClassPaths(List<String> cplist)
  {
    TpsbProjectCatalog catalog = TpsbProjectCatalog.getInstance();
    List<String> cps = catalog.getProjectCps();
    cplist.addAll(cps);
  }

  private static void addRencerCps(List<String> cps)
  {
    String tpsbcp = LocalSettings.get().get("genome.tpsb.rendercp");
    if (StringUtils.isBlank(tpsbcp) == true) {
      return;
    }
    String[] pathes = StringUtils.split(tpsbcp, ',');
    for (String path : pathes) {
      cps.add(StringUtils.trim(path));
    }

  }

  private static void addRenderprojectsCps(List<String> cps)
  {
    String tpsbcp = LocalSettings.get().get("genome.tpsb.renderprojectscp");
    if (StringUtils.isBlank(tpsbcp) == true) {
      return;
    }
    String[] pathes = StringUtils.split(tpsbcp, ',');
    for (String path : pathes) {
      String spath = StringUtils.trim(path);
      File dir = new File(spath);
      File td = new File(dir, "target/test-classes");
      if (td.exists() == true) {
        cps.add(td.getAbsolutePath());
      }
      td = new File(dir, "target/classes");
      if (td.exists() == true) {
        cps.add(td.getAbsolutePath());
      }
    }

  }

  public static ClassLoader getRendererClassLoader()
  {
    if (rendererClassLoader != null) {
      return rendererClassLoader;
    }
    List<String> cps = new ArrayList<String>();
    addRencerCps(cps);
    addRenderprojectsCps(cps);
    initProjectClassPaths(cps);
    StringBuilder sb = new StringBuilder();
    sb.append("Inited Render ClassLoader:");
    for (String scl : cps) {
      sb.append(" ").append(scl).append("\n");
    }
    log.info(sb.toString());
    rendererClassLoader = createClassLoaderFromPath(cps);

    return rendererClassLoader;
  }

  public static <T> T loadClass(String name, Class<T> expected)
  {
    try {
      return (T) getRendererClassLoader().loadClass(name).newInstance();
    } catch (ClassNotFoundException ex) {
      throw new RuntimeException("Cannot load class: " + name + "; " + ex.getMessage(), ex);
    } catch (RuntimeException ex) {
      throw ex;
    } catch (Exception ex) {
      throw new RuntimeException(ex);
    } catch (NoClassDefFoundError ex) {
      throw ex;
    }
  }

}
