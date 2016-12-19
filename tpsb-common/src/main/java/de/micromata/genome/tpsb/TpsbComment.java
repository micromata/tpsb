package de.micromata.genome.tpsb;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * A Comment, which can be readed by TPSB static Doc parser.
 * 
 * @author Roger Rene Kommer (r.kommer.extern@micromata.de)
 *
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface TpsbComment {
  String value() default "";
}
