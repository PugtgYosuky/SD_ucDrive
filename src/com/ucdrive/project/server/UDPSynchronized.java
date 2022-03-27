package com.ucdrive.project.server;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.ucdrive.project.server.ftp.sync.FileDispatcher;
import com.ucdrive.project.server.ftp.sync.FileType;
import com.ucdrive.project.server.ftp.sync.SyncFile;

public class UDPSynchronized extends Thread{
    private Server server;
    private FileDispatcher fileDispatcher;
    private final int SLEEP_TIME = 10000;
    private final int PACKET_SIZE = 1024;
    private DatagramSocket socket;

    public UDPSynchronized(Server server, FileDispatcher fileDispatcher) throws SocketException {
        this.server = server;
        this.socket = new DatagramSocket(server.getSynchronizePort(), server.getMyIp());
        this.fileDispatcher = fileDispatcher;
    }
    
    public Server getServer() {
        return server;
    }

    public void setServer(Server server) {
        this.server = server;
    }

    public FileDispatcher getFileDispatcher() {
        return fileDispatcher;
    }
    
    public void setFileDispatcher(FileDispatcher fileDispatcher) {
        this.fileDispatcher = fileDispatcher;
    }

    public void sendBinaryFile(SyncFile syncFile) {
        try {
            ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream outputStream = new ObjectOutputStream(byteOutputStream);
            File file = new File(syncFile.getAbsolutePath());
            DataInputStream fileData = new DataInputStream(new FileInputStream(file));
            long nBytes = file.length();
            int numberPackets = (int) Math.ceil(nBytes / PACKET_SIZE);
            byte[] bytes = new byte[PACKET_SIZE];
            Path path = Paths.get(syncFile.getAbsolutePath());
            long size = Files.size(path);
            int read;
            
            while((read = fileData.read(bytes)) != -1) {
                outputStream.writeObject(null);
                byteOutputStream.reset();
                outputStream.reset();
            }
            
            fileData.close();
        } catch(IOException exc) {
            exc.printStackTrace();
        }
    }

    public void sendFiles() {
        SyncFile file;

        while((file = fileDispatcher.getFile()) != null) {
            if(file.getType() == FileType.BINARY) {
                sendBinaryFile(file);
            } else {
                
            }
        }
    }
    
    public void receiveFiles() {

    }

    @Override
    public void run() {
        while(true){
            if(server.getPrimaryServer()){
                try {
                    Thread.sleep(SLEEP_TIME);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                sendFiles();
            }else {
                receiveFiles();
            }
            
        }
    }
    

}
