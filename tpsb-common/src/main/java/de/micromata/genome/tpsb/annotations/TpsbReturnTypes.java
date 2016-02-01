package de.micromata.genome.tpsb.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * In case of T goTo(Class<T> target) defines possibility of target types.
 * 
 * @author roger
 * 
 */
@Target({ ElementType.METHOD})
public @interface TpsbReturnTypes {
  Class< ? >[] value();
}
