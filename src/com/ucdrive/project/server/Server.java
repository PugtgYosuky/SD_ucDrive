package com.ucdrive.project.server;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class Server {

    private boolean primaryServer;
    private int heartbeats;
    private long timeout;
    private InetAddress myIp;
    private InetAddress otherIp;
    private int myTCPPort;
    private int myUDPPort;
    private int otherUDPPort;
    private String storagePath;

    public Server(int heartbeats, long timeout, String myIp, String otherIp, int myTCPPort, int myUDPPort, int otherUDPPort, String storagePath) throws UnknownHostException{
        this.primaryServer = false;
        this.heartbeats = heartbeats;
        this.timeout = timeout;
        this.myIp = InetAddress.getByName(myIp);
        this.otherIp = InetAddress.getByName(otherIp);
        this.myTCPPort = myTCPPort;
        this.myUDPPort = myUDPPort;
        this.otherUDPPort = otherUDPPort;
        this.storagePath = storagePath;
    }

    public void start() {
        ServerUDP serverUDP = new ServerUDP(myIp, myTCPPort, otherIp, otherUDPPort);
        serverUDP.start();
        while(!primaryServer) {
            primaryServer = serverUDP.isPrimary();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        
        ServerTCP serverTCP = new ServerTCP(this.myTCPPort, this.myIp, 10, this.storagePath);
        ServerFTP serverFTP = new ServerFTP(0, 10);
        serverTCP.start();
        serverFTP.start();

        try {
            serverTCP.join();
            serverFTP.join();
            serverUDP.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    public int getMyUDPPort() {
        return myUDPPort;
    }

    public int getMyTCPPort() {
        return myTCPPort;
    }

    public int getOtherUDPPort() {
        return otherUDPPort;
    }

    public void setMyTCPPort(int myTCPPort) {
        this.myTCPPort = myTCPPort;
    }

    public void setMyUDPPort(int myUDPPort) {
        this.myUDPPort = myUDPPort;
    }

    public void setOtherUDPPort(int otherUDPPort) {
        this.otherUDPPort = otherUDPPort;
    }
    
    public int getHeartbeats() {
        return heartbeats;
    }

    public void setHeartbeats(int heartbeats) {
        this.heartbeats = heartbeats;
    }

    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    public boolean primaryServer(){
        return this.primaryServer;
    }

    public void setPrimaryServer(boolean primaryServer) {
        this.primaryServer = primaryServer;
    }

    public InetAddress getMyIp() {
        return myIp;
    }

    public void setMyIp(InetAddress myIp) {
        this.myIp = myIp;
    }

    public InetAddress getOtherIp() {
        return otherIp;
    }

    public void setOtherIp(InetAddress otherIp) {
        this.otherIp = otherIp;
    }

    public String getStoragePath() {
        return storagePath;
    }
    
    public void setStoragePath(String storagePath) {
        this.storagePath = storagePath;
    }

    public static void main(String[] args) {
        try {
            String ip = args[0];
            int portTCP = Integer.parseInt(args[1]);
            int portUDP = Integer.parseInt(args[2]);
            String otherIp = args[3];
            int otherPort = Integer.parseInt(args[4]);

            String storagePath = args[5];
            int heartbeats = Integer.parseInt(args[6]);
            long timeout = Long.parseLong(args[7]);
        
            try {
                Server server = new Server(heartbeats, timeout, ip, otherIp, portTCP, portUDP, otherPort, storagePath);
                
                server.start();
            } catch(UnknownHostException exc) {
                exc.printStackTrace();
            }
        } catch(IndexOutOfBoundsException exc) {
            System.out.println("Invalid number of arguments...");
        } catch(NumberFormatException exc) {
            System.out.println("Error trying to parse numbers...");
        }
    }
}
