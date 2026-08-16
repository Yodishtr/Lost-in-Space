package Model;

import java.util.HashMap;
import java.util.Map;

public class Player {

    private Room currentRoom;
    private final Map<String, Item> inventory = new HashMap<>();
    private Integer health;
    private String name;
    private Integer enemiesDefeated;
    private Integer enemiesToKillToWin;
    private Integer numberOfItemsRequiredToWin;

    public Player(Room currentRoom, Integer health, String name) {
        this.currentRoom = currentRoom;
        this.health = health;
        this.name = name;
        this.enemiesDefeated = 0;
    }

    public Player(Room currentRoom, Integer health, String name, Integer numberOfItemsRequiredToWin,
                  Integer enemiesToKillToWin) {
        this.currentRoom = currentRoom;
        this.health = health;
        this.name = name;
        this.enemiesDefeated = 0;
        this.numberOfItemsRequiredToWin = numberOfItemsRequiredToWin;
        this.enemiesToKillToWin = enemiesToKillToWin;
    }

    public Player() {}

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

    public Integer getEnemiesToKillToWin() {
        return enemiesToKillToWin;
    }

    public Integer getNumberOfItemsRequiredToWin() {
        return numberOfItemsRequiredToWin;
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

    public void setEnemiesToKillToWin(Integer enemiesToKillToWin) {
        this.enemiesToKillToWin = enemiesToKillToWin;
    }

    public void setNumberOfItemsRequiredToWin(Integer numberOfItemsRequiredToWin) {
        this.numberOfItemsRequiredToWin = numberOfItemsRequiredToWin;
    }
}
