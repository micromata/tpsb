package de.micromata.tpsb.doc.parser.japa.handler;

import japa.parser.ast.Node;
import de.micromata.tpsb.doc.ParserContext;

public interface INodeHandler<T extends Node> {

	public void handle(T node, ParserContext ctx);
}
