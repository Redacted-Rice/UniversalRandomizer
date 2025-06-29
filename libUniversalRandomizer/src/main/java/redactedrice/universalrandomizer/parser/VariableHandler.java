package redactedrice.universalrandomizer.parser;

import java.util.Collection;

public interface VariableHandler extends LiteralHandler {
	boolean contains(String var);
	Collection<String> getVariables();
}
