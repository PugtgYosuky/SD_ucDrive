package com.ucdrive.project.client.commands.list;

import java.io.File;
import java.io.IOException;

import com.ucdrive.project.client.Client;
import com.ucdrive.project.client.commands.Command;
import com.ucdrive.project.client.commands.CommandAction;
import com.ucdrive.project.client.commands.CommandDescription;
import com.ucdrive.project.client.commands.CommandHandler;

@CommandDescription(prefix="local-mkdir", description="Create a directory")
public class CmdMakeDirectory extends CommandHandler {

    @Override
    public CommandAction parse(Command command) throws IOException {
        Client client = command.getClient();
        if(command.getArgsLength() < 1) {
            System.out.println("Invalid usage. Use: mkdir <dir name>");
            return CommandAction.INVALID_USAGE;
        }

        String target = client.getPath() + "/" + command.getArg(1);
        File newDir = new File(target);
        
        if(newDir.exists()) {
            System.out.println("This directory already exists...");
            return CommandAction.ERROR;
        }

        newDir.mkdir();

        System.out.println("Directory created");

        return CommandAction.SUCCESS;
    }
    
}
