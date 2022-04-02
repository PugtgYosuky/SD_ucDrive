package com.ucdrive.project.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import com.ucdrive.project.server.client.commands.CommandExecutor;
import com.ucdrive.project.server.client.commands.CommandHandler;
import com.ucdrive.project.server.ftp.ServerFTP;
import com.ucdrive.project.server.ftp.sync.FileDispatcher;
import com.ucdrive.project.server.ftp.sync.ServerUDP;

public class Server {

    private boolean primaryServer;
    private int heartbeats;
    private int timeout;
    private InetAddress myIp;
    private InetAddress otherIp;
    private int myTCPPort;
    private int myUDPPort;
    private int otherUDPPort;
    private int synchronizePort;
    private int otherSynchronizePort;
    private String storagePath;

    public Server(int heartbeats, int timeout, String myIp, String otherIp, int myTCPPort,
                 int myUDPPort, int otherUDPPort, int synchronizePort, int otherSynchronizePort,
                 String storagePath) throws UnknownHostException{

        this.primaryServer = false;
        this.heartbeats = heartbeats;
        this.timeout = timeout;
        this.myIp = InetAddress.getByName(myIp);
        this.otherIp = InetAddress.getByName(otherIp);
        this.myTCPPort = myTCPPort;
        this.myUDPPort = myUDPPort;
        this.otherUDPPort = otherUDPPort;
        this.synchronizePort = synchronizePort;
        this.otherSynchronizePort = otherSynchronizePort;
        this.storagePath = storagePath;
    }

    public void start() {
        ServerUDP serverUDP;
		try {
			serverUDP = new ServerUDP(this);
		} catch (SocketException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
            return;
		}
        serverUDP.start();

        try {
            synchronized (serverUDP) {
                serverUDP.wait();
            }
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        FileDispatcher fileDispatcher = serverUDP.getSynchronizedThread().getFileDispatcher();

        ServerFTP serverFTP = new ServerFTP(0, this,10, fileDispatcher);
        CommandExecutor commandExecutor;
        try {
            commandExecutor = new CommandExecutor(fileDispatcher, this, serverFTP);
        } catch (Exception exc) {
            exc.printStackTrace();
            return;
        }
        CommandHandler.commandExecutor = commandExecutor;

        ServerTCP serverTCP = new ServerTCP(this.myTCPPort, this.myIp, commandExecutor, 10, this.storagePath, fileDispatcher, this);
        serverTCP.start();
        serverFTP.start();
        try {
            serverUDP.join();
            serverTCP.join();
            serverFTP.join();
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
    
    public int getSynchronizePort() {
        return synchronizePort;
    }

    public void setSynchronizePort(int synchronizePort) {
        this.synchronizePort = synchronizePort;
    }

    public int getOtherSynchronizePort() {
        return otherSynchronizePort;
    }
    
    public void setOtherSynchronizePort(int otherSynchronizePort) {
        this.otherSynchronizePort = otherSynchronizePort;
    }
    
    public int getHeartbeats() {
        return heartbeats;
    }

    public void setHeartbeats(int heartbeats) {
        this.heartbeats = heartbeats;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public synchronized boolean getPrimaryServer(){
        return this.primaryServer;
    }

    public synchronized void setPrimaryServer(boolean primaryServer) {
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
            String configFile = args[0];

            File file = new File(configFile);

            if(file.exists()) {
                BufferedReader buffer = new BufferedReader(new FileReader(file));
                
                String[] line = buffer.readLine().split(" ");

                buffer.close();

                String ip = line[0];
                int portTCP = Integer.parseInt(line[1]);
                int portUDP = Integer.parseInt(line[2]);
                String otherIp = line[3];
                int otherUDPPort = Integer.parseInt(line[4]);
                int synchronizePort = Integer.parseInt(line[5]);
                int otherSynchronizePort = Integer.parseInt(line[6]);

                String storagePath = line[7];
                int heartbeats = Integer.parseInt(line[8]);
                int timeout = Integer.parseInt(line[9]);
                
                try {
                    Server server = new Server(heartbeats, timeout, ip, otherIp, portTCP, portUDP, otherUDPPort, synchronizePort, otherSynchronizePort, storagePath);
                    server.start();
                } catch(UnknownHostException exc) {
                    exc.printStackTrace();
                }

            } else {
                System.out.println("Invalid config file");
            }
            
        } catch(IndexOutOfBoundsException exc) {
            System.out.println("Invalid number of arguments...");
        } catch(NumberFormatException exc) {
            System.out.println("Error trying to parse numbers...");
        } catch (FileNotFoundException exc) {
            exc.printStackTrace();
        } catch(IOException exc) {
            exc.printStackTrace();
        }
    }
}
