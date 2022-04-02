package com.ucdrive.project.server.client.commands.list;

import java.io.File;
import java.io.IOException;

import com.ucdrive.project.server.client.ClientThread;
import com.ucdrive.project.server.client.User;
import com.ucdrive.project.server.client.commands.Command;
import com.ucdrive.project.server.client.commands.CommandAction;
import com.ucdrive.project.server.client.commands.CommandDescription;
import com.ucdrive.project.server.client.commands.CommandHandler;

/**
 * List the files in the current server directory
 */
@CommandDescription(prefix="server-ls", description="List file in the current directory")
public class CmdList extends CommandHandler {

	/**
     * Sends the name of all files in the current directory to the user
     * 
     * @param command The command object that is being parsed.
     * @return CommandAction.SUCCESS.
     */
    @Override
	public CommandAction parse(Command command) throws IOException {

		ClientThread client = command.getClient();
        User user = client.getUser();
        File currentDirectory = new File(user.getAbsolutePath());
        File [] files = currentDirectory.listFiles();
        client.sendMessage("Files in "+ user.getPath() + ":");
        for(File file : files)
            client.sendMessage("\t" + file.getName());
		return CommandAction.SUCCESS;
		
	}
    
}
