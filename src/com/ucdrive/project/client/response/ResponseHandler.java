package com.ucdrive.project.client.response;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;

import com.ucdrive.project.client.Client;
import com.ucdrive.project.server.ftp.RequestType;
import com.ucdrive.project.shared.Message;
import com.ucdrive.project.shared.Transfer;

public class ResponseHandler {

    private Client client;

    public ResponseHandler(Client client) {
        this.client = client;
    }

    public void execute(Message message) {
        System.out.println(message.getMessage());
    }

    public boolean uploadFile(Transfer transfer, DataOutputStream outputStream, DataInputStream inputStream) {
        try (DataInputStream fileData = new DataInputStream(new FileInputStream(client.getPath() + "/" + transfer.getFilename()))) {
            byte[] bytes = new byte[1024];

            int read;

            while((read = fileData.read(bytes)) != -1) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                outputStream.write(bytes, 0, read);
            }
            
            fileData.close();
            return true;
        } catch(IOException exc) {
            System.out.println("Problem while sending files. Please do again");
            exc.printStackTrace();
            return false;
        }
    }

    public boolean downloadFile(Transfer transfer, DataOutputStream outputStream, DataInputStream inputStream) {
        File file = new File(client.getPath() + "/" + transfer.getFilename());
        try (DataOutputStream fileData = new DataOutputStream(new FileOutputStream(file))){
            byte[] bytes = new byte[1024];

            int read;

            while((read = inputStream.read(bytes)) != -1) {
                fileData.write(bytes, 0, read);
            }
            fileData.close();
            return true;
        } catch(IOException exc) {
            System.out.println("Error while downloading a file");
            file.delete();
            return false;
        }
    }

    public void execute(Transfer transfer) {
        
        try (Socket socket = new Socket(transfer.getIp(), transfer.getPort());
            DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
            DataInputStream inputStream = new DataInputStream(socket.getInputStream())) {

            outputStream.writeUTF(transfer.getId());
            if(inputStream.readBoolean()){
                if(transfer.getType() == RequestType.UPLOAD)
                    uploadFile(transfer, outputStream, inputStream);
                else
                    downloadFile(transfer, outputStream, inputStream);
            }

            outputStream.close();
            inputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
