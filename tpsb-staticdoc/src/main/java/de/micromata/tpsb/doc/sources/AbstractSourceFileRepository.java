package de.micromata.tpsb.doc.sources;


public abstract class AbstractSourceFileRepository implements ISourceFileRepository {

	private String[] locations;

	public AbstractSourceFileRepository(String[] locations) {
		this.locations = locations;
	}
	
	public String[] getLocations() {
		return this.locations;
	}

	public void setLocations(String[] locations) {
		this.locations = locations;
	}
}