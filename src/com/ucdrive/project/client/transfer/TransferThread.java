package com.ucdrive.project.client.transfer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;

import com.ucdrive.project.server.ftp.RequestType;
import com.ucdrive.project.shared.Transfer;

/**
 * This class is responsible for handling the transfers between the client and the server
 */
public class TransferThread extends Thread {

    private TransferDispatcher transferDispatcher;

    // Constructor of the class. It is responsible for initializing the fields of the
    // class.
    public TransferThread(TransferDispatcher transferDispatcher) {
        this.transferDispatcher = transferDispatcher;
    }

    /**
     * It uploads a file to the server.
     * 
     * @param transfer The transfer object that contains the information about the file to be
     * uploaded.
     * @param outputStream The output stream to which the file is to be written.
     * @param inputStream The input stream of the file to be sent.
     * @return True if the file was successfully send. Otherwise, false
     */
    public boolean uploadFile(Transfer transfer, DataOutputStream outputStream, DataInputStream inputStream) {
        try (DataInputStream fileData = new DataInputStream(new FileInputStream(transfer.getClientPath()))) {
            byte[] bytes = new byte[1024];

            int read;

            // It reads the file and writes it to the output stream.
            while((read = fileData.read(bytes)) != -1) {
                outputStream.write(bytes, 0, read);
            }
            System.out.println("Upload of "+ transfer.getFilename() + " concluded");
            fileData.close();
            return true;
        } catch(IOException exc) {
            System.out.println("Problem while sending files. Please do it again");
            return false;
        }
    }

    /**
     * Downloads a file from the server to the client
     * 
     * @param transfer The transfer object that contains the information about the file to be
     * downloaded.
     * @param outputStream The output stream to write the file to.
     * @param inputStream The input stream from the client.
     * @return The method returns a boolean value. If the file was successfully downloaded, the value
     * is true. Otherwise, the value is false.
     */
    public boolean downloadFile(Transfer transfer, DataOutputStream outputStream, DataInputStream inputStream) {
        File file = new File(transfer.getClientPath());
        try (DataOutputStream fileData = new DataOutputStream(new FileOutputStream(file))){
            byte[] bytes = new byte[1024];

            int read;

            while((read = inputStream.read(bytes)) != -1) {
                fileData.write(bytes, 0, read);
            }
            System.out.println("Download of "+ transfer.getFilename() + " concluded");
            fileData.close();
            return true;
        } catch(IOException exc) {
            System.out.println("Error while downloading a file");
            file.delete();
            return false; 
        }
    }

    /**
     * It sends a request to the server to either upload or download a file.
     * 
     * @param transfer The transfer object that contains the information about the file to be
     * transferred.
     * @return The method returns a boolean value. If the file was successfully uploaded/downloaded, the value
     * is true. Otherwise, the value is false.
     */
    public boolean handleTransfer(Transfer transfer) {
        // Responsible for opening the socket, sending the request to the server and closing the
        // socket.
        try (Socket socket = new Socket(transfer.getIp(), transfer.getPort());
            DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
            DataInputStream inputStream = new DataInputStream(socket.getInputStream())) {

            outputStream.writeUTF(transfer.getId());
            // Checks if the server has accepted the request. If it has, it will send the file to the
            // server.
            if(inputStream.readBoolean()){
                if(transfer.getType() == RequestType.UPLOAD) {
                    if(!uploadFile(transfer, outputStream, inputStream))
                        return false;
                } else {
                    if(!downloadFile(transfer, outputStream, inputStream))
                        return false;
                }
            }

            outputStream.close();
            inputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Transfer handler lost connection");
            return false;
        }
        return true;
    }

    /**
     * It handles the transfer.
     */
    @Override
    public void run() {

       // Waiting for the transferDispatcher to notify it that there is a transfer to be handled.
        while(true){
            synchronized(transferDispatcher){
                while(transferDispatcher.getSize() == 0){
                    try {
                        transferDispatcher.wait();
                    } catch (InterruptedException e) {
                        return;
                    }
                }
            }
            // Responsible for handling the transfers. It will keep on handling the transfers until the
            // transferDispatcher has no transfers to be handled.
            while(transferDispatcher.getSize() != 0) {
                Transfer transfer = transferDispatcher.getTransfer();
                if(!handleTransfer(transfer))
                    return;

                transferDispatcher.removeTransfer();
            }
        }
        
    }
    
}
