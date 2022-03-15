package com.ucdrive.project.server.client.commands.list;

import java.io.IOException;

import com.ucdrive.project.server.client.ClientThread;
import com.ucdrive.project.server.client.commands.CommandDescription;
import com.ucdrive.project.server.client.commands.CommandHandler;
import com.ucdrive.project.server.client.commands.Command;
import com.ucdrive.project.server.client.commands.CommandAction;

@CommandDescription(prefix="change-password", description="Change the current client's password")
public class CmdChangePassword extends CommandHandler {

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
