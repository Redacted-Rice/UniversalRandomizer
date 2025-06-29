package redactedrice.universalrandomizer.parser.basic;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

import redactedrice.universalrandomizer.parser.Parser;
import redactedrice.universalrandomizer.parser.ParserModule;

/** A named DSL‐line handler carrying a back‐pointer to its parser. */
public abstract class BaseModule implements ParserModule {
    private final String name;
    private final Predicate<String> predicate;
    protected final Set<String> reservedWords;
    protected Parser parser;

    protected BaseModule(String name, Predicate<String> predicate) {
        this.name = name;
        this.predicate = predicate;
        this.reservedWords = new HashSet<>();
    }

	@Override
	public boolean matches(String logicalLine) {
		return predicate.test(logicalLine);
	}

    /** The unique name you gave this handler. */
    public String getName() {
        return name;
    }
    
    /**
     * Any keywords or tokens this handler “reserves.”  If two handlers
     * both reserve the same word, the parser will emit a warning.
     */
    public Set<String> getReservedWords() {
        return reservedWords;
    }

	@Override
    public void setParser(Parser parser) {
    	this.parser = parser;
    }
}