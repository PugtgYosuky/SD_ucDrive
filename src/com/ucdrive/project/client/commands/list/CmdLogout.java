package com.ucdrive.project.client.commands.list;

import java.io.IOException;

import com.ucdrive.project.client.commands.Command;
import com.ucdrive.project.client.commands.CommandAction;
import com.ucdrive.project.client.commands.CommandDescription;
import com.ucdrive.project.client.commands.CommandHandler;

@CommandDescription(prefix="logout")
public class CmdLogout extends CommandHandler{

	@Override
	public CommandAction parse(Command command) throws IOException {
		commandExecutor.executeServerCommand(command);
		return CommandAction.CLOSE_CONNECTION;
	}
    
}