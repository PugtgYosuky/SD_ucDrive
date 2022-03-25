package com.ucdrive.project.server;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class ServerUDP extends Thread{
    
    private int serverPort;
    private InetAddress ip;
    private int otherPort;
    private InetAddress otherIp;
    
    public ServerUDP(InetAddress ip, int serverPort, InetAddress otherIp, int otherPort) {
        this.ip = ip;
        this.serverPort = serverPort;
        this.otherIp = otherIp;
        this.otherPort = otherPort;
    }

    public boolean isPrimary() {
        return true;
    }

    @Override
    public void run() {
        try (DatagramSocket socket = new DatagramSocket(serverPort, ip)) {

            // TODO: fechar datagramsocket
        } catch (SocketException exc) {

        }
    }

}
