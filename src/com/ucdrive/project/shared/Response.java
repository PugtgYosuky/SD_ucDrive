/*
    UCDrive

    Alunos:
        Joana Simoes, 2019217013
        Samuel Carinhas, 2019217199
*/

package com.ucdrive.project.shared;

import java.io.Serializable;

import com.ucdrive.project.client.response.ResponseHandler;

// The Response interface is used to define the contract for a response object.
public interface Response extends Serializable {
    
    public void execute(ResponseHandler response);

    public String getCommand();

}
