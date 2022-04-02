/*
    UCDrive

    Alunos:
        Joana Simoes, 2019217013
        Samuel Carinhas, 2019217199
*/

package com.ucdrive.project.server.ftp;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;

import com.ucdrive.project.server.client.User;
import com.ucdrive.project.server.ftp.sync.FileDispatcher;
import com.ucdrive.project.server.ftp.sync.FileType;
import com.ucdrive.project.server.ftp.sync.SyncFile;

/**
 * The DataThread class is responsible for handling the data transfer between the client and the server
 */
public class DataThread {
    
    private Socket socket;
    private RequestDispatcher requests;
    private DataInputStream inputStream;
    private DataOutputStream outputStream;
    private FileDispatcher fileDispatcher;

    // This is the constructor of the DataThread class. It is responsible for creating the
    // DataInputStream and the DataOutputStream.
    public DataThread(Socket socket, RequestDispatcher requests, FileDispatcher fileDispatcher) throws IOException {
        this.socket = socket;
        this.requests = requests;
        this.inputStream = new DataInputStream(this.socket.getInputStream());
        this.outputStream = new DataOutputStream(this.socket.getOutputStream());
        this.fileDispatcher = fileDispatcher;
    }

    /**
     * Reads the id from the client and searches it on the list of authorized requests
     * 
     * @return The RequestFile founded or null
     */
    public RequestFile authenticate() throws IOException {
        String id = inputStream.readUTF();
        return requests.findRequest(id);
    }
    
    /**
     * Reads the file from the file system and writes it to the output stream
     * 
     * @param requestFile The file that the client is requesting.
     * @return A boolean with the result
     */
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

    /**
     * Upload a file to the disk and adds it to the file dispatcher to be synchronized
     * 
     * @param requestFile The file that was uploaded by the user.
     * @return A boolean with the result
     */
    private boolean uploadFile(RequestFile requestFile) {
        User user = requestFile.getUser();
        String path = user.getUsername() + user.getPath() + "/" + requestFile.getFileName();
        File file = new File(requestFile.getPath());
        try (DataOutputStream fileData = new DataOutputStream(new FileOutputStream(file))) {
            // Reading the data from the input stream and writing it to the output stream.
            byte [] bytes = new byte [1024];
            int read;
            while((read = inputStream.read(bytes)) != -1) {
                fileData.write(bytes, 0, read);
            }
            fileData.close();

            System.out.println("ADD FILE TO FILE DISPATCHER: " + path + " - " + requestFile.getPath());
            // This is adding the file to the file dispatcher. The file dispatcher is responsible for
            // keeping track of the files that are being synchronized.
            SyncFile syncFile = new SyncFile(path, requestFile.getPath(), FileType.BINARY);
            fileDispatcher.addFile(syncFile);
            return true;
        } catch(IOException exc) {
            System.out.println("Error while uploading a file");
            file.delete();
            return false;
        }       
    }

    /**
     * This function is responsible for handling the file transfer with the server. It will first authenticate the user
     * and then either upload or download the file
     */
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
