package redactedrice.universalrandomizer.parser.basic;

import java.util.function.Predicate;

import redactedrice.universalrandomizer.parser.Parser;
import redactedrice.universalrandomizer.parser.ParserModule;

/** A named DSL‐line handler carrying a back‐pointer to its parser. */
public abstract class BaseModule implements ParserModule {
    private final String name;
    protected Parser parser;

    protected BaseModule(String name, Predicate<String> predicate) {
        this.name = name;
    }

    /** The unique name you gave this handler. */
    public String getName() {
        return name;
    }

	@Override
    public void setParser(Parser parser) {
    	this.parser = parser;
    }
}