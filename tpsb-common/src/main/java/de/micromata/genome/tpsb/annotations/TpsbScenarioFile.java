package de.micromata.genome.tpsb.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Marks an method, which has argument with a scenario file.
 * 
 * In test documentation this will be used to print.
 * 
 * The file will be lookup in the project root pathes. (-pr in StaticTestDocGenerator).
 * 
 * @author Roger Rene Kommer (r.kommer.extern@micromata.de)
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface TpsbScenarioFile {

  /**
   * Pattern to build the file name.
   * 
   * ${0} to ${n} addresses the arguments.
   * 
   * @return
   */
  String filePattern() default "${0}";

  /**
   * fully qualified class name of the renderer. Has to be implement ScenarioRenderer.
   * 
   * @return
   */
  String scenarioRendererClass() default "de.micromata.genome.tpsb.builder.PreTextScenarioRenderer";
}
