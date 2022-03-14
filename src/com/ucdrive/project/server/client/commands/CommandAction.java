package com.ucdrive.project.server.client.commands;

public enum CommandAction {


    SUCCESS("Success"), CLOSE_CONNECTION("Close connection");

    private String string;

    CommandAction(String string) {
        this.string = string;
    }

    @Override
    public String toString() {
        return this.string;
    }

}
