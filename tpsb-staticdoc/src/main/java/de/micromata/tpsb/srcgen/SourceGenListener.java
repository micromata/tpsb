package de.micromata.tpsb.srcgen;

import de.micromata.tpsb.doc.parser.MethodInfo;
import de.micromata.tpsb.doc.parser.TestStepInfo;

/**
 * Will be called while generating source via SourceGenerator.
 * 
 * @author Roger Rene Kommer (r.kommer.extern@micromata.de)
 * 
 */
public class SourceGenListener
{
  public void beforeGenerateTestStep(SourceGenerator gen, int currentStepCount, MethodInfo mi, TestStepInfo ts, String previusBuilder)
  {

  }

  public void afterGenerateTestStep(SourceGenerator gen, int currentStepCount, MethodInfo mi, TestStepInfo ts, String previusBuilder)
  {

  }

  public void beforeGenerateTestMethod(SourceGenerator gen, MethodInfo mi)
  {

  }

  public void afterGenerateTestMethod(SourceGenerator gen, MethodInfo mi)
  {

  }
}
