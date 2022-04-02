/*
    UCDrive

    Alunos:
        Joana Simoes, 2019217013
        Samuel Carinhas, 2019217199
*/

package com.ucdrive.project.server;

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

import com.ucdrive.project.server.ftp.sync.FileDispatcher;
import com.ucdrive.project.server.ftp.sync.FilePacket;
import com.ucdrive.project.server.ftp.sync.FileType;
import com.ucdrive.project.server.ftp.sync.PacketHandler;
import com.ucdrive.project.server.ftp.sync.SyncFile;
import com.ucdrive.project.server.ftp.sync.SyncPacket;

/**
 * The UDPSynchronized class is a thread that is responsible for sending and receiving files from the
 * other server
 */
public class UDPSynchronized extends Thread{
    private Server server;
    private FileDispatcher fileDispatcher;
    private final int SLEEP_TIME = 10000;
    private final int PACKET_SIZE = 16384;
    private DatagramSocket socket;

    // This is the constructor of the UDPSynchronized class. It creates a new DatagramSocket and sets
    // the timeout to the value specified in the server object.
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

    /**
     * Send a file to the other server
     * 
     * @param syncFile The file to be sent.
     * @param path The path to the file to be sent.
     * @return A boolean with the result
     */
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
                
                // The above code is sending a packet to the server and receiving an acknowledgement.
                // If the acknowledgement is not received, the code will retry sending the packet until it reaches the max failOvers possibles
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

                        // This is skipping the bytes that were sent
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

                // This is checking if the failOvers is equal to the number of heartbeats. If it is, it
                // means that the server is not responding. So, it closes the file and the streams and
                // returns false.
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
    /**
     * Send a folder to the other server
     * 
     * @param file The folder to send
     * @param path The path of the folder to be sent.
     * @return A boolean with the result
     */
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
            
            // Sends a packet to the server and waits for an acknowledgement.
            // If the server doesn't respond, it will increment the failOvers variable and try again.
            // If the failOvers variable reaches the number of heartbeats, exits.
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

    /**
     * Tries to send all the files in the FileDispatcher to the other server
     */
    public void sendFiles() {
        SyncFile file;

        // Sends files to the other server
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
                        e.printStackTrace();
                    }
                }
            }
        }
    }
    
    // Receiving files from the primary server.
    public void receiveFiles() {
        int nextIndex, failOvers = 0;
        PacketHandler packetHandler = new PacketHandler(server);
        while(failOvers < server.getHeartbeats()){
            try {
                byte [] buffer = new byte [PACKET_SIZE * 2];
                // Receiving a packet from the socket and execute it's handler.
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);
                ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(buffer, 0, packet.getLength());
                ObjectInputStream inputStream = new ObjectInputStream(byteArrayInputStream);
                SyncPacket filePacket = (SyncPacket) inputStream.readObject();
                nextIndex = filePacket.execute(packetHandler);
                ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
                // Says to the primary server the next index it should send
                DataOutputStream outputStream = new DataOutputStream(byteOutputStream);
                outputStream.writeInt(nextIndex);
                byte [] acknowledge = byteOutputStream.toByteArray();
                outputStream.close();
                byteOutputStream.close();
                socket.send(new DatagramPacket(acknowledge, acknowledge.length, server.getOtherIp(), server.getOtherSynchronizePort()));
                failOvers = 0;
            } catch(SocketTimeoutException exc) {
                failOvers++;
                // The above code is checking if the server is the primary server. If it is, then it
                // will return.
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

   /**
    * If the server is the primary server, then it will send files to the secondary server.
    * Otherwise, it will receive files from the primary server
    */
    @Override
    public void run() {
        // A server that receives files from clients and sends them to the clients.
        while(true){
            if(server.getPrimaryServer()){
                synchronized(this) {
                    while(fileDispatcher.getSize() == 0) {
                        try {
                            this.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                            return;
                        }
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
