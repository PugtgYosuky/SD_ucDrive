package com.ucdrive.project.client.commands.list;

import java.io.IOException;

import com.ucdrive.project.client.commands.Command;
import com.ucdrive.project.client.commands.CommandAction;
import com.ucdrive.project.client.commands.CommandDescription;
import com.ucdrive.project.client.commands.CommandHandler;

/**
 * Show the list of commands in the client side
 */
@CommandDescription(prefix="local-help", description="Show the current list of commands in the client side")
public class CmdHelp extends CommandHandler {
    
    /**
     * Prints out the list of commands that the command executor knows about
     * 
     * @param command The command that was executed.
     * @return CommandAction.SUCCESS
     */
    @Override
    public CommandAction parse(Command command) throws IOException {
        System.out.println("Command list:");
        for(CommandDescription cd : commandExecutor.getCommands()){
            System.out.println("\t" + cd.prefix() + " - " + cd.description());
        }
		return CommandAction.SUCCESS;
    }

}
