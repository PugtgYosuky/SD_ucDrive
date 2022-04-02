package com.ucdrive.project.client.commands;

// A enum class that holds the possible values for the command action.
public enum CommandAction {
    SUCCESS("Success"),
    CLOSE_CONNECTION("Close connection"),
    NOT_FOUND("Command not found"),
    INVALID_USAGE("Invalid usage"),
    ERROR("Error"), 
    CHANGE_PASSWORD("Password changed"),
    RETRY("Retry to connect");

    private String string;

    // Constructor for the enum class. It is used to initialize the string value of the enum.
    CommandAction(String string) {
        this.string = string;
    }

    /**
     * Returns a string representation of the object
     * 
     * @return The string representation of the object.
     */
    @Override
    public String toString() {
        return this.string;
    }
}
