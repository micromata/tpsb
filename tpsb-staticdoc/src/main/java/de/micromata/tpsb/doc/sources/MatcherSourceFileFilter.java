package de.micromata.tpsb.doc.sources;

import japa.parser.JavaParser;
import japa.parser.ParseException;
import japa.parser.ast.CompilationUnit;
import japa.parser.ast.body.ClassOrInterfaceDeclaration;
import japa.parser.ast.visitor.GenericVisitorAdapter;

import org.apache.log4j.Logger;

import de.micromata.genome.util.matcher.BooleanListRulesFactory;
import de.micromata.genome.util.matcher.Matcher;

/**
 * Filter, welcher den Klassennamen der übergebene JavaSource nach einem
 * Matcher-Ausdruck hin untersucht
 * 
 * @author Stefan Stützer (s.stuetzer@micromata.com)
 */
public class MatcherSourceFileFilter implements ISourceFileFilter {

	Logger log = Logger.getLogger(MatcherSourceFileFilter.class);

	private Matcher<String> matcher;

	boolean matches = false;

	public MatcherSourceFileFilter(String pattern) {
		matcher = new BooleanListRulesFactory<String>().createMatcher(pattern);
	}

	@Override
  public synchronized boolean matches(JavaSourceFileHolder file) {
		matches = false;
		try {
			CompilationUnit cu = JavaParser.parse(file.getAsInputStream());
			new ClassNameVisitor().visit(cu, matcher);
		} catch (ParseException e) {
			log.error("Fehler beim Parsen der Quell-Datei. " + e.getMessage());
		}
		return matches;
	}

	class ClassNameVisitor extends GenericVisitorAdapter<Void, Matcher<String>> {
		@Override
		public Void visit(ClassOrInterfaceDeclaration n, Matcher<String> matcher) {
			MatcherSourceFileFilter.this.matches = matcher.match(n.getName());
			return null;
		}
	}
}
