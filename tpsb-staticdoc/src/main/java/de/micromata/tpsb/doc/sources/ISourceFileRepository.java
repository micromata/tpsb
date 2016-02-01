package de.micromata.tpsb.doc.sources;

import java.util.Collection;

/**
 * Interface für verschiedene Repositories in welchen sich Sourcen befinden können (z.B. File-System, Jars...)
 * 
 * @author Stefan Stützer (s.stuetzer@micromata.com)
 */
public interface ISourceFileRepository {

	  String[] getLocations();
	  
	  void setLocations(String[] locations);

	  Collection<JavaSourceFileHolder> getSources();
}
