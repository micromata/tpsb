package de.micromata.tpsb.doc.renderer;

import de.micromata.tpsb.doc.ParserConfig;
import de.micromata.tpsb.doc.ParserContext;

/**
 * Render-Interface zum Rendern und Persistieren eines Parser-Lauf Ergebnisses
 * 
 * @author Stefan Stützer (s.stuetzer@micromata.com)
 */
public interface IResultRenderer {
	
	void renderResult(ParserContext ctx, ParserConfig cfg);

	void setOutputFilename(String outputFileName);

	String getOutputFilename();
	
	String getFileExtension();
}
