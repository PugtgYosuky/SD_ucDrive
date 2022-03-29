package com.ucdrive.project.client.commands.list;

import java.io.IOException;

import com.ucdrive.project.client.commands.Command;
import com.ucdrive.project.client.commands.CommandAction;
import com.ucdrive.project.client.commands.CommandDescription;
import com.ucdrive.project.client.commands.CommandHandler;

@CommandDescription(prefix="change-password")
public class CmdChangePassword extends CommandHandler{

    @Override
    public CommandAction parse(Command command) throws IOException {
        if(command.getArgsLength() < 2){
            System.out.println("Invalid usage. Use: change-password <password>");
            return CommandAction.INVALID_USAGE;
        }
        commandExecutor.executeServerCommand(command);
        return CommandAction.CHANGE_PASSWORD;
    }
    
}
