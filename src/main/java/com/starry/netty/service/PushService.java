package com.starry.netty.service;

/**
 * @author starry
 */
public interface PushService {
    void pushMsgToOne(String userId,String msg);

    void pushMsgToAll(String msg);
}