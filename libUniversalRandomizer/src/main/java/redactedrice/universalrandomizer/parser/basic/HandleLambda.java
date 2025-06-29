package redactedrice.universalrandomizer.parser.basic;

// To support simple Lambda Handlers mostly for testing
@FunctionalInterface
public interface HandleLambda {
	// Handles the passed logical line
    void handle(String logicalLine);
}