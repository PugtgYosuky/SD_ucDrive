package com.ucdrive.project.client.commands.list;

import java.io.IOException;

import com.ucdrive.project.client.commands.Command;
import com.ucdrive.project.client.commands.CommandAction;
import com.ucdrive.project.client.commands.CommandDescription;
import com.ucdrive.project.client.commands.CommandHandler;

/**
 * Download a file from the server
 */
@CommandDescription(prefix="download", description="Download a file from the server")
public class CmdDownload extends CommandHandler {

    /**
     * It downloads a file to the server.
     * 
     * @param command The command that was executed.
     * @return The CommandAction.SUCCESS is returned when the command is executed successfully.
     */
    @Override
    public CommandAction parse(Command command) throws IOException {
        // It checks if the command has the right number of arguments. If it doesn't, it prints an
        // error message and returns the `CommandAction.INVALID_USAGE` value.
        if(command.getArgsLength() < 2) {
            System.out.println("Invalid usage. Use: upload <file name>");
            return CommandAction.INVALID_USAGE;
        }
        
        // Adding the path of the current directory to the command.
        String path = commandExecutor.getClient().getPath() + "/" + command.getArg(1);
        command.setCommand(command.getCommand() + " " + path);
        
        commandExecutor.executeServerCommand(command);

        return CommandAction.SUCCESS;
    }

}
