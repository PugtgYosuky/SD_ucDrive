package com.ucdrive.project.client.commands;

import com.ucdrive.project.client.Client;

public class Command {
    private String command;
    private String[] args;
    private Client client;

    public Command(String command, Client client) {
        this.command = command;
        this.args = command.split(" ");
        this.client = client;
    }

    public Client getClient(){
        return this.client;
    }

    public void setClient(Client client) {
        this.client = client;
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
