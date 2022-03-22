package com.ucdrive.project.server.client.commands;

import com.ucdrive.project.server.client.ClientThread;

/**
 * A command is a string that is sent to the server. The command is split into an array of strings, and
 * the first string is the command name
 */
public class Command {

    private ClientThread client;
    private String command;
    private String[] args;

    public Command(ClientThread client, String command) {
        this.client = client;
        this.command = command;
        this.args = command.split(" ");
    }

    /**
     * Returns the client associated with this thread
     * 
     * @return The client object.
     */
    public ClientThread getClient() {
        return this.client;
    }

    /**
     * Returns the command that was executed
     * 
     * @return The command that was passed in.
     */
    public String getCommand() {
        return this.command;
    }

    /**
     * The `getPrefix` function returns the first argument passed to the function
     * 
     * @return The first argument.
     */
    public String getPrefix() {
        return this.args[0];
    }

    /**
     * Return the argument at the given position
     * 
     * @param pos The position of the argument.
     * @return The string at the specified position in the args array.
     */
    public String getArg(int pos) {
        return this.args[pos];
    }

    /**
     * Returns the number of arguments passed to the function
     * 
     * @return The length of the args array.
     */
    public int getArgsLength() {
        return this.args.length;
    }

}
