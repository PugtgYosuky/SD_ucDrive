package com.ucdrive.project.client.commands.list;

import java.io.File;
import java.io.IOException;

import com.ucdrive.project.client.Client;
import com.ucdrive.project.client.commands.Command;
import com.ucdrive.project.client.commands.CommandAction;
import com.ucdrive.project.client.commands.CommandDescription;
import com.ucdrive.project.client.commands.CommandHandler;

/**
 * Change the current directory
 */
@CommandDescription(prefix="local-cd", description="Change the current directory")
public class CmdChangeDirectory extends CommandHandler {

    /**
     * Method to change the current directory 
     * If the user enters "cd ..", then the function will change the current directory to the parent
     * directory. Otherwise, it will change the current directory to the directory specified by the
     * user
     * 
     * @param command The command object that was parsed.
     * @return The return type is CommandAction.
     */
    @Override
    public CommandAction parse(Command command) throws IOException {
        Client client = command.getClient();
        if(command.getArgsLength() < 2){
            System.out.println("Invalid usage. Use: cd <dir name>");
            return CommandAction.INVALID_USAGE;
        }

        String newDirectory = command.getArg(1);
        
        File directory;
        // This is checking if the user entered "cd ..". If they did, then the function will change the
        // current directory to the parent directory. Otherwise, it will change the current directory
        // to the directory specified by the user.
        if(newDirectory.equals("..")) {
            
            directory = new File(client.getPath()).getParentFile();
            if(directory == null) {
                System.out.println("Invalid move");
                return CommandAction.ERROR;
            }
        } else {
            directory = new File(client.getPath() + "/" + newDirectory);
        }

        // This is checking if the directory exists and if it is a directory. If it is, then the
        // client's path is set to the new directory.
        if(directory.exists() && directory.isDirectory()){
            client.setPath(directory.getAbsolutePath());
            System.out.println("Directory change to: " + client.getPath());
            return CommandAction.SUCCESS;
        }
        
        System.out.println("Unknown directory");
        
		return CommandAction.ERROR;
	}
    
}
