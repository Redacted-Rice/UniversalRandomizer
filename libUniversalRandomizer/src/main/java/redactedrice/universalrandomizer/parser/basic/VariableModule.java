package redactedrice.universalrandomizer.parser.basic;

//File: VariableDefinitionHandler.java
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VariableModule extends BaseModule {
	private final static Pattern varDef = Pattern.compile("^\\s*variable\\s+(\\w+)\\s*=\\s*(.+)$");
	
	private final Map<String, Object> variables = new LinkedHashMap<>();
	
	protected VariableModule() {
		super("BasicVariableHandler", line -> varDef.matcher(line).matches());
		reservedWords.add("variable");
	}
	
	/** Retrieve all defined variables (immutable view). */
	public Map<String,Object> getVariables() {
	return Collections.unmodifiableMap(variables);
	}
	
	@Override
	public void handle(String line) {
		Matcher m = varDef.matcher(line);
		if (!m.find()) {
			return;  // shouldn't happen if matches() used right
		}
		
		// Check for previous def
		// TODO: Interface for "reserved" words - commands, alias, and vars?
		// Seems like it would make sense and make checking things easier
		
		String varName = m.group(1);
		String literal = m.group(2).trim();
		
		Object obj = parser.evaluateLiteral(literal);
		if (obj != null) {
			variables.put(varName, obj);
		} else {
			throw new IllegalArgumentException(
			  "VariableHandler: For variable \"" + varName + "\" + cannot parse value: " + literal
			);
		}
	}
}

