package de.micromata.genome.tpsb.httpmockup.testbuilder;

import java.io.File;
import java.io.StringWriter;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

import de.micromata.genome.tpsb.httpmockup.MockHttpServletRequest;

/**
 * 
 * @author Roger Rene Kommer (r.kommer.extern@micromata.de)
 * 
 */
public class HttpSectionParserTest
{
  @Test
  public void testParseHttpScenario()
  {
    try {
      String content = FileUtils.readFileToString(new File("./dev/extrc/tests/httpmockup/HttpScenario1.txt"), "UTF-8");
      HttpScenario scen = new HttpScenario(content);
      MockHttpServletRequest req = scen.getRequest();
      final StringWriter sw = new StringWriter();
      IOUtils.copy(req.getReader(), sw);
      String s = sw.toString();
      s.length();
    } catch (Exception ex) {
      throw new RuntimeException(ex);
    }

  }
}
