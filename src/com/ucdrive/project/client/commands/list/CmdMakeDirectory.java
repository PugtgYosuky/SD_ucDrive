/*
    UCDrive

    Alunos:
        Joana Simoes, 2019217013
        Samuel Carinhas, 2019217199
*/

package com.ucdrive.project.client.commands.list;

import java.io.File;
import java.io.IOException;

import com.ucdrive.project.client.Client;
import com.ucdrive.project.client.commands.Command;
import com.ucdrive.project.client.commands.CommandAction;
import com.ucdrive.project.client.commands.CommandDescription;
import com.ucdrive.project.client.commands.CommandHandler;

/**
 * Create a directory in the client side
 */
@CommandDescription(prefix="local-mkdir", description="Create a directory")
public class CmdMakeDirectory extends CommandHandler {

    /**
     * Create a new directory
     * 
     * @param command The command object that was parsed.
     * @return CommandAction
     */
    @Override
    public CommandAction parse(Command command) throws IOException {
        // Checking if the user has entered the correct number of arguments. If they haven't,
        // it will print out an error message and return an invalid usage.
        Client client = command.getClient();
        if(command.getArgsLength() < 2) {
            System.out.println("Invalid usage. Use: local-mkdir <dir name>");
            return CommandAction.INVALID_USAGE;
        }

        String target = client.getPath() + "/" + command.getArg(1);
        File newDir = new File(target);
        
        // Checks if if the directory already exists. If it does, return an
        // error.
        if(newDir.exists()) {
            System.out.println("This directory already exists...");
            return CommandAction.ERROR;
        }

        // This is creating a new directory in the client side.
        newDir.mkdir();

        System.out.println("Directory created");

        return CommandAction.SUCCESS;
    }
    
}
