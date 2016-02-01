package de.micromata.tpsb.doc.renderer;

import java.io.File;

import org.apache.log4j.Logger;

import com.thoughtworks.xstream.XStream;

import de.micromata.tpsb.doc.parser.FileInfo;
import de.micromata.tpsb.doc.parser.ParserResult;

/**
 * Parser fuer ein durch den XStream Renderer gerendertes Ergebnis
 * 
 * @author Stefan Stützer (s.stuetzer@micromata.com)
 */
public class XStreamParser implements IResultParser
{

  private final static Logger log = Logger.getLogger(XStreamParser.class);

  @Override
  public ParserResult parseRawFile(String rawDataFileName)
  {
    log.info("Lese XML-Rohdatendatei: " + rawDataFileName);
    File rawDataFile = new File(rawDataFileName);
    if (rawDataFile.exists() == false) {
      log.error("Rohdatendatei existiert nicht: " + rawDataFileName);
    }
    XStream xStream = XStreamRenderer.createXStream();
    ParserResult fromXML = (ParserResult) xStream.fromXML(rawDataFile);
    return fromXML;
  }

  @Override
  public Object parseRawObjectFile(String rawDataFileName)
  {
    log.info("Lese XML-Rohdatendatei: " + rawDataFileName);
    File rawDataFile = new File(rawDataFileName);
    if (rawDataFile.exists() == false) {
      log.error("Rohdatendatei existiert nicht: " + rawDataFileName);
    }
    XStream xStream = XStreamRenderer.createXStream();
    Object o = xStream.fromXML(rawDataFile);
    if (o instanceof FileInfo) {
      FileInfo fi = (FileInfo) o;
      fi.updateMethodToParentReferences();
    }
    return o;
  }
}
