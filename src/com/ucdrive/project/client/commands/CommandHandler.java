package com.ucdrive.project.client.commands;

import java.io.IOException;

public abstract class CommandHandler {

    public static CommandExecutor commandExecutor;

    public abstract CommandAction parse(Command command) throws IOException;

}
