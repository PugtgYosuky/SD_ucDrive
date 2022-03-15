package com.ucdrive.project.server.client.commands;

import java.io.IOException;

public abstract class CommandHandler {

    public static CommandExecutor commandExecutor;

    public abstract CommandAction parse(Command command) throws IOException;

}
