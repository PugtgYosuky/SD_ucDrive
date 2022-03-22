package com.ucdrive.project.shared;

import java.io.Serializable;

import com.ucdrive.project.client.response.ResponseHandler;

public interface Response extends Serializable {
    
    public void execute(ResponseHandler response);

}
