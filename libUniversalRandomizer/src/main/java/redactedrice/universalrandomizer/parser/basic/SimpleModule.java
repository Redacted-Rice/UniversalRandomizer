package redactedrice.universalrandomizer.parser.basic;

import java.util.function.Predicate;

public class SimpleModule extends BaseModule {
    private final HandleLambda handler;
	
	public SimpleModule(String name, Predicate<String> predicate, HandleLambda handler, String... reservedWords) {
        super(name, predicate);
        this.handler = handler;
        
        for (String word : reservedWords) {
        	this.reservedWords.add(word);
        }
    }

	@Override
	public void handle(String logicalLine) {
		handler.handle(logicalLine);
	}
}
