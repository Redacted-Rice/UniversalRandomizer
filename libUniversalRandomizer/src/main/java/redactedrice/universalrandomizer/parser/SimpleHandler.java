package redactedrice.universalrandomizer.parser;

import java.util.function.Predicate;

public class SimpleHandler extends BaseHandler {
    private final LineHandler handler;
	
	public SimpleHandler(String name, Predicate<String> predicate, LineHandler handler, String... reservedWords) {
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
