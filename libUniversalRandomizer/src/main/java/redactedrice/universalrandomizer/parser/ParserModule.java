package redactedrice.universalrandomizer.parser;

import java.util.Set;

public interface ParserModule {
    boolean matches(String logicalLine);
    void handle(String logicalLine);
    String getName();
    Set<String> getReservedWords();
    void setParser(Parser parser);
}
