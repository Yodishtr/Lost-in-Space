package Model;

import java.util.*;

public class Room {

    private String name;
    private String description;
    private final Map<String, Room> exits;
    private final List<Item> itemList;
    private Optional<Enemy> optionalEnemy;

    public Room(String name, String description, Map<String, Room> exits, List<Item> itemList, Optional<Enemy> enemy) {
        this.name = name;
        this.description = description;
        this.exits = new HashMap<>(exits);
        this.itemList = itemList;
        this.optionalEnemy = enemy;
    }

    public Room(String name, String description) {
        this.name = name;
        this.description = description;
        this.exits = new HashMap<>();
        this.itemList = new ArrayList<>();
        this.optionalEnemy = Optional.empty();
    }

    public Room (String name) {
        this.name = name;
        this.exits = new HashMap<>();
        this.itemList = new ArrayList<>();
        this.optionalEnemy = Optional.empty();
    }

    // Getters
    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Map<String, Room> getExits() {
        return exits;
    }

    public List<Item> getItemList() {
        return itemList;
    }

    public Optional<Enemy> getOptionalEnemy() {
        return optionalEnemy;
    }

    // Setters
    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setExits(Map<String, Room> newExits) {
        this.exits.clear();
        this.exits.putAll(newExits);
    }

    public void removeExit(String exitName) {
        this.exits.remove(exitName);
    }

    public void setItemList(List<Item> newItemList) {
        this.itemList.clear();
        this.itemList.addAll(newItemList);
    }

    public void addItem(Item item) {
        this.itemList.add(item);
    }

    public void removeItem(Item item) {
        this.itemList.remove(item);
    }

    public void setOptionalEnemy(Enemy enemy) {
        this.optionalEnemy = Optional.ofNullable(enemy);
    }

    public void setOptionalEnemy(Optional<Enemy> enemy) {
        if (enemy == null) {
            this.optionalEnemy = Optional.empty();
        } else {
            this.optionalEnemy = enemy;
        }
    }
}
