package com.ucdrive.project.server.client.commands.list;

import java.io.IOException;

import com.ucdrive.project.server.client.commands.Command;
import com.ucdrive.project.server.client.commands.CommandAction;
import com.ucdrive.project.server.client.commands.CommandHandler;
import com.ucdrive.project.server.client.commands.CommandDescription;

@CommandDescription(prefix="logout", description="Logout from the server")
public class CmdLogout extends CommandHandler {

    @Override
    public CommandAction parse(Command command) throws IOException {
        command.getClient().sendMessage("You have been logged out");
        return CommandAction.CLOSE_CONNECTION;
    }

}
