package com.ucdrive.project.server.client.commands.list;

import java.io.IOException;

import com.ucdrive.project.server.client.ClientThread;
import com.ucdrive.project.server.client.commands.Command;
import com.ucdrive.project.server.client.commands.CommandAction;
import com.ucdrive.project.server.client.commands.CommandDescription;
import com.ucdrive.project.server.client.commands.CommandHandler;
import com.ucdrive.project.server.ftp.RequestFile;
import com.ucdrive.project.server.ftp.RequestType;
import com.ucdrive.project.server.storage.User;
import com.ucdrive.project.shared.Transfer;

@CommandDescription(prefix="upload", description="Upload a file")
public class CmdUpload extends CommandHandler {

    @Override
    public CommandAction parse(Command command) throws IOException {
        ClientThread clientThread = command.getClient();
        if(command.getArgsLength() < 3){
            clientThread.sendMessage("Invalid usage. Use: upload <filename>");
            return CommandAction.INVALID_USAGE;
        }
        String fileName = command.getArg(1);
        User user = clientThread.getUser();
        String filePath = user.getAbsolutePath() + "/" + fileName;

        RequestFile requestFile = new RequestFile(user, filePath, RequestType.UPLOAD, fileName);
        commandExecutor.getServerFTP().getRequestDispatcher().addRequest(requestFile);
        clientThread.sendResponse(new Transfer(requestFile.getUniqueID(), fileName, commandExecutor.getServerFTP().getPort(), RequestType.UPLOAD, commandExecutor.getServerFTP().getIp(), command.getArg(2)));
        clientThread.sendMessage("Upload concluded");

        return CommandAction.SUCCESS;
    }
    
}
