/*
    UCDrive

    Alunos:
        Joana Simoes, 2019217013
        Samuel Carinhas, 2019217199
*/

package com.ucdrive.project.server.client.commands.list;

import java.io.IOException;

import com.ucdrive.project.server.client.commands.Command;
import com.ucdrive.project.server.client.commands.CommandAction;
import com.ucdrive.project.server.client.commands.CommandHandler;
import com.ucdrive.project.server.client.commands.CommandDescription;

/**
 * Logout from the server
 */
@CommandDescription(prefix="logout", description="Logout from the server")
public class CmdLogout extends CommandHandler {

    /**
     * When the user types "logout", the server will send a message to the client and then close the
     * connection
     * 
     * @param command The command that was sent by the client.
     * @return The return type is CommandAction.CLOSE_CONNECTION. This means that the connection will
     * be closed.
     */
    @Override
    public CommandAction parse(Command command) throws IOException {
        command.getClient().sendMessage("You have been logged out");
        return CommandAction.CLOSE_CONNECTION;
    }

}
