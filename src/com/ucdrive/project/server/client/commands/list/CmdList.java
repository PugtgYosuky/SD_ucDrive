package com.ucdrive.project.server.client.commands.list;

import java.io.File;
import java.io.IOException;

import com.ucdrive.project.server.client.ClientThread;
import com.ucdrive.project.server.client.User;
import com.ucdrive.project.server.client.commands.Command;
import com.ucdrive.project.server.client.commands.CommandAction;
import com.ucdrive.project.server.client.commands.CommandDescription;
import com.ucdrive.project.server.client.commands.CommandHandler;

@CommandDescription(prefix="ls", description="List file in the current directory")
public class CmdList extends CommandHandler {

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
