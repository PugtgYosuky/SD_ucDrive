package com.ucdrive.project.server.client.commands.list;

import com.ucdrive.project.server.client.ClientThread;
import com.ucdrive.project.server.client.User;
import com.ucdrive.project.server.client.commands.*;
import com.ucdrive.project.server.ftp.sync.FileType;
import com.ucdrive.project.server.ftp.sync.SyncFile;

import java.io.File;
import java.io.IOException;

@CommandDescription(prefix="server-mkdir", description="Create a new directory")
public class CmdMakeDirectory extends CommandHandler {

    @Override
    public CommandAction parse(Command command) throws IOException {
        ClientThread client = command.getClient();
        if(command.getArgsLength() < 1) {
            client.sendMessage("Invalid usage. Use: mkdir <dir name>");
            return CommandAction.INVALID_USAGE;
        }

        User user = client.getUser();
        String target = user.getAbsolutePath() + "/" + command.getArg(1);
        File newDir = new File(target);
        
        if(newDir.exists()) {
            client.sendMessage("This directory already exists...");
            return CommandAction.ERROR;
        }

        newDir.mkdir();

        String localPath = user.getUsername() + user.getPath() + "/" + command.getArg(1);

        commandExecutor.getFileDispatcher().addFile(new SyncFile(localPath, target, FileType.DIRECTORY));

        client.sendMessage("Directory created");

        return CommandAction.SUCCESS;
    }

}
