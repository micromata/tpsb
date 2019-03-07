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

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;

public class TypeUtils
{
  public static String getShortClassName(String fqname)
  {
    int lidx = fqname.lastIndexOf('.');
    if (lidx == -1) {
      return fqname;
    }
    return fqname.substring(lidx + 1);
  }

  /**
   * 
   * @param fqname the full qualified name of the package
   * @return null if no package
   */
  public static String getPackageFromFqClassName(String fqname)
  {
    int lidx = fqname.lastIndexOf('.');
    if (lidx == -1) {
      return null;
    }
    return fqname.substring(0, lidx);
  }

  public static String quoteString(String s)
  {
    if (s == null || s.equals("null") == true) {
      return null;
    }
    return "\"" + s + "\"";
  }

  public static String stripClassEnd(String t)
  {
    if (t == null || t.endsWith(".class") == false) {
      return t;
    }
    return t.substring(0, t.length() - ".class".length());
  }

  public static String appendClassEnd(String t)
  {
    if (t == null) {
      return null;
    }
    if (t.endsWith(".class") == true) {
      return t;
    }
    return t + ".class";
  }

  public static boolean isValidTestName(String s)
  {
    return isValidJavaIdentifier(s, false);
  }

  public static boolean isValidJavaIdentifier(String s, boolean withDots)
  {
    if (StringUtils.isEmpty(s) == true) {
      return false;
    }
    if (Character.isJavaIdentifierStart(s.charAt(0)) == false) {
      return false;
    }
    for (int i = 1; i < s.length(); ++i) {
      char c = s.charAt(i);
      if (Character.isJavaIdentifierPart(c) == false && (withDots == true && c == '.') == false) {
        return false;
      }
    }
    return true;
  }

  public static boolean isValidTestClassName(String s)
  {
    return isValidJavaIdentifier(s, true);
  }

  /**
   * Checks if s is quoted "asdfasdf" and removed it.
   * 
   * @param s the string to unquote
   * @return the string without the quotes
   */
  public static String unqoteString(String s)
  {
    if (s == null) {
      return s;
    }
    if (s.startsWith("\"") == false) {
      return s;
    }
    s = s.substring(1, s.length() - 1);
    s = StringEscapeUtils.unescapeJava(s);
    return s;
  }
}
