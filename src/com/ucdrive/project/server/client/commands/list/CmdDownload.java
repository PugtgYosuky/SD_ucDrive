package com.ucdrive.project.server.client.commands.list;

import java.io.File;
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

@CommandDescription(prefix="download", description="Download a file")
public class CmdDownload extends CommandHandler {

    @Override
    public CommandAction parse(Command command) throws IOException {
        String fileName = command.getArg(1);
        ClientThread clientThread = command.getClient();
        User user = clientThread.getUser();
        String filePath = user.getAbsolutePath() + "/" + fileName;
        
        File file = new File(filePath);

        if(file.exists()){
            RequestFile requestFile = new RequestFile(user, filePath, RequestType.DOWNLOAD);
            ServerFTP.getRequestDispatcher().addRequest(requestFile);
            clientThread.sendMessage("");
        }
        
        clientThread.sendMessage("File not found");

        return CommandAction.ERROR;
    }
    
}
