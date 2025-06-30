package redactedrice.universalrandomizer.parser;


//File: AliasReplacer.java
public interface AliasModule extends ParserModule {
	String replaceAliases(String line);
	boolean isAlias(String alias);
}
