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

package de.micromata.tpsb.doc.parser;

import de.micromata.tpsb.doc.TpsbEnvironment;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.Predicate;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * Repraesentiert die Parser-Information zu einer Java-Datei
 * 
 * @author Roger Kommer (roger.kommer.extern@micromata.de)
 * @author Stefan Stützer (s.stuetzer@micromata.com), roger
 */
public class FileInfo extends AnnotatedBase
{

  private static final long serialVersionUID = 1867295012931533807L;

  private List<String> imports = new ArrayList<String>();

  private String className;

  private String superClassName;

  /**
   * class X&lt;T, B&gt; MyClass. T, B will be here
   */
  private List<String> typeArgNames;

  /**
   * extends Super&lt;First,Second&gt; etc.
   */
  private List<String> superTemplateArgs;

  transient private FileInfo superClassFileInfo;

  private List<MethodInfo> methodInfos = new ArrayList<MethodInfo>();

  transient private Map<String, String> importsMap;

  /**
   * This was edited
   */
  transient private boolean dirty = false;

  public FileInfo()
  {

  }

  public FileInfo(FileInfo other)
  {
    super(other);
    this.superClassFileInfo = other.superClassFileInfo;
    this.className = other.className;
    this.imports.addAll(other.imports);
    this.importsMap = other.importsMap;
    for (MethodInfo mi : other.methodInfos) {
      methodInfos.add(new MethodInfo(mi));
    }
  }

  @Override
  public boolean isValid()
  {
    if (super.isValid() == false) {
      return false;
    }
    for (MethodInfo mi : methodInfos) {
      if (mi.isValid() == false) {
        return false;
      }
    }
    return true;
  }

  @Override
  public void collectValidationMessages(List<String> ret)
  {
    super.collectValidationMessages(ret);
    for (MethodInfo mi : methodInfos) {
      mi.collectValidationMessages(ret);
    }
  }

  public List<String> getAllValidationMessages()
  {
    List<String> ret = new ArrayList<String>();
    collectValidationMessages(ret);
    return ret;
  }

  public List<String> resolveFqClassInfoFromImports(List<String> l)
  {
    List<String> ret = new ArrayList<String>();
    for (String s : l) {
      ret.add(resolveFqClassInfoFromImports(s));
    }
    return ret;
  }

  public String resolveFqClassInfoFromImports(String shortClassName)
  {
    if (shortClassName == null) {
      return null;
    }
    if (className.endsWith("." + shortClassName) == true) {
      return className;
    }
    for (String imp : imports) {
      if (imp.endsWith("." + shortClassName) == true) {
        return imp;
      }
    }
    FileInfo fi = TpsbEnvironment.get().findTestBuilder(TypeUtils.getPackageFromFqClassName(className) + "." + shortClassName);
    if (fi != null) {
      return fi.getClassName();
    }
    return shortClassName;

  }

  @Override
  public void clearValidationMessages()
  {
    super.clearValidationMessages();
    for (MethodInfo mi : getMethodInfos()) {
      mi.clearValidationMessages();
    }
  }

  @Override
  public void clearValidationMessagesWithPrefix(String prefix)
  {
    super.clearValidationMessagesWithPrefix(prefix);
    for (MethodInfo mi : getMethodInfos()) {
      mi.clearValidationMessagesWithPrefix(prefix);
    }
  }

  public void replaceOrAddMethod(MethodInfo method)
  {
    MethodInfo om = findMethodInfo(method.getMethodName());
    if (om != null) {
      removeMethodInfo(om);
    }
    addMethodInfo(method);
  }

  public MethodInfo getMethodInfo(final String methodName, List<String> argTypes)
  {
    MethodInfo mi = findMethodInfo(methodName, argTypes);
    if (mi != null) {
      return mi;
    }
    throw new UnsupportedOperationException("Die Methode:"
        + methodName
        + " kann nicht in der Klasse:"
        + getClassName()
        + " aufgelöst werden.");

  }

  public MethodInfo findMethodInfo(final String methodName, List<String> argTypes)
  {
    Collection<MethodInfo> methods = getAllMethodInfos(methodName);

    if (org.apache.commons.collections.CollectionUtils.isEmpty(methods)) {
      return null;
    }

    // Nur eine Methode mit identischem Namen
    if (methods.size() == 1) {
      return methods.iterator().next();
    }

    // Wenn die Parameter unterstüzt werden, wird die Methode zurück
    // gegeben.
    for (MethodInfo methodInfo : methods) {
      if (methodInfo.matchArgTypes(argTypes)) {
        return methodInfo;
      }
    }
    return null;
  }

  /**
   * Alle verfügbaren Methoden der Klasse (incl. der Mehtoden aus den Oberklassen) die dem übergebenen Methodennamen entsprechen.
   * 
   * @param methodName der übergebene Methodenname
   * @return eine Liste der {@link MethodInfo}s die dem übergebenen Methodennamen entsprechen.
   */
  private Collection<MethodInfo> getAllMethodInfos(final String methodName)
  {
    List<MethodInfo> methods = getAllMethodInfos();

    Collection<MethodInfo> overloadedMethods = CollectionUtils.select(methods, new Predicate<MethodInfo>() {
      @Override
      public boolean evaluate(MethodInfo mInfo)
      {
        return StringUtils.equals(methodName, mInfo.getMethodName());
      }
    });

    return overloadedMethods;
  }

  /**
   * Alle verfügbaren Methoden der Klasse (incl. der Mehtoden aus den Oberklassen)
   * 
   * @return eine Liste mit den {@link MethodInfo}s der {@link FileInfo}
   */
  private List<MethodInfo> getAllMethodInfos()
  {
    List<MethodInfo> methods = new ArrayList<MethodInfo>();
    methods.addAll(methodInfos);

    FileInfo currentFInfo = this;
    TpsbEnvironment env = TpsbEnvironment.get();
    currentFInfo = env.resolveSuper(currentFInfo);
    while (currentFInfo.getSuperClassFileInfo() != null) {
      methods.addAll(currentFInfo.getSuperClassFileInfo().getMethodInfos());
      currentFInfo = currentFInfo.getSuperClassFileInfo();
      currentFInfo = env.resolveSuper(currentFInfo);
    }
    return methods;
  }

  public void updateMethodToParentReferences()
  {
    for (MethodInfo mi : methodInfos) {
      mi.setClassInfo(this);
    }
  }

  public void addMethodInfo(MethodInfo mInfo)
  {
    mInfo.setClassInfo(this);
    getMethodInfos().add(mInfo);
    dirty = true;
  }

  public void removeMethodInfo(MethodInfo mi)
  {
    getMethodInfos().remove(mi);
    mi.setClassInfo(null);
    dirty = true;
  }

  public void addImport(String fqImport)
  {
    if (getImports().contains(fqImport) == true) {
      return;
    }
    imports.add(fqImport);
    importsMap = null;
  }

  public String getClassName()
  {
    return className;
  }

  public void setClassName(String className)
  {
    this.className = className;
  }

  public String getShortClassName()
  {
    if (className.indexOf('.') == -1) {
      return className;
    }
    return StringUtils.substringAfterLast(className, ".");
  }

  public void setMethodInfos(List<MethodInfo> methodInfos)
  {
    this.methodInfos = methodInfos;
  }

  public List<MethodInfo> getMethodInfos()
  {
    return methodInfos;
  }

  public MethodInfo getMethodInfo(final String methodName)
  {
    return getMethodInfo(methodName, Collections.EMPTY_LIST);
  }

  public MethodInfo findMethodInfo(final String methodName)
  {
    return findMethodInfo(methodName, Collections.EMPTY_LIST);
  }

  public void setSuperClassFileInfo(FileInfo superClassFileInfo)
  {
    this.superClassFileInfo = superClassFileInfo;
    if (superClassFileInfo != null) {
      superClassName = superClassFileInfo.getClassName();
    }
  }

  public FileInfo getSuperClassFileInfo()
  {
    return superClassFileInfo;
  }

  @Override
  public boolean equals(Object obj)
  {
    if (obj instanceof FileInfo == false) {
      return false;
    }
    if (obj == this) {
      return true;
    }
    final FileInfo lhs = (FileInfo) obj;
    final EqualsBuilder eq = new EqualsBuilder();
    eq.append(className, lhs.className);
    return eq.isEquals();
  }

  @Override
  public int hashCode()
  {
    HashCodeBuilder hb = new HashCodeBuilder();
    hb.append(className);
    return hb.toHashCode();
  }

  @Override
  public String toString()
  {
    ToStringBuilder tb = new ToStringBuilder(this, ToStringStyle.SIMPLE_STYLE);
    tb.append(className).append(getMethodInfos());
    return tb.toString();
  }

  public String getSuperClassName()
  {
    return superClassName;
  }

  public String getFqSuperClassName()
  {
    return resolveFqClassInfoFromImports(superClassName);
  }

  public void setSuperClassName(String superClassName)
  {
    this.superClassName = superClassName;
  }

  public List<String> getImports()
  {
    return imports;
  }

  public void setImports(List<String> imports)
  {
    this.imports = imports;
    this.importsMap = null;
  }

  public Map<String, String> getImportsMap()
  {
    if (importsMap == null) {
      importsMap = new HashMap<String, String>();
      for (String imp : imports) {
        importsMap.put(TypeUtils.getShortClassName(imp), imp);
      }
    }
    return importsMap;
  }

  public void setImportsMap(Map<String, String> importsMap)
  {
    this.importsMap = importsMap;
  }

  public List<String> getSuperTemplateArgs()
  {
    return superTemplateArgs;
  }

  public void setSuperTemplateArgs(List<String> superTemplateArgs)
  {
    this.superTemplateArgs = superTemplateArgs;
  }

  public List<String> getTypeArgNames()
  {
    return typeArgNames;
  }

  public void setTypeArgNames(List<String> typeArgNames)
  {
    this.typeArgNames = typeArgNames;
  }

  public boolean isDirty()
  {
    return dirty;
  }

  public void setDirty(boolean dirty)
  {
    this.dirty = dirty;
  }
}