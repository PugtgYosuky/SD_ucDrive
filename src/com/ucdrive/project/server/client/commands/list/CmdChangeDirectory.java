package com.ucdrive.project.server.client.commands.list;

import java.io.File;
import java.io.IOException;

import com.ucdrive.project.server.client.ClientThread;
import com.ucdrive.project.server.client.User;
import com.ucdrive.project.server.client.commands.Command;
import com.ucdrive.project.server.client.commands.CommandAction;
import com.ucdrive.project.server.client.commands.CommandDescription;
import com.ucdrive.project.server.client.commands.CommandHandler;

@CommandDescription(prefix="server-cd", description="Change current directory")
public class CmdChangeDirectory extends CommandHandler{

	@Override
	public CommandAction parse(Command command) throws IOException {
		ClientThread client = command.getClient();
        if(command.getArgsLength() < 1){
            client.sendMessage("Invalid usage. Use: cd <dir name>");
            return CommandAction.INVALID_USAGE;
        }

        String newDirectory = command.getArg(1);
        
        User user = client.getUser();
        String target, userPath;
        if(newDirectory.equals("..")) {
            if(user.getPath().equals("/home")) {
                client.sendMessage("Invalid move");
                return CommandAction.ERROR;
            }
            target = user.getAbsolutePath().substring(0, user.getAbsolutePath().lastIndexOf("/"));
            userPath = user.getPath().substring(0, user.getPath().lastIndexOf("/"));
        } else {
            target = user.getAbsolutePath() + "/" + newDirectory;
            userPath = user.getPath() + "/" + newDirectory;
        }

        File directory = new File(target);

        if(directory.exists() && directory.isDirectory()){
            user.setPath(userPath);
            client.sendMessage("Directory change to: " + user.getPath());
            client.saveUsers();
            return CommandAction.SUCCESS;
        }
        client.sendMessage("Unknown directory");
		return CommandAction.INVALID_USAGE;
	}
    
}
