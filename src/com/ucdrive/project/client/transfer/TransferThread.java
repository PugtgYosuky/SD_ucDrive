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

public class TransferThread extends Thread {

    private TransferDispatcher transferDispatcher;

    public TransferThread(TransferDispatcher transferDispatcher) {
        this.transferDispatcher = transferDispatcher;
    }

    public boolean uploadFile(Transfer transfer, DataOutputStream outputStream, DataInputStream inputStream) {
        try (DataInputStream fileData = new DataInputStream(new FileInputStream(transfer.getClientPath()))) {
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
            System.out.println("Upload of "+ transfer.getFilename() + " concluded");
            fileData.close();
            return true;
        } catch(IOException exc) {
            System.out.println("Problem while sending files. Please do it again");
            return false;
        }
    }

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

    public boolean handleTransfer(Transfer transfer) {
        try (Socket socket = new Socket(transfer.getIp(), transfer.getPort());
            DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
            DataInputStream inputStream = new DataInputStream(socket.getInputStream())) {

            outputStream.writeUTF(transfer.getId());
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
            System.out.println("Transfer handler lost connection");
            return false;
        }
        return true;
    }

    @Override
    public void run() {

        while(true){
            synchronized(transferDispatcher){
                while(transferDispatcher.getSize() == 0){
                    try {
                        transferDispatcher.wait();
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
            while(transferDispatcher.getSize() != 0) {
                Transfer transfer = transferDispatcher.getTransfer();
                if(!handleTransfer(transfer))
                    return;

                transferDispatcher.removeTransfer();
            }
        }
        
    }
    
}
