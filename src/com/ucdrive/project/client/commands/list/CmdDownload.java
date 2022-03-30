package com.ucdrive.project.client.commands.list;

import java.io.IOException;

import com.ucdrive.project.client.commands.Command;
import com.ucdrive.project.client.commands.CommandAction;
import com.ucdrive.project.client.commands.CommandDescription;
import com.ucdrive.project.client.commands.CommandHandler;

@CommandDescription(prefix="download", description="Download a file from the server")
public class CmdDownload extends CommandHandler {

    @Override
    public CommandAction parse(Command command) throws IOException {
        if(command.getArgsLength() < 2) {
            System.out.println("Invalid usage. Use: upload <file name>");
            return CommandAction.INVALID_USAGE;
        }
        
        String path = commandExecutor.getClient().getPath() + "/" + command.getArg(1);
        command.setCommand(command.getCommand() + " " + path);
        System.out.println(command.getCommand());
        commandExecutor.executeServerCommand(command);

        return CommandAction.SUCCESS;
    }

}
