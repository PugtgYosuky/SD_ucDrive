package com.ucdrive.project.client.commands;

import java.io.IOException;

/**
 * This class is responsible for parsing the command and executing the command.
 */
public abstract class CommandHandler {

    public static CommandExecutor commandExecutor;

    /**
     * It takes a command and returns a command action
     * 
     * @param command The command that is being parsed.
     * @return The CommandAction object that is created by the parse method.
     */
    public abstract CommandAction parse(Command command) throws IOException;

}
