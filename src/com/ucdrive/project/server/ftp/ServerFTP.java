package com.ucdrive.project.server.ftp;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import com.ucdrive.project.server.Server;
import com.ucdrive.project.server.ftp.sync.FileDispatcher;

public class ServerFTP extends Thread{

    private final int BACKLOG = 100;
    private int port;
    private InetAddress ip;
    private int serverPort;
    private RequestDispatcher requests;
    private ThreadPoolExecutor pool;
    private FileDispatcher fileDispatcher;
    private Server server;
    
    public ServerFTP(int serverPort, Server server, int maxThreads, FileDispatcher fileDispatcher) {
        this.serverPort = serverPort;
        this.server = server;
        this.requests = new RequestDispatcher();
        this.pool = (ThreadPoolExecutor) Executors.newFixedThreadPool(maxThreads);
        this.fileDispatcher = fileDispatcher;
    }

    public RequestDispatcher getRequestDispatcher() {
        return this.requests;
    }

    public void acceptRequests(ServerSocket server) throws IOException {
        while(true) {
            Socket socket = server.accept();
            pool.submit(() -> {
                DataThread dataThread;
                try {
                    dataThread = new DataThread(socket, requests, fileDispatcher);
                } catch (IOException e) {
                    return;
                }
                
                dataThread.start();

                try {
                    socket.close();
                } catch(IOException exc) {
                    exc.printStackTrace();
                }
            });
        }
    }

    public int getPort() {
        return this.port;
    }

    public InetAddress getIp() {
        return this.ip;
    }
    
    @Override
    public void run() {
        try (ServerSocket server = new ServerSocket(serverPort, BACKLOG, this.server.getMyIp())) {
            ip = server.getInetAddress();
            port = server.getLocalPort();
            acceptRequests(server);
        } catch(IOException exc) {
            exc.printStackTrace();
        }
    }
    
}
