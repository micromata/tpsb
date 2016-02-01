package de.micromata.tpsb.doc.renderer;

import de.micromata.tpsb.doc.ParserConfig;

/**
 * Render-Interface, welches eines entsprechenden Parser zum gerenderten Ergebnis bereitstellt
 * 
 * @author Stefan Stützer (s.stuetzer@micromata.com)
 */
public interface IParseableResultRenderer extends IResultRenderer
{

  public IResultParser getParser();
  /**
   * Render a single FileInfo
   * @param obj
   * @param cfg
   */
  void renderObject(Object obj, ParserConfig cfg);

}
