//
// Copyright (C) 2010-2016 Micromata GmbH
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//  http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//

package de.micromata.tpsb.doc.renderer;

import java.io.StringWriter;
import java.util.Map;

import org.apache.commons.lang.CharEncoding;
import org.apache.log4j.Logger;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

/**
 * Renderer auf Basis der Velocity Template Engine
 * 
 * @author Stefan Stützer (s.stuetzer@micromata.com)
 */
public class BasicVelocityRenderer {

	private final static Logger log = Logger
			.getLogger(BasicVelocityRenderer.class);

	private String template;

	VelocityEngine ve = new VelocityEngine();

	public BasicVelocityRenderer(String template) {
		this.template = template;
		// ResourceLoader registrieren
		ve.addProperty("resource.loader", "class");
		ve.addProperty("class.resource.loader.class",
				"org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
		ve.addProperty("resource.loader", "file");
		ve.addProperty("file.resource.loader.class",
				"org.apache.velocity.runtime.resource.loader.FileResourceLoader");
		ve.init();
	}

	public byte[] renderResult(Map<String, Object> context) throws Exception {
		VelocityContext ctx = new VelocityContext(context);
		return renderResult(ctx);
	}

	public byte[] renderResult(VelocityContext context) throws Exception {
		Template t = null;
		log.info("Verwende Report-Template " + template);
		t = ve.getTemplate(template, CharEncoding.UTF_8);

		StringWriter writer = new StringWriter();
		t.merge(context, writer);

		return writer.toString().getBytes(CharEncoding.UTF_8);
	}

	public String getFileExtension() {
		return "html";
	}
}
