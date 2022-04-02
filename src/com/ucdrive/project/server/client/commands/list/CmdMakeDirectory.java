/*
    UCDrive

    Alunos:
        Joana Simoes, 2019217013
        Samuel Carinhas, 2019217199
*/
package com.ucdrive.project.server.client.commands.list;

import com.ucdrive.project.server.client.ClientThread;
import com.ucdrive.project.server.client.User;
import com.ucdrive.project.server.client.commands.*;
import com.ucdrive.project.server.ftp.sync.FileType;
import com.ucdrive.project.server.ftp.sync.SyncFile;

import java.io.File;
import java.io.IOException;

/**
 * Create a new directory in the server
 */
@CommandDescription(prefix="server-mkdir", description="Create a new directory")
public class CmdMakeDirectory extends CommandHandler {

    /**
     * Creates a directory
     * 
     * @param command The command object that was parsed.
     * @return CommandAction
     */
    @Override
    public CommandAction parse(Command command) throws IOException {
        ClientThread client = command.getClient();
        // This is checking if the user has entered the correct number of arguments. If they haven't,
        // then we send a message to the client and return an error.
        if(command.getArgsLength() < 2) {
            client.sendMessage("Invalid usage. Use: server-mkdir <dir name>");
            return CommandAction.INVALID_USAGE;
        }

        User user = client.getUser();
        String target = user.getAbsolutePath() + "/" + command.getArg(1);
        File newDir = new File(target);
        
        // This is checking if the directory already exists. If it does, then we send a message to the
        // client and return an error.
        if(newDir.exists()) {
            client.sendMessage("This directory already exists...");
            return CommandAction.ERROR;
        }

        newDir.mkdir();

        String localPath = user.getUsername() + user.getPath() + "/" + command.getArg(1);

        // This is adding the directory to the file dispatcher. The file dispatcher is responsible for
        // keeping track of all the files to be synced.
        commandExecutor.getFileDispatcher().addFile(new SyncFile(localPath, target, FileType.DIRECTORY));

        client.sendMessage("Directory created");

        return CommandAction.SUCCESS;
    }

}
