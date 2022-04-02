package com.ucdrive.project.server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import com.ucdrive.project.server.ftp.DataThread;
import com.ucdrive.project.server.ftp.RequestDispatcher;
import com.ucdrive.project.server.ftp.sync.FileDispatcher;

public class ServerFTP extends Thread{

    private final int BACKLOG = 100;
    private static int port;
    private static InetAddress ip;
    private int serverPort;
    private static RequestDispatcher requests;
    private ThreadPoolExecutor pool;
    private FileDispatcher fileDispatcher;
    private Server server;
    
    public ServerFTP(int serverPort, Server server, int maxThreads, FileDispatcher fileDispatcher) {
        this.serverPort = serverPort;
        this.server = server;
        if(requests == null)
            requests = new RequestDispatcher();
        this.pool = (ThreadPoolExecutor) Executors.newFixedThreadPool(maxThreads);
        this.fileDispatcher = fileDispatcher;
    }

    public static RequestDispatcher getRequestDispatcher() {
        return requests;
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

    public static int getPort() {
        return port;
    }

    public static InetAddress getIp() {
        return ip;
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
