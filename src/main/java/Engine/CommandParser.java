package Engine;

import utilities.SynonymBuilder;

import java.util.Map;
import java.util.Set;

public class CommandParser {

    private final Map<String, Set<String>> synonymDictionary;
    private final Set<String> stopWordSet;

    public CommandParser() {
        synonymDictionary = SynonymBuilder.buildSynonyms("synonyms.txt");
        stopWordSet = SynonymBuilder.stopWordsBuilder("stopwords.txt");
    }

    // need private helpers
    // normalizing, tokenizing (eseentially split the iput into list of words),
    // removal of stop words, resolve the verb by using the synonym map,
    // extract the target, assemble the command object.
}
