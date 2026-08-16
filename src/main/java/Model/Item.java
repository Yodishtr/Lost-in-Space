package Model;

public class Item {

    private String name;
    private String description;
    private boolean requiredToWin;

    public Item(String name, String description, boolean requiredToWin) {
        this.name = name;
        this.description = description;
        this.requiredToWin = requiredToWin;
    }

    public Item(String name, boolean requiredToWin) {
        this.name = name;
        this.requiredToWin = requiredToWin;
    }

    public Item (String name) {
        this.name = name;
    }

    // Getters
    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public boolean isRequiredToWin() {
        return requiredToWin;
    }

    // Setters
    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setRequiredToWin(boolean requiredToWin) {
        this.requiredToWin = requiredToWin;
    }
}
