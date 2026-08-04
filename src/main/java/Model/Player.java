package Model;

import java.util.HashMap;
import java.util.Map;

public class Player {

    private Room currentRoom;
    private final Map<String, Item> inventory = new HashMap<>();
    private Integer health;
    private String name;
    private Integer enemiesDefeated;

    public Player(Room currentRoom, Integer health, String name) {
        this.currentRoom = currentRoom;
        this.health = health;
        this.name = name;
        this.enemiesDefeated = 0;
    }

    // Getters
    public Room getCurrentRoom() {
        return currentRoom;
    }

    public Map<String, Item> getInventory() {
        return inventory;
    }

    public Integer getHealth() {
        return health;
    }

    public String getName() {
        return name;
    }

    public Integer getEnemiesDefeated() {
        return enemiesDefeated;
    }

    // Setters
    public void setHealth(Integer health) {
        if (health < 0) {
            this.health = 0;
        } else {
            this.health = health;
        }
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCurrentRoom(Room currentRoom) {
        this.currentRoom = currentRoom;
    }

    public void setInventory(Map<String, Item> inventory) {
        this.inventory.clear();
        this.inventory.putAll(inventory);
    }

    public void addItemToInventory(Item item) {
        this.inventory.put(item.getName(), item);
    }

    public void removeItemFromInventory(Item item) {
        this.inventory.remove(item.getName());
    }

    public void addEnemyDefeated() {
        this.enemiesDefeated++;
    }
}
