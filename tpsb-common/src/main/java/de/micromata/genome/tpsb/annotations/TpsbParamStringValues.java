package de.micromata.genome.tpsb.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * Mark a parameter with a list of valid input types.
 * 
 * @author roger
 * 
 */
@Target({ ElementType.PARAMETER})
public @interface TpsbParamStringValues {
  String[] value();
}
