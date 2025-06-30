package redactedrice.universalrandomizer.parser;


//File: AliasReplacer.java
public interface AliasHandler extends ReservedWordHandler {
	String replaceAliases(String line);
}
