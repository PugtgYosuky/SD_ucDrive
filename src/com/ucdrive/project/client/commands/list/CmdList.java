/*
    UCDrive

    Alunos:
        Joana Simoes, 2019217013
        Samuel Carinhas, 2019217199
*/

package com.ucdrive.project.client.commands.list;

import java.io.File;
import java.io.IOException;

import com.ucdrive.project.client.commands.Command;
import com.ucdrive.project.client.commands.CommandAction;
import com.ucdrive.project.client.commands.CommandDescription;
import com.ucdrive.project.client.commands.CommandHandler;

/**
 * List the files in the current directory
 */
@CommandDescription(prefix="local-ls", description="List the files in current directory")
public class CmdList extends CommandHandler{

	/**
     * Prints the names of all files in the current directory
     * 
     * @param command The command that was parsed.
     * @return CommandAction.SUCCESS
     */
    @Override
	public CommandAction parse(Command command) throws IOException {
        String directory = command.getClient().getPath();
        File currentDirectory = new File(directory);
        File [] files = currentDirectory.listFiles();
        System.out.println("Files in " + directory);

        for(File file : files)
            System.out.println("\t" + file.getName());
            
		return CommandAction.SUCCESS;
	}   
}
