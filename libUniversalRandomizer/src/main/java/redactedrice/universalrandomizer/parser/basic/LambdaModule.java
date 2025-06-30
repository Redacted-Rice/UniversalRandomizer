package redactedrice.universalrandomizer.parser.basic;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

public class LambdaModule extends BaseModule {
    private final ModuleLambdaFn handler;
    protected final Set<String> reservedWords;
	
	public LambdaModule(String name, Predicate<String> predicate, ModuleLambdaFn handler, String... reservedWords) {
        super(name, predicate);
        this.handler = handler;
        
        this.reservedWords = new HashSet<>();
        for (String word : reservedWords) {
        	this.reservedWords.add(word);
        }
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
	public void handle(String logicalLine) {
		handler.handle(logicalLine);
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
