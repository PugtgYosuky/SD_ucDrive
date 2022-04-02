package com.ucdrive.project.server.ftp.sync;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import com.ucdrive.project.server.Server;

public class UDPSynchronized extends Thread{
    private Server server;
    private FileDispatcher fileDispatcher;
    private final int SLEEP_TIME = 10000;
    private final int PACKET_SIZE = 16384;
    private DatagramSocket socket;

    public UDPSynchronized(Server server) throws SocketException {
        this.server = server;
        this.socket = new DatagramSocket(server.getSynchronizePort(), server.getMyIp());
        this.socket.setSoTimeout(server.getTimeout());
        this.fileDispatcher = new FileDispatcher(this);
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

    public boolean sendBinaryFile(SyncFile syncFile, String path) {
        try {
            DatagramPacket packet, acknowledge = new DatagramPacket(new byte[PACKET_SIZE * 2], PACKET_SIZE * 2);
            ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream outputStream = new ObjectOutputStream(byteOutputStream);
            File file = new File(syncFile.getAbsolutePath());
            DataInputStream fileData = new DataInputStream(new FileInputStream(file));
            long nBytes = file.length();
            int numberPackets = (int) Math.ceil(nBytes*1.0 / PACKET_SIZE);
            byte[] bytes = new byte[PACKET_SIZE];
            FilePacket filePacket = new FilePacket(1, numberPackets, syncFile.getPath(), true, path);
            int read;
            int i = 1;
            int failOvers = 0;
            while((read = fileData.read(bytes)) != -1) {
                filePacket.setIndex(i);
                filePacket.setBuffer(bytes);
                filePacket.setBufferLength(read);
                outputStream.writeObject(filePacket);
                byte [] buffer = byteOutputStream.toByteArray();
                packet = new DatagramPacket(buffer, buffer.length, server.getOtherIp(), server.getOtherSynchronizePort());
                
                while(failOvers < server.getHeartbeats()) {
                    try {
                        socket.send(packet);
                        byte [] response = new byte [PACKET_SIZE * 2];
                        acknowledge = new DatagramPacket(response, response.length);
                        socket.receive(acknowledge);
                        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(response, 0, acknowledge.getLength());
                        DataInputStream inputStream = new DataInputStream(byteArrayInputStream);
                        int nextIndex = inputStream.readInt();
                        byteArrayInputStream.close();
                        inputStream.close();

                        if(nextIndex-(i+1) > 0) {
                            fileData.skipBytes((nextIndex-(i+1)) * PACKET_SIZE);
                            i = nextIndex;
                        }

                        failOvers = 0;
                        break;
                    } catch(SocketTimeoutException exc) {
                        failOvers++;
                    }
                }

                if(failOvers == server.getHeartbeats()){
                    fileData.close();
                    outputStream.close();
                    byteOutputStream.close();
                    return false;
                }

                byteOutputStream = new ByteArrayOutputStream();
                outputStream = new ObjectOutputStream(byteOutputStream);
                i++;
            }
            fileData.close();
            outputStream.close();
            byteOutputStream.close();
        } catch(IOException exc) {
            exc.printStackTrace();
        }

        return true;
    }
    public boolean sendFolder(SyncFile file, String path) {
        try {
            DatagramPacket packet, acknowledge = new DatagramPacket(new byte[PACKET_SIZE], PACKET_SIZE);
            ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream outputStream = new ObjectOutputStream(byteOutputStream);
            FilePacket filePacket = new FilePacket(1, 1, file.getPath(), false, path);
            int failOvers = 0;
            outputStream.writeObject(filePacket);
            byte [] buffer = byteOutputStream.toByteArray();
            packet = new DatagramPacket(buffer, buffer.length, server.getOtherIp(), server.getOtherSynchronizePort());
            
            while(failOvers < server.getHeartbeats()) {
                try {
                    socket.send(packet);
                    socket.receive(acknowledge);

                    failOvers = 0;
                    break;
                } catch(SocketTimeoutException exc){
                    failOvers++;
                }
            }
            outputStream.close();
            byteOutputStream.close();
            if(failOvers == server.getHeartbeats())
                return false;
            
        } catch(IOException exc) {
            exc.printStackTrace();
        }
        return true;
    }

    public void sendFiles() {
        SyncFile file;

        while((file = fileDispatcher.getFile()) != null) {
            System.out.println("SENDING: " + file.getPath());
            if(file.getType() == FileType.BINARY) {
                if(sendBinaryFile(file, "/disk/users/")) {
                    fileDispatcher.removeFile();
                }else{
                    System.out.println("Time out");
                    try {
                        Thread.sleep(SLEEP_TIME);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }else if(file.getType() == FileType.USER_DATA){
                if(sendBinaryFile(file, "/config/")) {
                    fileDispatcher.removeFile();
                }else{
                    System.out.println("Time out");
                    try {
                        Thread.sleep(SLEEP_TIME);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            } else {
                if(sendFolder(file, "/disk/users/")){
                    fileDispatcher.removeFile();
                }else{
                    System.out.println("Time out");
                    try {
                        Thread.sleep(SLEEP_TIME);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        }
    }
    
    public void receiveFiles() {
        int nextIndex, failOvers = 0;
        PacketHandler packetHandler = new PacketHandler(server);
        while(failOvers < server.getHeartbeats()){
            try {
                byte [] buffer = new byte [PACKET_SIZE * 2];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);
                ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(buffer, 0, packet.getLength());
                ObjectInputStream inputStream = new ObjectInputStream(byteArrayInputStream);
                SyncPacket filePacket = (SyncPacket) inputStream.readObject();

                nextIndex = filePacket.execute(packetHandler);
                ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
                DataOutputStream outputStream = new DataOutputStream(byteOutputStream);
                outputStream.writeInt(nextIndex);
                byte [] acknowledge = byteOutputStream.toByteArray();
                outputStream.close();
                byteOutputStream.close();
                socket.send(new DatagramPacket(acknowledge, acknowledge.length, server.getOtherIp(), server.getOtherSynchronizePort()));
                failOvers = 0;
            } catch(SocketTimeoutException exc) {
                failOvers++;
                if(server.getPrimaryServer())
                    return;
            } catch (IOException exc) {
                System.out.println("Exception");
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        packetHandler.deleteFile();
    }

    @Override
    public void run() {
        while(true){
            if(server.getPrimaryServer()){
                //System.out.println("File dispacher size: " + fileDispatcher.getSize());
                synchronized(this) {
                    while(fileDispatcher.getSize() == 0) {
                        try {
                            this.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                            return;
                        }
                        System.out.println("I'M ALIVE");
                    }
                }
                System.out.println("Trying to send files...");
                sendFiles();
            } else {
                receiveFiles();
            }
        }
    }

}
