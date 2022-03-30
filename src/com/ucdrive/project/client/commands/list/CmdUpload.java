package com.ucdrive.project.client.commands.list;

import java.io.File;
import java.io.IOException;

import com.ucdrive.project.client.commands.Command;
import com.ucdrive.project.client.commands.CommandAction;
import com.ucdrive.project.client.commands.CommandDescription;
import com.ucdrive.project.client.commands.CommandHandler;

@CommandDescription(prefix="upload", description = "Upload a file to the server")
public class CmdUpload extends CommandHandler{

    @Override
    public CommandAction parse(Command command) throws IOException {
        if(command.getArgsLength() < 2) {
            System.out.println("Invalid usage. Use: upload <file name>");
            return CommandAction.INVALID_USAGE;
        }
        String path = commandExecutor.getClient().getPath() + "/" + command.getArg(1);
        File file = new File(path);
        if(file.exists() && !file.isDirectory()) {
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
