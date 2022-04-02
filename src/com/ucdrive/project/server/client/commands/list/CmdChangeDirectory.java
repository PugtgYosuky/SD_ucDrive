/*
    UCDrive

    Alunos:
        Joana Simoes, 2019217013
        Samuel Carinhas, 2019217199
*/

package com.ucdrive.project.server.client.commands.list;

import java.io.File;
import java.io.IOException;

import com.ucdrive.project.server.client.ClientThread;
import com.ucdrive.project.server.client.User;
import com.ucdrive.project.server.client.commands.Command;
import com.ucdrive.project.server.client.commands.CommandAction;
import com.ucdrive.project.server.client.commands.CommandDescription;
import com.ucdrive.project.server.client.commands.CommandHandler;
import com.ucdrive.project.server.ftp.sync.FileType;
import com.ucdrive.project.server.ftp.sync.SyncFile;

/**
 * The CmdChangeDirectory class is a command handler that allows users to change directories
 */
@CommandDescription(prefix="server-cd", description="Change current directory")
public class CmdChangeDirectory extends CommandHandler{

	/**
     * If the user is in the root directory, and they try to go back, send them an error message.
     * Otherwise, change the user's path to the parent directory, and send them a message
     * 
     * @param command The command object that was parsed.
     * @return The return type is CommandAction. This is an enum that has three values: SUCCESS, ERROR,
     * and INVALID_USAGE.
     */
    @Override
	public CommandAction parse(Command command) throws IOException {
		ClientThread client = command.getClient();
        // This is checking if the user has entered a directory name. If they haven't, then they have
        // not entered the correct number of arguments.
        if(command.getArgsLength() < 2){
            client.sendMessage("Invalid usage. Use: server-cd <dir name>");
            return CommandAction.INVALID_USAGE;
        }

        String newDirectory = command.getArg(1);
        
        User user = client.getUser();
        String target, userPath;
        // This is checking if the user is trying to go back a directory. If they are, then we need to
        // make sure that they aren't trying to go back to the root directory. If they are, then we
        // send them an error message. Otherwise, we can change the user's path to the parent
        // directory, and send them a message.
        if(newDirectory.equals("..")) {
            if(user.getPath().equals("/home")) {
                client.sendMessage("Invalid move");
                return CommandAction.ERROR;
            }
            target = user.getAbsolutePath().substring(0, user.getAbsolutePath().lastIndexOf("/"));
            userPath = user.getPath().substring(0, user.getPath().lastIndexOf("/"));
        } else {
            if(newDirectory.contains("..")) {
                client.sendMessage("Invalid move. You can only go back by one folder at a time...");
                return CommandAction.ERROR;
            }
            target = user.getAbsolutePath() + "/" + newDirectory;
            userPath = user.getPath() + "/" + newDirectory;
        }

        File directory = new File(target);

        // This is checking if the directory exists, and if it is a directory. If it is, then we can
        // change the user's path to the new directory, and send them a message. Otherwise, we send
        // them an error message.
        if(directory.exists() && directory.isDirectory()){
            user.setPath(userPath);
            client.sendMessage("Directory change to: " + user.getPath());
            client.saveUsers();
            commandExecutor.getFileDispatcher().addFile(new SyncFile("accounts.txt", commandExecutor.getServer().getStoragePath() + "/config/accounts.txt", FileType.USER_DATA));
            return CommandAction.SUCCESS;
        }
        
        client.sendMessage("Unknown directory");
		return CommandAction.INVALID_USAGE;
	}
    
}
