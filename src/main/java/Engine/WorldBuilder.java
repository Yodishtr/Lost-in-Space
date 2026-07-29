package Engine;

import Model.Room;
import utilities.StoriesBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class WorldBuilder {
    // use the storiesbuilder utility class to fill out the description of each room

    public static Map<String, Room> buildWorld() {
        String[] roomNames = {"Airlock", "Armory", "Cargo Bay", "Engineering", "Medical Bay"};
        String[] storylineNames = {"AIRLOCK", "ARMORY", "CARGO_BAY", "ENGINEERING", "MEDICAL_BAY"};
        List<Room> rooms = new ArrayList<>();
        int i = 0;
        for (String roomName : roomNames) {
            Room currentRoom = new Room(roomName);
            String currentStorylineName = storylineNames[i];
            String description = StoriesBuilder.getStory(currentStorylineName, "storylines.txt");
            currentRoom.setDescription(description);
            rooms.add(currentRoom);
            i += 1;
        }
        for (Room room : rooms) {

        }
    }
}
