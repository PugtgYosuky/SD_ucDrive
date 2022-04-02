/*
    UCDrive

    Alunos:
        Joana Simoes, 2019217013
        Samuel Carinhas, 2019217199
*/

package com.ucdrive.project.server.client.commands.list;

import java.io.IOException;

import com.ucdrive.project.server.client.ClientThread;
import com.ucdrive.project.server.client.commands.Command;
import com.ucdrive.project.server.client.commands.CommandAction;
import com.ucdrive.project.server.client.commands.CommandDescription;
import com.ucdrive.project.server.client.commands.CommandHandler;

/**
 * List all the commands available
 */
@CommandDescription(prefix="server-help", description="List all the commands available")
public class CmdHelp extends CommandHandler {

   /**
    * It sends the list of commands to the user
    * 
    * @param command The command that was executed.
    * @return CommandAction.SUCCESS.
    */
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