package com.ucdrive.project.server.client.commands;

import com.ucdrive.project.server.client.ClientThread;

public class Command {

    private ClientThread client;
    private String command;
    private String[] args;

    public Command(ClientThread client, String command) {
        this.client = client;
        this.command = command;
        this.args = command.split(" ");
    }

    public ClientThread getClient() {
        return this.client;
    }

    public String getCommand() {
        return this.command;
    }

    public String getPrefix() {
        return this.args[0];
    }

    public String getArg(int pos) {
        return this.args[pos];
    }

    public int getArgsLength() {
        return this.args.length;
    }

}
