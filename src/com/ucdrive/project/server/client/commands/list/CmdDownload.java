package com.ucdrive.project.server.client.commands.list;

import java.io.File;
import java.io.IOException;

import com.ucdrive.project.server.client.ClientThread;
import com.ucdrive.project.server.client.User;
import com.ucdrive.project.server.client.commands.Command;
import com.ucdrive.project.server.client.commands.CommandAction;
import com.ucdrive.project.server.client.commands.CommandDescription;
import com.ucdrive.project.server.client.commands.CommandHandler;
import com.ucdrive.project.server.ftp.RequestFile;
import com.ucdrive.project.server.ftp.RequestType;
import com.ucdrive.project.shared.Transfer;

/**
 * This class is responsible for handling the download command
 */
@CommandDescription(prefix="download", description="Download a file")
public class CmdDownload extends CommandHandler {

    /**
     * Send a request to the server to download a file
     * 
     * @param command The command object that was sent by the client
     * @return The return type is CommandAction.SUCCESS. This means that the command was executed
     * successfully.
     */
    @Override
    public CommandAction parse(Command command) throws IOException {
        ClientThread clientThread = command.getClient();
        if(command.getArgsLength() < 3){
            clientThread.sendMessage("Invalid usage. Use: download <filename>");
            return CommandAction.INVALID_USAGE;
        }
        String fileName = command.getArg(1);
        User user = clientThread.getUser();
        String filePath = user.getAbsolutePath() + "/" + fileName;

        // This is checking if the file name contains the string "..". If it does, it will send a
        // message to the client and return an error.
        if(fileName.contains("..")) {
            clientThread.sendMessage("You can't use .. in this command");
            return CommandAction.ERROR;
        }
        File file = new File(filePath);

        // This is checking if the file exists. If it does, it will send a request to the server to
        // download the file and send a response to the client with the credentials to use in the download
        if(file.exists()){
            // Sends a request to the server to download the file
            RequestFile requestFile = new RequestFile(user, filePath, RequestType.DOWNLOAD, fileName);
            commandExecutor.getServerFTP().getRequestDispatcher().addRequest(requestFile);
            // This is sending a response to the client. The response is a Transfer object, which contains
            // the unique ID of the request file, the name of the file, the port of the server, the type of
            // request (downlaod), the IP of the server and the IP of the client.
            clientThread.sendResponse(new Transfer(requestFile.getUniqueID(), fileName, commandExecutor.getServerFTP().getPort(), RequestType.DOWNLOAD, commandExecutor.getServerFTP().getIp(), command.getArg(2)));
            return CommandAction.SUCCESS;
        }
        
        clientThread.sendMessage("File not found");

        return CommandAction.ERROR;
    }
    
}
