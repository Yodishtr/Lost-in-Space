package Engine;

import Model.Enemy;
import Model.Item;
import Model.Room;
import utilities.StoriesBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WorldBuilder {
    public static final String CARGO_BAY = "Cargo Bay";
    public static final String ENGINEERING = "Engineering";
    public static final String ARMORY = "Armory";
    public static final String AIRLOCK = "Airlock";
    public static final String MEDICAL_BAY = "Medical Bay";
    public static final String NORTH = "NORTH";
    public static final String EAST = "EAST";
    public static final String SOUTH = "SOUTH";
    public static final String WEST = "WEST";
    // use the storiesbuilder utility class to fill out the description of each room

    public static Map<String, Room> buildWorld() {
        String[] roomNames = {AIRLOCK, ARMORY, CARGO_BAY, ENGINEERING, MEDICAL_BAY};
        String[] storylineNames = {"AIRLOCK", "ARMORY", "CARGO_BAY", "ENGINEERING", "MEDICAL_BAY"};
        Map<String, Room> rooms = new HashMap<>();
        int i = 0;
        for (String roomName : roomNames) {
            Room currentRoom = new Room(roomName);
            String currentStorylineName = storylineNames[i];
            String description = StoriesBuilder.getStory(currentStorylineName, "storylines.txt");
            currentRoom.setDescription(description);
            rooms.put(roomName, currentRoom);
            i += 1;
        }
        // for each room need to add their respective items and their enemy
        for (String roomName : rooms.keySet()) {
            Room currentRoom = rooms.get(roomName);
            String[] directions = {NORTH, EAST, SOUTH, WEST};
            switch (roomName) {
                case AIRLOCK:
                    for (String direction : directions) {
                        if (direction.equals(NORTH)) {
                            currentRoom.addExit(direction, rooms.get(CARGO_BAY));
                        } else if (direction.equals(EAST)) {
                            currentRoom.addExit(direction, rooms.get(MEDICAL_BAY));
                        } else if (direction.equals(SOUTH)) {
                            currentRoom.addExit(direction, rooms.get(ARMORY));
                        } else if (direction.equals(WEST)) {
                            currentRoom.addExit(direction, rooms.get(ENGINEERING));
                        }
                    }
                    break;
                case ARMORY:
                    for (String direction : directions) {
                        if (direction.equals(NORTH)) {
                            currentRoom.addExit(direction, rooms.get(AIRLOCK));
                        } else if (direction.equals(EAST)) {
                            currentRoom.addExit(direction, rooms.get(MEDICAL_BAY));
                        } else if (direction.equals(SOUTH)) {
                            currentRoom.addExit(direction, null);
                        } else if (direction.equals(WEST)) {
                            currentRoom.addExit(direction, rooms.get(ENGINEERING));
                        }
                    }
                    Enemy securityDrone = new Enemy("Security Drone", "Multitool",
                            "The drone identifies you as an intruder and eliminates you",
                            "The Multitool bypasses the drone’s security system, " +
                                    "disabling it completely.");
                    currentRoom.setOptionalEnemy(securityDrone);
                    break;
                case CARGO_BAY:
                    for (String direction : directions) {
                        if (direction.equals(NORTH)) {
                            currentRoom.addExit(direction, null);
                        } else if (direction.equals(EAST)) {
                            currentRoom.addExit(direction, rooms.get(MEDICAL_BAY));
                        } else if (direction.equals(SOUTH)) {
                            currentRoom.addExit(direction, rooms.get(AIRLOCK));
                        } else if (direction.equals(WEST)) {
                            currentRoom.addExit(direction, rooms.get(ENGINEERING));
                        }
                    }
                    Item spareOxygenTank = new Item("Spare Oxygen Tank", "A portable reserve of " +
                            "breathable oxygen left among the cargo supplies. " +
                            "Essential equipment for surviving aboard a ship whose life-support systems are failing.",
                            true);
                    Item multiTool = new Item("Multitool", "A compact maintenance tool built for " +
                            "repairing and accessing ship systems. " +
                            "Its diagnostic interface can be used to disable the Security Drone in the Armory.",
                            false);
                    currentRoom.addItem(spareOxygenTank);
                    currentRoom.addItem(multiTool);
                    break;
                case ENGINEERING:
                    for (String direction : directions) {
                        if (direction.equals(NORTH)) {
                            currentRoom.addExit(direction, rooms.get(CARGO_BAY));
                        } else if (direction.equals(EAST)) {
                            currentRoom.addExit(direction, rooms.get(AIRLOCK));
                        } else if (direction.equals(SOUTH)) {
                            currentRoom.addExit(direction, rooms.get(ARMORY));
                        } else if (direction.equals(WEST)) {
                            currentRoom.addExit(direction, null);
                        }
                    }
                    Item sedativeInjector = new Item("Sedative Injector", "A medical injector " +
                            "containing a powerful sedative. " +
                            "It can incapacitate the Infected Crewmate without forcing you into a " +
                            "dangerous confrontation.", false);
                    Item fuelCell = new Item("Fuel Cell", "A high-density power cell designed to supply " +
                            "emergency energy to ship systems. Valuable for restoring power needed " +
                            "to escape the vessel.", true);
                    currentRoom.addItem(sedativeInjector);
                    currentRoom.addItem(fuelCell);
                    break;
                case MEDICAL_BAY:
                    for (String direction : directions) {
                        if (direction.equals(NORTH)) {
                            currentRoom.addExit(direction, rooms.get(CARGO_BAY));
                        } else if (direction.equals(EAST)) {
                            currentRoom.addExit(direction, null);
                        } else if (direction.equals(SOUTH)) {
                            currentRoom.addExit(direction, rooms.get(ARMORY));
                        } else if (direction.equals(WEST)) {
                            currentRoom.addExit(direction, rooms.get(AIRLOCK));
                        }
                    }
                    Enemy infectedCrewMate = new Enemy("Infected Crewmate", "Sedative Injector",
                            "The crewmate lunges at you, infecting you as well.",
                            "The sedative takes effect, and the infected crewmate collapses " +
                                    "unconscious");
                    currentRoom.setOptionalEnemy(infectedCrewMate);
                    break;
            }
        }
        return rooms;
    }
}
