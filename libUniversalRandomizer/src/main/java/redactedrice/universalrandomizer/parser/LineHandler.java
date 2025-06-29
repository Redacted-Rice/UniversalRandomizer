package redactedrice.universalrandomizer.parser;

// To support simple Lambda Handlers mostly for testing
@FunctionalInterface
public interface LineHandler {
	// Handles the passed logical line
    void handle(String logicalLine);
}