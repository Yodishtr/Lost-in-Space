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
    // use the storiesbuilder utility class to fill out the description of each room

    public static Map<String, Room> buildWorld() {
        String[] roomNames = {"Airlock", "Armory", "Cargo Bay", "Engineering", "Medical Bay"};
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
            String[] directions = {"North","East", "South", "West" };
            switch (roomName) {
                case "Airlock":
                    for (String direction : directions) {
                        if (direction.equals("North")) {
                            currentRoom.addExit(direction, rooms.get("Cargo Bay"));
                        } else if (direction.equals("East")) {
                            currentRoom.addExit(direction, rooms.get("Medical Bay"));
                        } else if (direction.equals("South")) {
                            currentRoom.addExit(direction, rooms.get("Armory"));
                        } else if (direction.equals("West")) {
                            currentRoom.addExit(direction, rooms.get("Engineering"));
                        }
                    }
                    break;
                case "Armory":
                    for (String direction : directions) {
                        if (direction.equals("North")) {
                            currentRoom.addExit(direction, rooms.get("Airlock"));
                        } else if (direction.equals("East")) {
                            currentRoom.addExit(direction, rooms.get("Medical Bay"));
                        } else if (direction.equals("South")) {
                            currentRoom.addExit(direction, null);
                        } else if (direction.equals("West")) {
                            currentRoom.addExit(direction, rooms.get("Engineering"));
                        }
                    }
                    Enemy securityDrone = new Enemy("Security Drone", "Multitool", false,
                            "The drone identifies you as an intruder and eliminates you",
                            "The Multitool bypasses the drone’s security system, " +
                                    "disabling it completely.");
                    currentRoom.setOptionalEnemy(securityDrone);
                    break;
                case "Cargo Bay":
                    for (String direction : directions) {
                        if (direction.equals("North")) {
                            currentRoom.addExit(direction, null);
                        } else if (direction.equals("East")) {
                            currentRoom.addExit(direction, rooms.get("Medical Bay"));
                        } else if (direction.equals("South")) {
                            currentRoom.addExit(direction, rooms.get("Airlock"));
                        } else if (direction.equals("West")) {
                            currentRoom.addExit(direction, rooms.get("Engineering"));
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
                case "Engineering":
                    for (String direction : directions) {
                        if (direction.equals("North")) {
                            currentRoom.addExit(direction, rooms.get("Cargo Bay"));
                        } else if (direction.equals("East")) {
                            currentRoom.addExit(direction, rooms.get("Airlock"));
                        } else if (direction.equals("South")) {
                            currentRoom.addExit(direction, rooms.get("Armory"));
                        } else if (direction.equals("West")) {
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
                case "Medical Bay":
                    for (String direction : directions) {
                        if (direction.equals("North")) {
                            currentRoom.addExit(direction, rooms.get("Cargo Bay"));
                        } else if (direction.equals("East")) {
                            currentRoom.addExit(direction, null);
                        } else if (direction.equals("South")) {
                            currentRoom.addExit(direction, rooms.get("Armory"));
                        } else if (direction.equals("West")) {
                            currentRoom.addExit(direction, rooms.get("Airlock"));
                        }
                    }
                    Enemy infectedCrewMate = new Enemy("Infected Crewmate", "Sedative Injector",
                            false, "The crewmate lunges at you, infecting you as well.",
                            "The sedative takes effect, and the infected crewmate collapses " +
                                    "unconscious");
                    currentRoom.setOptionalEnemy(infectedCrewMate);
                    break;
            }
        }
        return rooms;
    }
}
