package com.ucdrive.project.server.client.commands.list;

import java.io.IOException;

import com.ucdrive.project.client.commands.Command;
import com.ucdrive.project.client.commands.CommandAction;
import com.ucdrive.project.client.commands.CommandDescription;
import com.ucdrive.project.client.commands.CommandHandler;

@CommandDescription(prefix="upload", description="Upload a file")
public class CmdUpload extends CommandHandler {

    @Override
    public CommandAction parse(Command command) throws IOException {
        
        return null;
    }
    
}
