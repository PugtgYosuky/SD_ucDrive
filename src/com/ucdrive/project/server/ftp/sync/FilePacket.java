package com.ucdrive.project.server.ftp.sync;

import java.io.IOException;

public class FilePacket implements SyncPacket{
    private int index; // starts at 1
    private int totalPackets;
    private byte [] buffer;
    private int bufferLength;
    private String path; // id
    private boolean isBinaryFile;
    private String location;

    public FilePacket(int index, int totalPackets, byte[] buffer, String path, boolean isBinaryFile, String location) {
        this.index = index;
        this.totalPackets = totalPackets;
        this.buffer = buffer;
        this.path = path;
        this.isBinaryFile = isBinaryFile;
        this.location = location;
    }
    
    public FilePacket (int index, int totalPackets, String path, boolean isBinaryFile, String location){
        this.index = index;
        this.totalPackets = totalPackets;
        this.path = path;
        this.isBinaryFile = isBinaryFile;
        this.location = location;
    }
    
    public byte[] getBuffer() {
        return buffer;
    }
    
    public void setBuffer(byte[] buffer) {
        this.buffer = buffer;
    }
    
    public int getBufferLength() {
        return bufferLength;
    }

    public void setBufferLength(int bufferLength) {
        this.bufferLength = bufferLength;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getTotalPackets() {
        return totalPackets;
    }

    public void setTotalPackets(int totalPackets) {
        this.totalPackets = totalPackets;
    }
    
    public String getPath() {
        return path;
    }
    
    public void setPath(String path) {
        this.path = path;
    }

    public boolean getIsBinaryFile(){
        return this.isBinaryFile;
    }
    
    public void setIsBinaryFile(boolean isBinaryFile) {
        this.isBinaryFile = isBinaryFile;
    }

    public String getLocation() {
        return location;
    }

    public void setBinaryFile(boolean isBinaryFile) {
        this.isBinaryFile = isBinaryFile;
    }

    @Override
    public int execute(PacketHandler packet) throws IOException {
        return packet.execute(this);
    }

}
