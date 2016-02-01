package de.micromata.genome.tpsb.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Markieren von Methoden, die sowohl als Testmethode, als auch als Builder Methode funktionieren.
 * 
 * @author Roger Kommer (roger.kommer.extern@micromata.de)
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface TpsbMetaMethod {

}
