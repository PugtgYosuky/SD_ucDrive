/*
    UCDrive

    Alunos:
        Joana Simoes, 2019217013
        Samuel Carinhas, 2019217199
*/

package com.ucdrive.project.client.commands.list;

import java.io.IOException;

import com.ucdrive.project.client.commands.Command;
import com.ucdrive.project.client.commands.CommandAction;
import com.ucdrive.project.client.commands.CommandDescription;
import com.ucdrive.project.client.commands.CommandHandler;

/**
 * This class is used to change the client's password 
 */
@CommandDescription(prefix="change-password")
public class CmdChangePassword extends CommandHandler{

    /**
     * If the command has the correct number of arguments, send the command to the server
     * 
     * @param command The command that was parsed.
     * @return A CommandAction indicating that the client should to the login again
     */
    @Override
    public CommandAction parse(Command command) throws IOException {
        // Checks if the command has the correct number of arguments. If it doesn't, it will
        // print out an error message and return `CommandAction.INVALID_USAGE`.
        if(command.getArgsLength() < 2){
            System.out.println("Invalid usage. Use: change-password <password>");
            return CommandAction.INVALID_USAGE;
        }
        // send the command to the server
        commandExecutor.executeServerCommand(command);
        return CommandAction.CHANGE_PASSWORD;
    }
    
}
