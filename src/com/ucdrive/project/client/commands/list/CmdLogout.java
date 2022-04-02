package com.ucdrive.project.client.commands.list;

import java.io.IOException;

import com.ucdrive.project.client.commands.Command;
import com.ucdrive.project.client.commands.CommandAction;
import com.ucdrive.project.client.commands.CommandDescription;
import com.ucdrive.project.client.commands.CommandHandler;

/**
 * Logout from the server
 */
@CommandDescription(prefix="logout", description = "Logout from the server")
public class CmdLogout extends CommandHandler{

	/**
	 * It executes the command on the server and then closes the connection.
	 * 
	 * @param command The command that was sent to the server.
	 * @return The CommandAction.CLOSE_CONNECTION is returned to tell the client to close the connection.
	 */
	@Override
	public CommandAction parse(Command command) throws IOException {
		commandExecutor.executeServerCommand(command);
		return CommandAction.CLOSE_CONNECTION;
	}
    
}