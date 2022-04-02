package com.ucdrive.project.server.ftp.sync;

import java.io.IOException;
import java.io.Serializable;

// Defining a new interface called SyncPacket.
public interface SyncPacket extends Serializable{
    public int execute(PacketHandler packet) throws IOException;
}
