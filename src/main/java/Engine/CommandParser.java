package Engine;

import Model.Command;
import utilities.SynonymBuilder;

import java.util.*;

public class CommandParser {

    private final Map<String, Command.CommandType> synonymDictionary;
    private Set<String> stopWordSet;

    public CommandParser() {
        synonymDictionary = SynonymBuilder.buildSynonyms("synonyms.txt");
        stopWordSet = SynonymBuilder.stopWordsBuilder("stopwords.txt");
    }

    // need private helpers
    // normalizing, tokenizing (eseentially split the iput into list of words),
    // removal of stop words, resolve the verb by using the synonym map,
    // extract the target, assemble the command object.

    public Command parseCommand(String input) {
        if (input.isEmpty()) {
            return new Command(Command.CommandType.UNKNOWN, null);
        }
        String normalizedInput = normalization(input);
        List<String> tokenizedInput = tokenization(normalizedInput);
        List<String> stopWordFree = stopWordRemoval(tokenizedInput);
        if (stopWordFree.isEmpty()) {
            return new Command(Command.CommandType.UNKNOWN, null);
        }
        Command.CommandType verb = getCommandType(stopWordFree);
        if (verb == Command.CommandType.UNKNOWN) {
            return new Command(Command.CommandType.UNKNOWN, null);
        }
        String target = targetExtraction(stopWordFree);
        return new Command(verb, target);
    }

    private String normalization(String rawInput) {
        String normalizedInput = rawInput.replaceAll("\\p{Punct}", " ");
        return normalizedInput.trim().toLowerCase();
    }

    private List<String> tokenization(String sanitizedInput) {
        if (sanitizedInput.isEmpty()) {
            return new ArrayList<>();
        } else {
            return new ArrayList<>(Arrays.asList(sanitizedInput.split("\\s+")));
        }
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

    private Command.CommandType getCommandType(List<String> wordList) {
        if (wordList.isEmpty()){
            return Command.CommandType.UNKNOWN;
        }
        if (!synonymDictionary.containsKey(wordList.getFirst())){
            return Command.CommandType.UNKNOWN;
        }
        return synonymDictionary.get(wordList.getFirst());
    }

    private String targetExtraction(List<String> wordList) {
        if (wordList.size() <= 1) {
            return null;
        } else {
            return String.join(" ", wordList.subList(1, wordList.size()));
        }
    }

}
