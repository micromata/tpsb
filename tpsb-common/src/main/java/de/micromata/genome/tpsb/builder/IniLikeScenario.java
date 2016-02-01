package de.micromata.genome.tpsb.builder;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;

import de.micromata.genome.util.runtime.RuntimeIOException;
import de.micromata.genome.util.types.Pair;

/**
 * Uses a utf-8 text file to store scenario data.
 * 
 * The file is devided by sections. Each section has following form
 * 
 * <pre>
 * Section comment
 * [SectionName]
 * section data
 * [/SectionName]
 * </pre>
 * 
 * One section with a name must only occours one.
 * 
 * All text outside a section are ignored and can be used for comments.
 * 
 * @author Roger Rene Kommer (r.kommer.extern@micromata.de)
 * 
 */
public class IniLikeScenario
{
  private String text;

  /**
   * key -> Comment, Body
   */
  private Map<String, Pair<String, String>> sections = new HashMap<String, Pair<String, String>>();

  public IniLikeScenario(String text)
  {
    this.text = text;
    sections = new IniLikeScenarioParser(text).getSections();
  }

  public String getSectionContent(String sectionName)
  {
    Pair<String, String> pair = sections.get(sectionName);
    if (pair == null) {
      return null;
    }
    return pair.getSecond();
  }

  public String getSectionComment(String sectionName)
  {
    Pair<String, String> pair = sections.get(sectionName);
    if (pair == null) {
      return null;
    }
    return pair.getFirst();
  }

  public Map<String, String> getSectionAsProperties(String sectionName)
  {
    Map<String, String> ret = new HashMap<String, String>();
    String text = getSectionContent(sectionName);
    if (StringUtils.isEmpty(text) == true) {
      return ret;
    }
    Properties props = new Properties();
    try {
      props.load(new StringReader(text));

      ret.putAll((Map) props);
      return ret;
    } catch (IOException ex) {
      throw new RuntimeIOException(ex);
    }
  }

  public Map<String, Pair<String, String>> getSections()
  {
    return sections;
  }
}
