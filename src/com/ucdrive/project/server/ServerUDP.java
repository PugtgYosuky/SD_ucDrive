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

public class ServerUDP extends Thread{
    
    private Server server;
    private DatagramSocket socket;
    
    public ServerUDP(Server server) throws SocketException {
        this.server = server;
        this.socket = new DatagramSocket(server.getMyUDPPort(), server.getMyIp());
        this.socket.setSoTimeout(server.getTimeout());
    }

    public boolean isPrimary() {
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
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
        return true;
    }

    @Override
    public void run() {
        while(true) {
            try {
                byte buf[] = new byte[1024];
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet);
                ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(buf, 0, packet.getLength());
                DataInputStream inputStream = new DataInputStream(byteArrayInputStream);
                boolean response = inputStream.readBoolean();

                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                DataOutputStream outputStream = new DataOutputStream(byteArrayOutputStream);
                outputStream.writeBoolean(response);
                byte resp[] = byteArrayOutputStream.toByteArray();
                DatagramPacket res = new DatagramPacket(resp, resp.length, packet.getAddress(), packet.getPort());
                socket.send(res);
            } catch(SocketTimeoutException exc) {

            } catch (IOException exc) {
                System.out.println("Exception");
                return;
            }
        }
    }

}
