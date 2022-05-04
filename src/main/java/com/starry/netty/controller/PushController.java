package com.starry.netty.controller;

import com.starry.netty.service.PushService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author starry
 */
@RestController
@RequestMapping("/push")
public class PushController {

    @Autowired
    private PushService pushService;

    /**
     * 推送给全部用户 * @param msg
     */
    @PostMapping("/pushAll")
    public void pushToAll(@RequestParam("msg") String msg) {
        pushService.pushMsgToAll(msg);
    }

    /**
     * 推送给指定用户 * @param userId * @param msg
     */
    @PostMapping("/pushOne")
    public void pushMsgToOne(@RequestParam("userId") String userId, @RequestParam("msg") String msg) {
        pushService.pushMsgToOne(userId, msg);
    }

}