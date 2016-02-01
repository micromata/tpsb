package de.micromata.genome.tpsb.httpmockup;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.servlet.ServletOutputStream;

/**
 * Adopted from stripes.
 * 
 * Mock implementation of a ServletOutputStream that just uses a byte array output stream to capture any output and make it available after
 * the test is done.
 * 
 * @author Tim Fennell
 * @since Stripes 1.1
 */
public class MockServletOutputStream extends ServletOutputStream
{
  private ByteArrayOutputStream out = new ByteArrayOutputStream();

  /** Pass through method calls ByteArrayOutputStream.write(int b). */
  @Override
  public void write(int b) throws IOException
  {
    out.write(b);
  }

  /** Returns the array of bytes that have been written to the output stream. */
  public byte[] getBytes()
  {
    return out.toByteArray();
  }

  /** Returns, as a character string, the output that was written to the output stream. */
  public String getString()
  {
    return out.toString();
  }
}
