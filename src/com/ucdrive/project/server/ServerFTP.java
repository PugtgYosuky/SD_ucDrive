/*
    UCDrive

    Alunos:
        Joana Simoes, 2019217013
        Samuel Carinhas, 2019217199
*/

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

/**
 * This class is responsible for accepting connections from clients to transfer files and dispatching them to a thread
 * pool
 */
public class ServerFTP extends Thread{

    private final int BACKLOG = 100;
    private int port;
    private InetAddress ip;
    private int serverPort;
    private RequestDispatcher requests;
    private ThreadPoolExecutor pool;
    private FileDispatcher fileDispatcher;
    private Server server;
    
    // This is the constructor of the class. It initializes the fields of the class.
    public ServerFTP(int serverPort, Server server, int maxThreads, FileDispatcher fileDispatcher) {
        this.serverPort = serverPort;
        this.server = server;
        this.requests = new RequestDispatcher();
        this.pool = (ThreadPoolExecutor) Executors.newFixedThreadPool(maxThreads);
        this.fileDispatcher = fileDispatcher;
    }

    /**
     * Returns the request dispatcher
     * 
     * @return The RequestDispatcher object.
     */
    public RequestDispatcher getRequestDispatcher() {
        return this.requests;
    }

    /**
     * Accepts incoming connections and creates a new thread for each one
     * 
     * @param server The server socket that will accept connections.
     */
    public void acceptRequests(ServerSocket server) throws IOException {
        while(true) {
            Socket socket = server.accept();
            // It creates a new thread for each connection.
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
    
    /**
     * Accepts incoming connections and creates a new thread to handle each connection
     */
    @Override
    public void run() {
        // It creates a new ServerSocket object and initializes it with the port number and the IP address of the server.
        try (ServerSocket server = new ServerSocket(serverPort, BACKLOG, this.server.getMyIp())) {
            ip = server.getInetAddress();
            port = server.getLocalPort();
            acceptRequests(server);
        } catch(IOException exc) {
            exc.printStackTrace();
        }
    }
    
}
