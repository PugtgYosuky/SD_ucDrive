package com.ucdrive.project.server.ftp;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;

public class DataThread {
    
    private Socket socket;
    private RequestDispatcher requests;
    private DataInputStream inputStream;
    private DataOutputStream outputStream;

    public DataThread(Socket socket, RequestDispatcher requests) throws IOException {
        this.socket = socket;
        this.requests = requests;
        this.inputStream = new DataInputStream(this.socket.getInputStream());
        this.outputStream = new DataOutputStream(this.socket.getOutputStream());
    }

    public RequestFile authenticate() throws IOException {
        String id = inputStream.readUTF();
        return requests.findRequest(id);
    }
    
    private boolean downloadFile(RequestFile requestFile) {
        try (DataInputStream fileData = new DataInputStream(new FileInputStream(requestFile.getPath()))) {
            byte[] bytes = new byte[1024];
    
            int read;
            
            while((read = fileData.read(bytes)) != -1) {
                outputStream.write(bytes, 0, read);
            }
            
            return true;
        } catch(IOException exc) {
            return false;
        }
    }

    private boolean uploadFile(RequestFile requestFile) {
        try (DataOutputStream fileData = new DataOutputStream(new FileOutputStream(requestFile.getPath()))) {
            byte [] bytes = new byte [1024];
            int read;
            while((read = inputStream.read(bytes)) != -1) {
                fileData.write(bytes, 0, read);
            }
            
            return true;
        } catch(IOException exc) {
            return false;
        }       
    }

    public void start() {
        RequestFile requestFile;
        try {
            requestFile = authenticate();
            if(requestFile == null){
                return;
            }
            outputStream.writeBoolean(true);
        } catch (IOException e) {
            return;
        }

        boolean transferState;
        if(requestFile.getType() == RequestType.UPLOAD)
            transferState = uploadFile(requestFile);
        else
            transferState = downloadFile(requestFile);
        
        if(transferState)
            requests.removeRequest(requestFile.getUniqueID());
    }
    
}
