package com.ucdrive.project.server.client.commands.list;

import com.ucdrive.project.server.client.ClientThread;
import com.ucdrive.project.server.client.commands.*;

import java.io.File;
import java.io.IOException;

@CommandDescription(prefix="mkdir", description="Create a new directory")
public class CmdMakeDirectory extends CommandHandler {

    @Override
    public CommandAction parse(Command command) throws IOException {
        ClientThread client = command.getClient();
        if(command.getArgsLength() < 1) {
            client.sendMessage("Invalid usage. Use: mkdir <dir name>");
            return CommandAction.INVALID_USAGE;
        }

        String target = client.getUser().getAbsolutePath() + "/" + command.getArg(1);
        File newDir = new File(target);
        
        if(newDir.exists()) {
            client.sendMessage("This directory already exists...");
            return CommandAction.ERROR;
        }

        newDir.mkdir();

        client.sendMessage("Directory created");

        return CommandAction.SUCCESS;
    }

}
