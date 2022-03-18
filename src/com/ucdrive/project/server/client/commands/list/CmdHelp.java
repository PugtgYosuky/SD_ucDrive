package com.ucdrive.project.server.client.commands.list;

import java.io.IOException;

import com.ucdrive.project.server.client.ClientThread;
import com.ucdrive.project.server.client.commands.Command;
import com.ucdrive.project.server.client.commands.CommandAction;
import com.ucdrive.project.server.client.commands.CommandDescription;
import com.ucdrive.project.server.client.commands.CommandHandler;

@CommandDescription(prefix="server-help", description="List all the commands available")
public class CmdHelp extends CommandHandler {

    @Override
    public CommandAction parse(Command command) throws IOException {
        ClientThread client = command.getClient();
        client.sendMessage("Command list:");
        for(CommandDescription cd : commandExecutor.getCommands()){
            client.sendMessage("\t" + cd.prefix() + " - " + cd.description());
        }
		return CommandAction.SUCCESS;
    }

}