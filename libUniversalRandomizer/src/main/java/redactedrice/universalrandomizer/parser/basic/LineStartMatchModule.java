package redactedrice.universalrandomizer.parser.basic;

import java.util.HashSet;
import java.util.Set;

import redactedrice.universalrandomizer.parser.Parser;
import redactedrice.universalrandomizer.parser.ParserModule;

/** A named DSL‐line handler carrying a back‐pointer to its parser. */
public abstract class LineStartMatchModule implements ParserModule {
    private final String name;
    protected Parser parser;
    protected final Set<String> reservedWords;

    protected LineStartMatchModule(String name, String... reservedWords) {
        this.name = name;
        
        this.reservedWords = new HashSet<>();
        for (String word : reservedWords) {
        	this.reservedWords.add(word);
        }
    }

    /** The unique name you gave this handler. */
    public String getName() {
        return name;
    }

	@Override
    public void setParser(Parser parser) {
    	this.parser = parser;
    }    

	@Override
	public boolean matches(String logicalLine) {
        if (logicalLine == null || logicalLine.isBlank()) {
        	return false;
        }

        String[] words = logicalLine.trim().split("\\s+", 2);
        return !words[0].isEmpty() && reservedWords.contains(words[0]);
	}

	@Override
	public boolean isReservedWord(String word) {
		return reservedWords.contains(word);
	}

	@Override
	public Set<String> getReservedWords() {
		return reservedWords;
	}
	
}