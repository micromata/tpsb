package de.micromata.tpsb.doc.renderer;


/**
 * Abstrakte Implementierung eines Renderers, welche sich um die Persistierung
 * kümmert
 * 
 * @author Stefan Stützer (s.stuetzer@micromata.com)
 */
public abstract class AbstractResultRenderer implements IResultRenderer {

	private String outputFilename;

	public AbstractResultRenderer() {
	}

	public AbstractResultRenderer(String outputFilename) {
		this.outputFilename = outputFilename;
	}

	public void setOutputFilename(String outputFileName) {
		this.outputFilename = outputFileName;
	}

	public String getOutputFilename() {
		return outputFilename;
	}
}