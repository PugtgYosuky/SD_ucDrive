package com.ucdrive.project.server;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class ServerUDP extends Thread{
    
    private int serverPort;
    private String ip;
    
    public ServerUDP(String ip, int serverPort) {
        this.ip = ip;
        this.serverPort = serverPort;
    }

    @Override
    public void run() {
        try (DatagramSocket socket = new DatagramSocket(serverPort, InetAddress.getByName(ip))) {

        } catch (SocketException exc) {

        } catch (UnknownHostException exc) {

        }
    }

}
