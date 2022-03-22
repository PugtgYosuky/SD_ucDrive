package com.ucdrive.project.server.client.commands.list;

import java.io.IOException;

import com.ucdrive.project.server.client.ClientThread;
import com.ucdrive.project.server.client.commands.CommandDescription;
import com.ucdrive.project.server.client.commands.CommandHandler;
import com.ucdrive.project.server.client.commands.Command;
import com.ucdrive.project.server.client.commands.CommandAction;

/**
 * Change the current client's password
 */
@CommandDescription(prefix="change-password", description="Change the current client's password")
public class CmdChangePassword extends CommandHandler {

    /**
     * `parse` is called when the client sends a command to the server. 
     * 
     * The first argument is the command object. 
     * 
     * The second argument is the password. 
     * 
     * The client thread is stored in the client variable. 
     * 
     * The user object is retrieved from the client thread. 
     * 
     * The password is set on the user object. 
     * 
     * The client thread is saved. 
     * 
     * The client is notified that the password has been changed. 
     * 
     * The client is closed.
     * 
     * @param command The command object that was sent to the server.
     * @return Nothing.
     */
    @Override
    public CommandAction parse(Command command) throws IOException {

        String password = command.getArg(1);
        ClientThread client = command.getClient();
        client.getUser().setPassword(password);
        client.sendMessage("Password changed. Please do login again");
        client.saveUsers();

        return CommandAction.CLOSE_CONNECTION;
    }

}
