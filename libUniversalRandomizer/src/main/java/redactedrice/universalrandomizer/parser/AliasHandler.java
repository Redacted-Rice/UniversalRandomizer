package redactedrice.universalrandomizer.parser;

import java.util.Set;

//File: AliasReplacer.java
public interface AliasHandler {
	String replaceAliases(String line);
	boolean contains(String alias);
	Set<String> getAliases();
}
