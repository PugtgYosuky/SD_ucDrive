package com.ucdrive.project.server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import com.ucdrive.project.server.client.ClientThread;
import com.ucdrive.project.server.client.commands.CommandExecutor;
import com.ucdrive.project.server.client.commands.CommandHandler;
import com.ucdrive.project.server.ftp.sync.FileDispatcher;
import com.ucdrive.project.server.storage.UserData;

public class ServerTCP extends Thread {
    
    private int serverPort;
    private InetAddress ip;
    private Vector<ClientThread> clients;
    private ThreadPoolExecutor pool;
    private UserData userData;
    private CommandExecutor commandExecutor;
    
    public ServerTCP(int serverPort, InetAddress ip, int maxThreads, String path, FileDispatcher fileDispatcher) {
        this.serverPort = serverPort;
        this.ip = ip;
        clients = new Vector<>();
        userData = new UserData(path);
        pool = (ThreadPoolExecutor) Executors.newFixedThreadPool(maxThreads);
        try {
            this.commandExecutor = new CommandExecutor(fileDispatcher);
            CommandHandler.commandExecutor = this.commandExecutor;
        } catch(Exception exc) {
            exc.printStackTrace();
        }
    }

    /**
     * Accepts clients and creates a new thread for each client
     * 
     * @param server The server socket that will be used to accept connections.
     */
    private void acceptClients(ServerSocket server) throws IOException {
        System.out.println("Server TCP started in " + ip + " with port: " + serverPort);
        while(true) {
            Socket socket = server.accept();
            pool.submit(() -> {
                ClientThread clientThread;
                try {
                    clientThread = new ClientThread(socket, userData, commandExecutor);
                } catch (IOException e) {
                    return;
                }
                System.out.println("Client connected");
                clients.add(clientThread);
                clientThread.start();
                if(clientThread.getUser() != null)
                    clientThread.getUser().setIsConnected(false);
                System.out.println("Client disconnected");
                clients.remove(clientThread);
                try {
                    socket.close();
                } catch(IOException exc) {
                    exc.printStackTrace();
                }
            });
        }
    }

    @Override
    public void run() {
        try (ServerSocket server = new ServerSocket(serverPort)) {
            acceptClients(server);
        } catch(IOException exc) {
            exc.printStackTrace();
        }
    }
    
}
