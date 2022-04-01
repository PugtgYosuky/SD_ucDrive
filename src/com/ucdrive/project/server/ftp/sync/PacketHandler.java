package com.ucdrive.project.server.ftp.sync;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;

import com.ucdrive.project.server.Server;

public class PacketHandler {

    private File file;
    private DataOutputStream fileData;
    private int currentIndex;
    private Server server;

    public PacketHandler(Server server) {
        this.fileData = null;
        this.file = null;
        this.currentIndex = 0;
        this.server = server;
    }

    public int execute(FilePacket file) throws IOException {
        if(!file.getIsBinaryFile()) {
            new File(server.getStoragePath() + file.getLocation() + file.getPath()).mkdirs();
            return 2;
        }

        if(currentIndex + 1 != file.getIndex()){
            return currentIndex + 1;
        }

        System.out.println("RECEIVED: (" + file.getIndex() + "/" + file.getTotalPackets() + ")");

        if(Arrays.compare(file.getChecksum(), file.calculateChecksum(file.getBuffer())) != 0) {
            System.out.println("Checksum failed");
            return currentIndex;
        }

        currentIndex++;
        if(currentIndex == 1) {
            System.out.println("Received file: " + file.getPath());
            this.file = new File(server.getStoragePath() + file.getLocation() + file.getPath());
            this.fileData = new DataOutputStream(new FileOutputStream(this.file));
        }

        int next = currentIndex + 1;

        this.fileData.write(file.getBuffer(), 0, file.getBufferLength());

        if(file.getIndex() == file.getTotalPackets()) {
            this.file = null;
            fileData.close();
            currentIndex = 0;
        }

        return next;
    }

    public void deleteFile() {
        if(this.file != null) {
            try {
                this.fileData.close();
                this.file.delete();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
