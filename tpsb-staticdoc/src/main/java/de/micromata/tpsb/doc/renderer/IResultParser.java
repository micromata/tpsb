package de.micromata.tpsb.doc.renderer;

import de.micromata.tpsb.doc.parser.ParserResult;

/**
 * Parser-Interface zum Pardern eines Parser-Lauf Ergebnisses
 * 
 * @author Stefan Stützer (s.stuetzer@micromata.com)
 */
public interface IResultParser
{

  ParserResult parseRawFile(String rawDataFileName);

  Object parseRawObjectFile(String rawDataFileName);
}
