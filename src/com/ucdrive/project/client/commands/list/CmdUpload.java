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
 * Upload a file to the server
 */
@CommandDescription(prefix="upload", description = "Upload a file to the server")
public class CmdUpload extends CommandHandler{

    /**
     * If the file exists and is not a directory, then it will send the command to the server
     * 
     * @param command The command that was entered by the user.
     * @return The command action is being returned. If the command action is success, then the command
     * was successfully sent to the server. If the command action is error, then the file was not
     * found. If the command action is invalid usage, then the user has
     * entered an invalid usage.
     */
    @Override
    public CommandAction parse(Command command) throws IOException {
        // This is checking if the user has entered the correct number of arguments. If they have not,
        // then it will print out the correct usage and return an invalid usage command action.
        if(command.getArgsLength() < 2) {
            System.out.println("Invalid usage. Use: upload <file name>");
            return CommandAction.INVALID_USAGE;
        }
        String path = commandExecutor.getClient().getPath() + "/" + command.getArg(1);
        File file = new File(path);
        // Checks if the file exists and if it is a directory. If it is a directory, then it
        // will print out an error message. If it is not a directory, then it will send the file to the
        // server.
        if(file.exists() && !file.isDirectory()) {
            // Adds the path to the command that was entered by the user. Then it will
            // send the command on the server.
            command.setCommand(command.getCommand() + " " + path);
            commandExecutor.executeServerCommand(command);
            return CommandAction.SUCCESS;
        }else if (file.isDirectory()){
            System.out.println("Cannot send a directory");
            return CommandAction.ERROR;
        }else{
            System.out.println("File not found");
            return CommandAction.ERROR;
        }
    }
    
}
