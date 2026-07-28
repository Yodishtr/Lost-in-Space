package utilities;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

public class StoriesBuilder {

    public static Map<String, String> getIntro(String fileName) {
        Map<String, String> introLookup = new HashMap<>();
        try (InputStream inputStream = StoriesBuilder.class.getResourceAsStream("/" + fileName)) {
            if (inputStream == null) {
                throw new NoSuchElementException(fileName + " cannot be found");
            }
            try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
                String currentLine;
                while ((currentLine = bufferedReader.readLine()) != null) {
                    String trimmedLine = currentLine.trim();
                    if (!trimmedLine.startsWith("#") && !trimmedLine.isBlank()) {
                        String[] splitLine = trimmedLine.split(":", 2);
                        if (splitLine.length < 2){
                            throw new IllegalArgumentException(fileName + " has a malformed line: " + currentLine);
                        }
                        if (!splitLine[0].contains("INTRO")) {
                            break;
                        }
                        introLookup.put(splitLine[0], splitLine[1]);
                    }
                }
                return introLookup;
            }

        } catch (IOException e) {
            throw new IllegalStateException(fileName + " could not be read", e);
        } catch (NoSuchElementException e) {
            throw new IllegalStateException(fileName + " cannot be found", e);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(fileName + " is invalid", e);
        }
    }

    public static String getStory(String roomName, String fileName) {
        try (InputStream inputStream = StoriesBuilder.class.getResourceAsStream("/" + fileName)) {
            if (inputStream == null) {
                throw new NoSuchElementException(fileName + " cannot be found");
            }
            try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
                String currentLine;
                String story = "";
                while ((currentLine = bufferedReader.readLine()) != null) {
                    String trimmedLine = currentLine.trim();
                    if (!trimmedLine.startsWith("#") && !trimmedLine.isBlank()) {
                        String[] splitLine = trimmedLine.split(":", 2);
                        if (splitLine.length < 2){
                            throw new IllegalArgumentException(fileName + " has a malformed line: " + currentLine);
                        }
                        if (!splitLine[0].contains(roomName)) {
                            continue;
                        }
                        story = splitLine[1];
                    }
                }
                return story;
            }
        } catch (IOException e) {
            throw new IllegalStateException(fileName + " could not be read", e);
        } catch (NoSuchElementException e) {
            throw new IllegalStateException(fileName + " cannot be found", e);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(fileName + " is invalid", e);
        }
    }
}
