package com.ucdrive.project.server.client.commands.list;

import java.io.IOException;

import com.ucdrive.project.server.ServerFTP;
import com.ucdrive.project.server.client.ClientThread;
import com.ucdrive.project.server.client.User;
import com.ucdrive.project.server.client.commands.Command;
import com.ucdrive.project.server.client.commands.CommandAction;
import com.ucdrive.project.server.client.commands.CommandDescription;
import com.ucdrive.project.server.client.commands.CommandHandler;
import com.ucdrive.project.server.ftp.RequestFile;
import com.ucdrive.project.server.ftp.RequestType;
import com.ucdrive.project.shared.Transfer;

@CommandDescription(prefix="upload", description="Upload a file")
public class CmdUpload extends CommandHandler {

    @Override
    public CommandAction parse(Command command) throws IOException {
        String fileName = command.getArg(1);
        ClientThread clientThread = command.getClient();
        User user = clientThread.getUser();
        String filePath = user.getAbsolutePath() + "/" + fileName;

        RequestFile requestFile = new RequestFile(user, filePath, RequestType.UPLOAD, fileName);
        ServerFTP.getRequestDispatcher().addRequest(requestFile);
        clientThread.sendResponse(new Transfer(requestFile.getUniqueID(), fileName, ServerFTP.getPort(), RequestType.UPLOAD, ServerFTP.getIp()));
        clientThread.sendMessage("Upload concluded");

        return CommandAction.SUCCESS;
    }
    
}
