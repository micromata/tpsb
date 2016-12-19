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

package de.micromata.genome.tpsb.builder;

/**
 * Simple text parser used to parse ini-files.
 * 
 * @author Roger Rene Kommer (r.kommer.extern@micromata.de)
 * 
 */
public class SimpleTextParser
{
  private int idx = 0;

  private String text;

  public SimpleTextParser(String text)
  {
    this.text = text;
  }

  public int currentIndex()
  {
    return idx;
  }

  public final char ch()
  {
    if (idx >= text.length()) {
      return 0;
    }
    return text.charAt(idx);
  }

  public final char inc()
  {
    ++idx;
    return ch();
  }

  public final char inc(int num)
  {
    idx += num;
    return ch();
  }

  public final char skipWs()
  {
    while (eof() == false) {
      char c = ch();
      if (Character.isWhitespace(c) == false) {
        return c;
      }
      inc();
    }
    return 0;
  }

  public void skipToWs()
  {
    while (eof() == false) {
      char c = ch();
      if (Character.isWhitespace(c) == true) {
        return;
      }
      inc();
    }
  }

  public void skipToWsOrNl()
  {
    while (eof() == false) {
      char c = ch();
      if (Character.isWhitespace(c) == true) {
        return;
      }
      if (isNl() == true) {
        return;
      }
      inc();
    }
  }

  public final boolean isNl()
  {
    char c = ch();
    return c == '\n' || c == '\r';
  }

  public final void skipNl()
  {
    char c = ch();
    if (c == '\n') {
      c = inc();
      if (c == '\r') {
        inc();
      }
    } else if (c == '\r') {
      c = inc();
      if (c == '\n') {
        inc();
      }
    }
  }

  public final char ch(int offset)
  {
    if (offset + idx < 0) {
      return 0;
    }
    if (offset + idx >= text.length()) {
      return 0;
    }
    return text.charAt(offset + idx);
  }

  public final boolean eof()
  {
    return idx >= text.length();
  }

  public boolean isEmptyLine()
  {
    return isNl();
  }

  /**
   * point after to begin next line
   */
  public final void skipLine()
  {
    while (eof() == false) {
      if (isNl() == true) {
        skipNl();
        return;
      }
      inc();
    }
  }

  public final void skipEndOfLine()
  {
    while (eof() == false) {
      if (isNl() == true) {
        return;
      }
      inc();
    }

  }

  public boolean skipUntil(char c)
  {
    while (eof() == false) {
      if (ch() == c) {
        return true;
      }
      inc();
    }
    return false;
  }

  /**
   * skip until one of the character can be found.
   * 
   * @param cs
   * @return found character or 0 for endofstream.
   */
  public char skipUntil(char... cs)
  {
    while (eof() == false) {
      char c = ch();
      for (char sc : cs) {
        if (sc == c) {
          return c;
        }
      }
      inc();
    }
    return 0;
  }

  public void reset(int pos)
  {
    idx = pos;
  }

  public boolean skipUntil(String stext)
  {
    String st = text.substring(idx);
    int sidx = st.indexOf(stext);
    if (sidx == -1) {
      idx = text.length();
      return false;
    }
    idx += sidx;
    return true;
  }

  public boolean startsWith(String text)
  {

    for (int i = 0; i < text.length(); ++i) {
      if (ch(i) != text.charAt(i)) {
        return false;
      }
    }
    return true;
  }

  public String substring(int start, int end)
  {
    return text.substring(start, end);
  }

  public String substring(int start)
  {
    return text.substring(start);
  }

  /**
   * rest of the unparsed string.
   * 
   * @return
   */
  public String rest()
  {
    if (eof()) {
      return "";
    }
    return text.substring(idx);
  }
}
