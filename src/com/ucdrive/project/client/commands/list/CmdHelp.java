package com.ucdrive.project.client.commands.list;

import java.io.IOException;

import com.ucdrive.project.client.commands.Command;
import com.ucdrive.project.client.commands.CommandAction;
import com.ucdrive.project.client.commands.CommandDescription;
import com.ucdrive.project.client.commands.CommandHandler;

@CommandDescription(prefix="local-help", description="Show the current list of commands in the client side")
public class CmdHelp extends CommandHandler {
    
    @Override
    public CommandAction parse(Command command) throws IOException {
        System.out.println("Command list:");
        for(CommandDescription cd : commandExecutor.getCommands()){
            System.out.println("\t" + cd.prefix() + " - " + cd.description());
        }
		return CommandAction.SUCCESS;
    }

}
