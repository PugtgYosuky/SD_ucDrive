package com.ucdrive.project.server.ftp;

import java.util.concurrent.ConcurrentHashMap;

public class RequestDispatcher {
    
    private ConcurrentHashMap<String, RequestFile> requests;

    public RequestDispatcher(){
        this.requests = new ConcurrentHashMap<>();
    }

    public void addRequest(RequestFile requestFile) {
        this.requests.put(requestFile.getUniqueID(), requestFile);
    }
    
    public RequestFile findRequest(String id) {
        return requests.get(id);
    }

    public RequestFile removeRequest(String id) {
        return this.requests.remove(id);
    }
    
}
