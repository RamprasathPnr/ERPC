package com.omneagate.erbc.Util;

import lombok.Data;

/**
 * SingleTon class for maintain the sessionId
 */
@Data
public class SessionId {

    private static SessionId mInstance = null;

    private String sessionId;

    private long userId;

    private String mobile_number;







    private SessionId() {
        sessionId = "";
        userId = 0l;
        mobile_number = "";

    }

    public static synchronized SessionId getInstance() {
        if (mInstance == null) {
            mInstance = new SessionId();
        }
        return mInstance;
    }

}
