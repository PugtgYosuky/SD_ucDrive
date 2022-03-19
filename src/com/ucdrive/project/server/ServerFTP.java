package com.ucdrive.project.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import com.ucdrive.project.server.ftp.DataThread;
import com.ucdrive.project.server.ftp.RequestDispatcher;

public class ServerFTP extends Thread{

    private int serverPort;
    private RequestDispatcher requests;
    private ThreadPoolExecutor pool;
    
    public ServerFTP(int serverPort, int maxThreads) {
        this.serverPort = serverPort;
        this.requests = new RequestDispatcher();
        this.pool = (ThreadPoolExecutor) Executors.newFixedThreadPool(maxThreads);
        this.start();
    }

    public void acceptRequests(ServerSocket server) throws IOException {
        while(true) {
            Socket socket = server.accept();
            pool.submit(() -> {
                DataThread dataThread;
                try {
                    dataThread = new DataThread(socket, this.requests);
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
    
    @Override
    public void run() {
        try (ServerSocket server = new ServerSocket(serverPort)) {
            acceptRequests(server);
        } catch(IOException exc) {
            exc.printStackTrace();
        }
    }
    
}
