package com.ucdrive.project.server.client.commands;

import java.io.IOException;

/**
 * This class is responsible for parsing the command and executing the command.
 */
public abstract class CommandHandler {

    public static CommandExecutor commandExecutor;

    public abstract CommandAction parse(Command command) throws IOException;

}
