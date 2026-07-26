package Model;

public class Enemy {

    private String name;
    private String requiredItemName;
    private boolean resolved;
    private String failureMessage;
    private String successMessage;

    public Enemy(String name, String requiredItemName, boolean resolved, String failureMessage, String successMessage) {
        this.name = name;
        this.requiredItemName = requiredItemName;
        this.resolved = resolved;
        this.failureMessage = failureMessage;
        this.successMessage = successMessage;
    }

    public Enemy(String name, String requiredItemName, boolean resolved) {
        this.name = name;
        this.requiredItemName = requiredItemName;
        this.resolved = resolved;
    }

    // Getters
    public String getName() {
        return name;
    }

    public String getRequiredItemName() {
        return requiredItemName;
    }

    public boolean isResolved() {
        return resolved;
    }

    public String getFailureMessage() {
        return failureMessage;
    }

    public String getSuccessMessage() {
        return successMessage;
    }

    // Setters
    public void setName(String name) {
        this.name = name;
    }

    public void setRequiredItemName(String requiredItemName) {
        this.requiredItemName = requiredItemName;
    }

    public void setResolved(boolean resolved) {
        this.resolved = resolved;
    }

    public void setFailureMessage(String failureMessage) {
        this.failureMessage = failureMessage;
    }

    public void setSuccessMessage(String successMessage) {
        this.successMessage = successMessage;
    }
}

