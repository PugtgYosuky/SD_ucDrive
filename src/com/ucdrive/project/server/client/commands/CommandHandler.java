package com.ucdrive.project.server.client.commands;

import java.io.IOException;

public interface CommandHandler {

    public CommandAction parse(Command command) throws IOException;

}
