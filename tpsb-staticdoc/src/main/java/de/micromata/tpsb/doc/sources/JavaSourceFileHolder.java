package de.micromata.tpsb.doc.sources;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.apache.commons.codec.binary.StringUtils;

/**
 * Holds an source file.
 * 
 * @author Roger Rene Kommer (r.kommer.extern@micromata.de)
 * 
 */
public class JavaSourceFileHolder
{

  enum Source
  {
    FileSystem, Jar
  }

  private Source source;

  private String origin;

  private String filename;

  private String content;

  public JavaSourceFileHolder(String javaFilename, String javaFileContent)
  {
    this.filename = javaFilename;
    this.content = javaFileContent;
  }

  public void setFilename(String fileName)
  {
    this.filename = fileName;
  }

  public String getFilename()
  {
    return filename;
  }

  public void setContent(String content)
  {
    this.content = content;
  }

  public String getContent()
  {
    return content;
  }

  public InputStream getAsInputStream()
  {
    return new ByteArrayInputStream(StringUtils.getBytesUtf8(content));
  }

  public void setSource(Source origin)
  {
    this.source = origin;
  }

  public Source getSource()
  {
    return source;
  }

  public void setOrigin(String path)
  {
    this.origin = path;
  }

  public String getOrigin()
  {
    return origin;
  }

  @Override
  public String toString()
  {
    return source.name() + ":" + origin + " > " + filename;
  }
}
