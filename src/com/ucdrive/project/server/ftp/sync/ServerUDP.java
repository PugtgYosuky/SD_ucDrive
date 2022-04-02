package com.ucdrive.project.server.ftp.sync;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import com.ucdrive.project.server.Server;

public class ServerUDP extends Thread{
    
    private Server server;
    private DatagramSocket socket;
    private UDPSynchronized synchronizedThread;

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

    private boolean isPrimary() {
        ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
        DataOutputStream outputStream = new DataOutputStream(byteOutputStream);
        try {
            outputStream.writeBoolean(true);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        byte [] buffer = byteOutputStream.toByteArray();

        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, server.getOtherIp(), server.getOtherUDPPort());
        for(int i = 0; i < server.getHeartbeats(); i++) {
            try {
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
