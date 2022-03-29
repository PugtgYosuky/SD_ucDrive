package com.ucdrive.project.client.commands.list;

import java.io.File;
import java.io.IOException;

import com.ucdrive.project.client.Client;
import com.ucdrive.project.client.commands.Command;
import com.ucdrive.project.client.commands.CommandAction;
import com.ucdrive.project.client.commands.CommandDescription;
import com.ucdrive.project.client.commands.CommandHandler;

@CommandDescription(prefix="local-cd", description="Change the current directory")
public class CmdChangeDirectory extends CommandHandler {

    @Override
    public CommandAction parse(Command command) throws IOException {
        Client client = command.getClient();
        if(command.getArgsLength() < 2){
            System.out.println("Invalid usage. Use: cd <dir name>");
            return CommandAction.INVALID_USAGE;
        }

        String newDirectory = command.getArg(1);
        
        File directory;
        if(newDirectory.equals("..")) {
            
            directory = new File(client.getPath()).getParentFile();
            if(directory == null) {
                System.out.println("Invalid move");
                return CommandAction.ERROR;
            }
        } else {
            directory = new File(client.getPath() + "/" + newDirectory);
        }

        if(directory.exists() && directory.isDirectory()){
            client.setPath(directory.getAbsolutePath());
            System.out.println("Directory change to: " + client.getPath());
            return CommandAction.SUCCESS;
        }
        
        System.out.println("Unknown directory");
        
		return CommandAction.ERROR;
	}
    
}
