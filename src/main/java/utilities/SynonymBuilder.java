package utilities;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class SynonymBuilder {

    public static Map<String, Set<String>> buildSynonyms(String synonymsText) {
        try (InputStream inputStream = SynonymBuilder.class.getResourceAsStream("/" + synonymsText))
        {
            if (inputStream == null) {
                throw new NoSuchElementException("cannot find synonyms.txt.txt");
            }
            try (BufferedReader buffReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
                String currentLine;
                Map<String, Set<String>> synonyms = new HashMap<>();
                while ((currentLine = buffReader.readLine()) != null) {
                    String trimmedLine = currentLine.trim();
                    if (!trimmedLine.startsWith("#") && !trimmedLine.isBlank()) {
                        String[] firstSplit = currentLine.split(":", 2);
                        if (firstSplit.length != 2) {
                            throw new IllegalArgumentException("synonyms.txt.txt contains invalid synonym lines: " +
                                    currentLine);
                        }
                        String currentKey = firstSplit[0].trim();
                        synonyms.put(currentKey, new HashSet<>());
                        String[] secondSplit = firstSplit[1].trim().split(",");
                        for (String word : secondSplit) {
                            synonyms.get(currentKey).add(word.trim());
                        }
                    }
                }
                return synonyms;
            }
        } catch (IOException ioException) {
            throw new IllegalStateException("cannot read synonyms.txt", ioException);
        } catch (NoSuchElementException noSuchElementException) {
            throw new IllegalStateException("cannot find synonyms.txt", noSuchElementException);
        } catch (IllegalArgumentException illegalArgumentException) {
            throw new IllegalStateException("invalid lines in synonyms.txt", illegalArgumentException);
        }
    }

    public static Set<String> stopWordsBuilder(String fillerText) {
        try (InputStream inputStream = SynonymBuilder.class.getResourceAsStream("/" + fillerText)) {
            if (inputStream == null) {
                throw new NoSuchElementException("cannot find synonyms.txt.txt");
            }
            try (BufferedReader buffReader = new BufferedReader(new InputStreamReader(inputStream,
                    StandardCharsets.UTF_8))) {
                String currentLine;
                Set<String> stopWords = new HashSet<>();
                while ((currentLine = buffReader.readLine()) != null) {
                    String trimmedLine = currentLine.trim();
                    if (!trimmedLine.startsWith("#") && !trimmedLine.isBlank()) {
                        stopWords.add(trimmedLine.trim());
                    }
                }
                return stopWords;
            }
        } catch (IOException ioException) {
            throw new IllegalStateException("cannot read filler.txt", ioException);
        } catch (NoSuchElementException noSuchElementException) {
            throw new IllegalStateException("cannot find synonyms.txt", noSuchElementException);
        }
    }
}
