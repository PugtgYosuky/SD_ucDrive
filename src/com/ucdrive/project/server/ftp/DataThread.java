package com.ucdrive.project.server.ftp;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;

import com.ucdrive.project.server.client.User;
import com.ucdrive.project.server.ftp.sync.FileDispatcher;
import com.ucdrive.project.server.ftp.sync.FileType;
import com.ucdrive.project.server.ftp.sync.SyncFile;

public class DataThread {
    
    private Socket socket;
    private RequestDispatcher requests;
    private DataInputStream inputStream;
    private DataOutputStream outputStream;
    private FileDispatcher fileDispatcher;

    public DataThread(Socket socket, RequestDispatcher requests, FileDispatcher fileDispatcher) throws IOException {
        this.socket = socket;
        this.requests = requests;
        this.inputStream = new DataInputStream(this.socket.getInputStream());
        this.outputStream = new DataOutputStream(this.socket.getOutputStream());
        this.fileDispatcher = fileDispatcher;
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

            fileData.close();
            return true;
        } catch(IOException exc) {
            return false;
        }
    }

    private boolean uploadFile(RequestFile requestFile) {
        User user = requestFile.getUser();
        String path = user.getUsername() + user.getPath() + "/" + requestFile.getFileName();
        try (DataOutputStream fileData = new DataOutputStream(new FileOutputStream(requestFile.getPath()))) {
            byte [] bytes = new byte [1024];
            int read;
            while((read = inputStream.read(bytes)) != -1) {
                fileData.write(bytes, 0, read);
            }
            fileData.close();

            System.out.println("ADD FILE TO FILE DISPATCHER: " + path + " - " + requestFile.getPath());
            SyncFile syncFile = new SyncFile(path, requestFile.getPath(), FileType.BINARY);
            System.out.println("Here");
            fileDispatcher.addFile(syncFile);
            System.out.println("KJADGHAJDGAA");
            
            
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

            boolean transferState;
            if(requestFile.getType() == RequestType.UPLOAD)
                transferState = uploadFile(requestFile);
            else
                transferState = downloadFile(requestFile);
            
            if(transferState)
                requests.removeRequest(requestFile.getUniqueID());
        } catch (IOException e) {
            return;
        }finally {
            try {
                outputStream.close();
				inputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
    }
    
}
