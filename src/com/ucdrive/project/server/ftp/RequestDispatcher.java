/*
    UCDrive

    Alunos:
        Joana Simoes, 2019217013
        Samuel Carinhas, 2019217199
*/

package com.ucdrive.project.server.ftp;

import java.util.concurrent.ConcurrentHashMap;

/**
 * This class is responsible for storing client requests if file transfers 
 */
public class RequestDispatcher {
    
    private ConcurrentHashMap<String, RequestFile> requests;

    public RequestDispatcher(){
        this.requests = new ConcurrentHashMap<>();
    }

    /**
     * Add a request to the list of requests.
     * 
     * @param requestFile The request file that was just created.
     */
    public void addRequest(RequestFile requestFile) {
        this.requests.put(requestFile.getUniqueID(), requestFile);
    }
    
    /**
     * Find a request by its id
     * 
     * @param id The id of the request to be found.
     * @return A RequestFile object or null
     */
    public RequestFile findRequest(String id) {
        return requests.get(id);
    }

    /**
     * Remove a request from the list of requests
     * 
     * @param id The id of the request to remove.
     * @return The RequestFile object that was removed from the list.
     */
    public RequestFile removeRequest(String id) {
        return this.requests.remove(id);
    }
    
}
