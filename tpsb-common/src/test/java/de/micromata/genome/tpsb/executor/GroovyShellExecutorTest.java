package de.micromata.genome.tpsb.executor;

import org.junit.Test;

public class GroovyShellExecutorTest
{

  @Test
  public void testExecuteGroovy()
  {
    GroovyShellExecutor gse = new GroovyShellExecutor();

    String code = "import de.micromata.genome.tpsb.annotations.TpsbTestSuite;\n"
        + "import de.micromata.genome.tpsb.StartTestBuilder;\n"
        + "import org.junit.Test;\r\n"
        + "\r\n"
        + "/**\r\n"
        + " * Tested die autoamatische Zuwahl von Sperrgut\r\n"
        + " *\r\n"
        + " * @author Roger Rene Kommer (r.kommer.extern@micromata.de)\r\n"
        + " */\r\n"
        + "@TpsbTestSuite\r\n"
        + "public class ProductServiceAutoSperrTest\r\n"
        + "{\r\n"
        + "  /**\r\n"
        + "   * Sendung, deren Ausmasse Sperrgut benötigt.\r\n"
        + "   *\r\n"
        + "   */\r\n"
        + "  @Test\r\n"
        + "  public void testAutselect()\r\n"
        + "  {\r\n"
        + "   System.out.println(\"TPSBSTARTMETHOD: com.dpdhl.vls.productmodel.validation.ProductServiceAutoSperrTest.testAutselect\");\r\n"
        + "    StartTestBuilder commonTestBuilder = new StartTestBuilder();\r\n"
        + "   System.out.println(\"TPSBTESTSTEPNUM: 1\");\r\n"
        + "    ;\r\n"
        + " //if (true) throw new RuntimeException(\"oops\");\n"
        + "   System.out.println(\"TPSBENDMETHOD: com.dpdhl.vls.productmodel.validation.ProductServiceAutoSperrTest.testAutselect\");\r\n"
        + "  }\r\n"
        + "}";
    try {
      gse.executeCode(code, null);
    } catch (Exception ex) {
      throw new RuntimeException(ex);
    }
  }

}
