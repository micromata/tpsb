package de.micromata.genome.junittools.wicket;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.apache.wicket.Page;

/**
 * Tag a WicketPageBuilder class, which wicket page it wrapps.
 * 
 * @author Roger Rene Kommer (r.kommer.extern@micromata.de)
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface TpsbWicketPage {
  Class< ? extends Page> value();
}
