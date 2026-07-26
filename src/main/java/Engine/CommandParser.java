package Engine;

import utilities.SynonymBuilder;

import java.util.*;

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

    private String normalization(String rawInput) {
        String normalizedInput = rawInput.replaceAll("\\p{Punct}", " ");
        return normalizedInput.trim().toLowerCase();
    }

    private List<String> tokenization(String sanitizedInput) {
        String[] wordArray = sanitizedInput.split(" ");
        return new ArrayList<>(Arrays.asList(wordArray));
    }

    private List<String> stopWordRemoval(List<String> wordList) {
        List<String> stopWordFree = new ArrayList<>();
        for (String word : wordList) {
            if (!stopWordSet.contains(word)) {
                stopWordFree.add(word);
            }
        }
        return stopWordFree;
    }



}
