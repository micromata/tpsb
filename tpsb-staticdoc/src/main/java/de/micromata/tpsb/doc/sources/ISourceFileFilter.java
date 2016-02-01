package de.micromata.tpsb.doc.sources;

/**
 * Interface für Java Source Filter Implemetierungen
 * 
 * @author Stefan Stützer
 *
 */
public interface ISourceFileFilter {

	boolean matches(JavaSourceFileHolder file);
}
