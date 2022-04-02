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
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

/**
 * If the server is the primary server, it will receive pings to the other server. If the server is not
 * the primary server, it will send pings from the other server.
 */
public class ServerUDP extends Thread{
    
    private Server server;
    private DatagramSocket socket;
    private UDPSynchronized synchronizedThread;

    // This is the constructor of the class. It creates a new DatagramSocket with the given port and
    // IP. It also sets the timeout of the socket
    public ServerUDP(Server server) throws SocketException {
        this.server = server;
        this.socket = new DatagramSocket(server.getMyUDPPort(), server.getMyIp());
        this.socket.setSoTimeout(server.getTimeout());
        this.synchronizedThread = new UDPSynchronized(this.server);
    }

    public UDPSynchronized getSynchronizedThread() {
        return this.synchronizedThread;
    }

    public void setSynchronizedThread(UDPSynchronized synchronizedThread) {
        this.synchronizedThread = synchronizedThread;
    }

    /**
     * It sends a boolean to the other server and waits for a response. If the response is not
     * received, it will send the ping again a certain number of times. If it doesn't receive a response, assumes that it is the primary server
     * 
     * @return The boolean value of whether or not the server is the primary.
     */
    private boolean isPrimary() {
        // This is creating a byte array from the output stream.
        ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
        DataOutputStream outputStream = new DataOutputStream(byteOutputStream);
        try {
            outputStream.writeBoolean(true);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        byte [] buffer = byteOutputStream.toByteArray();

        // Sending the ping to the other server.
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, server.getOtherIp(), server.getOtherUDPPort());
        for(int i = 0; i < server.getHeartbeats(); i++) {
            try {
                // Sending a ping to the other server and waiting for a response. If the response is
                // not received, it will send the ping again.
                socket.send(packet);
                byte [] bufferResponse = new byte[buffer.length + 1];
                DatagramPacket packetResponse = new DatagramPacket(bufferResponse, bufferResponse.length);

                socket.receive(packetResponse);
                return false;
            }catch(SocketTimeoutException exc){
                System.out.println("Timeout (" + (i+1) + "/" + server.getHeartbeats() + ")");
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            /*try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }*/

        }
        return true;
    }

    /**
     * It receives a ping from a secondary server and responds with a boolean value
     */
    private void receivePings() {
        while(true) {
            try {
                byte[] buf = new byte[1024];
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet);
                ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(buf, 0, packet.getLength());
                DataInputStream inputStream = new DataInputStream(byteArrayInputStream);
                boolean response = inputStream.readBoolean();

                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                DataOutputStream outputStream = new DataOutputStream(byteArrayOutputStream);
                outputStream.writeBoolean(response);
                byte[] resp = byteArrayOutputStream.toByteArray();
                DatagramPacket res = new DatagramPacket(resp, resp.length, packet.getAddress(), packet.getPort());
                socket.send(res);
            } catch(SocketTimeoutException exc) {

            } catch (IOException exc) {
                System.out.println("Exception");
                return;
            }
        }
    }

    /**
     * Send a ping to the other server and wait for a response. If the response is received, reset the
     * failOvers counter. If the response is not received, increment the failOvers counter. If the
     * failOvers counter reaches the number of heartbeats, then the other server is considered dead
     * ans assumes that it is the primary server
     */
    private void sendPings() {
        ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
        DataOutputStream outputStream = new DataOutputStream(byteOutputStream);
        try {
            outputStream.writeBoolean(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte [] buffer = byteOutputStream.toByteArray();

        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, server.getOtherIp(), server.getOtherUDPPort());
        int failOvers = 0;
        while(failOvers < server.getHeartbeats()) {
            try {
                socket.send(packet);
                byte [] bufferResponse = new byte[buffer.length + 1];
                DatagramPacket packetResponse = new DatagramPacket(bufferResponse, bufferResponse.length);

                socket.receive(packetResponse);
                failOvers = 0;
            }catch(SocketTimeoutException exc){
                failOvers++;
                System.out.println("Timeout (" + (failOvers) + "/" + server.getHeartbeats() + ")");
            } catch (IOException e) {
                e.printStackTrace();
            }
            /*try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }*/
        }
    }

    /**
     * If this server is the primary server, it will receive pings to all other server. If it is not the
     * primary server, it will send pings from the primary server
     */
    @Override
    public void run() {
        boolean primaryServer = isPrimary();
        server.setPrimaryServer(primaryServer);
        synchronizedThread.start();
        if(primaryServer) {
            synchronized(this) {
                this.notifyAll();
            }
            receivePings();
        } else {
            sendPings();
            server.setPrimaryServer(true);
            synchronized(this) {
                this.notifyAll();
            }
            receivePings();
        }
    }

}
