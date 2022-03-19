package com.ucdrive.project.server.ftp;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class DataThread {
    
    private Socket socket;
    private RequestDispatcher requests;
    private DataInputStream inputStream;
    private DataOutputStream outputStream;

    public DataThread(Socket socket, RequestDispatcher requests) throws IOException {
        this.socket = socket;
        this.requests = requests;
        this.inputStream = new DataInputStream(socket.getInputStream());
        this.outputStream = new DataOutputStream(socket.getOutputStream());
    }

    public RequestFile authenticate() throws IOException {
        String id = inputStream.readUTF();
        return requests.findRequest(id);
    }

    public void start() {

        RequestFile requestFile;
        try {
            requestFile = authenticate();
            if(requestFile == null){
                return;
            }
            outputStream.writeBoolean(true);
        } catch (IOException e) {
            return;
        }

        while(true) {

            // Transferencia do ficheiro
            
        }
    }
    
}
