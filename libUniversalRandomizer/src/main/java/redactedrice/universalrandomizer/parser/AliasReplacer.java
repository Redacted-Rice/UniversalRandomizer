package redactedrice.universalrandomizer.parser;

import java.util.Set;

//File: AliasReplacer.java
public interface AliasReplacer {
	String replaceAliases(String line);
	Set<String> getAliases();
}
