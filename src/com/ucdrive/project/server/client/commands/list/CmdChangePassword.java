package com.ucdrive.project.server.client.commands.list;

import java.io.IOException;

import com.ucdrive.project.server.client.ClientThread;
import com.ucdrive.project.server.client.commands.CommandDescription;
import com.ucdrive.project.server.client.commands.CommandHandler;
import com.ucdrive.project.server.ftp.sync.FileType;
import com.ucdrive.project.server.ftp.sync.SyncFile;
import com.ucdrive.project.server.client.commands.Command;
import com.ucdrive.project.server.client.commands.CommandAction;

/**
 * Change the current client's password
 */
@CommandDescription(prefix="change-password", description="Change the current client's password")
public class CmdChangePassword extends CommandHandler {


    /**
     * It changes the password of the user.
     * 
     * @param command The command object that was sent by the client.
     * @return CommandAction.INVALID_USAGE or CommandACtion.CLOSE_CONNECTION
     */
    @Override
    public CommandAction parse(Command command) throws IOException {
        if(command.getArgsLength() < 2){
            command.getClient().sendMessage("Invalid usage. Use: change-password <password>");
            return CommandAction.INVALID_USAGE;
        }
        // Changing the password of the user.
        String password = command.getArg(1);
        ClientThread client = command.getClient();
        client.getUser().setPassword(password);
        client.saveUsers();
        
       // This is adding the file `accounts.txt` to the file dispatcher. This is so that the file
       // dispatcher can synchronize the file with the second server
        commandExecutor.getFileDispatcher().addFile(new SyncFile("accounts.txt", commandExecutor.getServer().getStoragePath() + "/config/accounts.txt", FileType.USER_DATA));
        
        return CommandAction.CLOSE_CONNECTION;
    }

}
