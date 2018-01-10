package de.micromata.genome.tpsb.soapui;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import de.micromata.genome.tpsb.StartTestBuilder;
import de.micromata.genome.tpsb.annotations.TpsbScenarioFile;

@RunWith(Parameterized.class)
public class TpsbTestSuite
{
  private String name;

  private static String soapUiProject = "TpsbTest-soapui-project";

  private static String baseDir = "./dev/extrc/test";

  public TpsbTestSuite(String name)
  {
    this.name = name;
  }

  /**
   * Die createShipment TestSuite.
   */
  @TpsbScenarioFile(filePattern = "./dev/extrc/tests/soapui/createShipment-soapui-project.xml.tpsbscen.html")
  @Test
  public void testSoapUi()
  {

    String testCaseName = null;
    String testSuiteName = name;
    int idx = testSuiteName.indexOf('.');
    if (idx != -1) {
      testCaseName = testSuiteName.substring(idx + 1);
      testSuiteName = testSuiteName.substring(0, idx);
    }
    new StartTestBuilder()//
        .createBuilder(TestSoapUiTestBuilder.class)//
        .setScenarioBaseDir(baseDir) //
        .setBaseUrl("http://localhost:8080")
        .setWriteRequestResponseLog(true)
        //.setDisableLocalDispatch(true) //
        .executeSoapUI(soapUiProject, testSuiteName, testCaseName) //

    ;
  }

  @Parameters(name = "{0}")
  public static List<Object[]> files()
  {

    List<String> sl = new StartTestBuilder()//
        .createBuilder(TestSoapUiTestBuilder.class)//
        .setScenarioBaseDir(baseDir) //
        .getAllTestCasesForScenario(soapUiProject);
    List<Object[]> ret = new ArrayList<Object[]>();
    for (String tc : sl) {
      ret.add(new Object[] { tc });
    }
    return ret;
  }
}
