/*
    UCDrive

    Alunos:
        Joana Simoes, 2019217013
        Samuel Carinhas, 2019217199
*/

package com.ucdrive.project.client.commands;

import com.ucdrive.project.client.Client;

/**
 * A command that is sent by the client to the server
 */
public class Command {
    private String command;
    private String[] args;
    private Client client;

    // This is the constructor of the class. It is used to create a new instance of the class.
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

    /**
     * Returns the command that was executed
     * 
     * @return The command that was passed in.
     */
    public String getCommand() {
        return this.command;
    }

    /**
     * Sets the command property of the `Command` class
     * 
     * @param command The command to send
     */
    public void setCommand(String command) {
        this.command = command;
    }

    /**
     * Returns the first argument passed to the function
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
     * @return The string value of the argument at the specified position.
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
