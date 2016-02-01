package de.micromata.genome.tpsb.builder;

import java.util.HashMap;
import java.util.Map;

import de.micromata.genome.util.types.Pair;

/**
 * Internal parser to parse a IniLikeScenario from text.
 * 
 * See {@see IniLikeScenario}.
 * 
 * @author Roger Rene Kommer (r.kommer.extern@micromata.de)
 * 
 */
public class IniLikeScenarioParser extends SimpleTextParser
{
  private static final char SECTION_START = '[';

  private static final char SECTION_END = ']';

  /**
   * key to comment, text
   */
  private Map<String, Pair<String, String>> sections = new HashMap<String, Pair<String, String>>();

  public IniLikeScenarioParser(String text)
  {
    super(text);

    parse();
  }

  private void parse()
  {
    String comment = skipToSection();
    while (eof() == false) {
      String sectionName = readSectionName();
      if (sectionName == null) {
        break;
      }
      String sectionBody = readSectionContent(sectionName);
      sections.put(sectionName, Pair.make(comment, sectionBody));
      comment = skipToSection();
    }
  }

  private String readSectionName()
  {
    if (skipUntil(SECTION_START) == false) {
      return null;
    }
    if (ch() == SECTION_START) {
      inc();
    }

    int start = currentIndex();
    if (skipUntil(SECTION_END, '\n', '\r') == SECTION_END) {
      String section = substring(start, currentIndex());
      inc();
      skipNl();
      return section;
    }
    throw new RuntimeException("Cannot find section end");
  }

  private String readSectionContent(String sectionName)
  {
    int start = currentIndex();
    String sectionEnd = "[/" + sectionName + "]";
    if (skipUntil(sectionEnd) == false) {
      throw new RuntimeException("Cannot find end of section: " + sectionEnd);
    }
    String body = substring(start, currentIndex());
    inc(sectionEnd.length());
    skipNl();
    return body;
  }

  private String skipToSection()
  {
    if (ch() == SECTION_START) {
      return "";
    }
    int sidx = currentIndex();
    while (eof() == false) {
      skipLine();
      if (ch() == SECTION_START) {
        return substring(sidx, currentIndex());
      }
    }
    return "";
  }

  public Map<String, Pair<String, String>> getSections()
  {
    return sections;
  }
}
