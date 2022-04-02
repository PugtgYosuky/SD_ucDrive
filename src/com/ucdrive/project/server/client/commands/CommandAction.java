package com.ucdrive.project.server.client.commands;


// A Java enum.
public enum CommandAction {


    SUCCESS("Success"),
    CLOSE_CONNECTION("Close connection"),
    NOT_FOUND("Command not found"),
    INVALID_USAGE("Invalid usage"),
    ERROR("Error");

    private String string;

    CommandAction(String string) {
        this.string = string;
    }

    @Override
    public String toString() {
        return this.string;
    }

}
