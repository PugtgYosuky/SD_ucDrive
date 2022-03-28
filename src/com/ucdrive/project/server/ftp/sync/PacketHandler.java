package com.ucdrive.project.server.ftp.sync;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import com.ucdrive.project.server.Server;

public class PacketHandler {

    private DataOutputStream fileData;
    private int currentIndex;
    private Server server;

    public PacketHandler(Server server) {
        this.fileData = null;
        this.currentIndex = 0;
        this.server = server;
    }

    public void execute(FilePacket file) throws IOException {
        if(!file.getIsBinaryFile()) {
            new File(server.getStoragePath() + file.getLocation() + file.getPath()).mkdir();
            return;
        }

        if(currentIndex + 1 != file.getIndex()){
            return;
        }

        System.out.println("RECEIVED: (" + file.getIndex() + "/" + file.getTotalPackets() + ")");

        currentIndex++;
        if(file.getIndex() == 1) {
            System.out.println("Received file: " + file.getPath());
            fileData = new DataOutputStream(new FileOutputStream(server.getStoragePath() + file.getLocation() + file.getPath()));
        }

        fileData.write(file.getBuffer(), 0, file.getBufferLength());
        
        if(file.getIndex() == file.getTotalPackets()) {
            fileData.close();
            currentIndex = 0;
        }
    }

}
