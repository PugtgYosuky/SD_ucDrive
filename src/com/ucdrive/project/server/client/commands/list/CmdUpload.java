/*
    UCDrive

    Alunos:
        Joana Simoes, 2019217013
        Samuel Carinhas, 2019217199
*/

package com.ucdrive.project.server.client.commands.list;

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
 * This class is responsible for handling the upload command
 */
@CommandDescription(prefix="upload", description="Upload a file")
public class CmdUpload extends CommandHandler {

    /**
     * This function is called when the user wants to upload a file. It will create a new request file
     * and add it to the request dispatcher. It will then send a response to the client, which contains
     * the unique ID of the request file, the name of the file, the port of the server, the type of
     * request (upload), and the IP address of the server to use
     * 
     * @param command The command object that was sent by the client.
     * @return The CommandAction.SUCCESS is returned when the command is executed successfully. Otherwise, CommandACtion.INVALID_USAGE
     */
    @Override
    public CommandAction parse(Command command) throws IOException {
        ClientThread clientThread = command.getClient();
        // Checks if the user has provided enough arguments. If not, it will send a message
        // to the user and return `CommandAction.INVALID_USAGE`.
        if(command.getArgsLength() < 3){
            clientThread.sendMessage("Invalid usage. Use: upload <filename>");
            return CommandAction.INVALID_USAGE;
        }
        String fileName = command.getArg(1);
        User user = clientThread.getUser();
        String filePath = user.getAbsolutePath() + "/" + fileName;

        // This is creating a new request file and adding it to the request dispatcher.
        RequestFile requestFile = new RequestFile(user, filePath, RequestType.UPLOAD, fileName);
        commandExecutor.getServerFTP().getRequestDispatcher().addRequest(requestFile);
        // This is sending a response to the client. The response is a Transfer object, which contains
        // the unique ID of the request file, the name of the file, the port of the server, the type of
        // request (upload)
        clientThread.sendResponse(new Transfer(requestFile.getUniqueID(), fileName, commandExecutor.getServerFTP().getPort(), RequestType.UPLOAD, commandExecutor.getServerFTP().getIp(), command.getArg(2)));
        clientThread.sendMessage("Upload concluded");

        return CommandAction.SUCCESS;
    }
    
}
