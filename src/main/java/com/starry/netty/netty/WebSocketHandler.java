package com.starry.netty.netty;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.starry.netty.pojo.Result;
import com.starry.netty.service.PushService;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;


/**
 *
 * @author starry
 */
@Component
@ChannelHandler.Sharable
public class WebSocketHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {
    private static final Logger log = LoggerFactory.getLogger(NettyServer.class);

    @Resource
    private PushService pushService;

    /**
     * 一旦连接，第一个被执行 * @param ctx * @throws Exception
     */
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        log.info("handlerAdded 被调用" + ctx.channel().id().asLongText());
        // 添加到channelGroup 通道组
        NettyConfig.getChannelGroup().add(ctx.channel());
    }

    private static final String IMAGE_TYPE = "image";
    private static final String TEXT_TYPE = "text";

    /**
     * 读取数据
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        log.info("服务器收到消息：{}", msg.text());

        //------------------------ 客户端连接到服务器
        // 获取用户ID,关联channel
        JSONObject jsonObject = JSONUtil.parseObj(msg.text());
        String uid = jsonObject.getStr("uid");
        if (StrUtil.isNotBlank(uid)) {
            NettyConfig.getUserChannelMap().put(uid, ctx.channel());

            // 将用户ID做为自定义属性加入到channel中，方便随时channel中获取用户ID
            AttributeKey<String> key = AttributeKey.valueOf("userId");
            ctx.channel().attr(key).setIfAbsent(uid);

            // 回复消息
            Result result = new Result(Result.SUCCESS, Result.BLANK_MESSAGE, "连接成功");
            ctx.channel().writeAndFlush(new TextWebSocketFrame(result.toString()));
            return;
        }

        /*
        * sender
        * {
        *   uid: 123456,
        *   userId: 123456,
        *   userIdToSend: 666666,
        *   messageType: "text",
        *   msg: "hello netty"
        * }
        * receiver
        * {
        *   code: 200,
        *   message: "",
        *   data: ""
        * }
        *
        * */
        String userId = jsonObject.getStr("userId");
        String messageType = jsonObject.getStr("messageType");
        String message = jsonObject.getStr("msg");
        String userIdToSend = jsonObject.getStr("userIdToSend");

        //------------------------ 客户端发送消息到服务器
        if (StrUtil.isNotBlank(userId) && StrUtil.isNotBlank(messageType) && StrUtil.isNotBlank(message)) {

            // 接收客户端的消息，进行处理
            // do something

            switch (messageType) {
                case IMAGE_TYPE:
                    // 处理图片
                    break;

                case TEXT_TYPE:
                    // 处理文本
                    break;

                default:
                    throw new RuntimeException();
            }


            Result result = new Result(Result.SUCCESS, "已接收", Result.BLANK_MESSAGE);
            ctx.channel().writeAndFlush(new TextWebSocketFrame(result.toString()));
            return;

        }


        //------------------------ 后台发送消息到客户端
        if (StrUtil.isNotBlank(userIdToSend) && StrUtil.isNotBlank(messageType) && StrUtil.isNotBlank(message)) {
            // 给客户端发送消息
            Result result1 = new Result(Result.SUCCESS, Result.BLANK_MESSAGE,message);
            pushService.pushMsgToOne(userIdToSend, result1.toString());

            // 回显发送成功提示
            Result result2 = new Result(Result.SUCCESS, "已发送",Result.BLANK_MESSAGE);
            ctx.channel().writeAndFlush(new TextWebSocketFrame(result2.toString()));
        }

    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        log.info("handlerRemoved 被调用" + ctx.channel().id().asLongText());
        // 删除通道
        NettyConfig.getChannelGroup().remove(ctx.channel());
        removeUserId(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.info("异常：{}", cause.getMessage());
        // 删除通道
        NettyConfig.getChannelGroup().remove(ctx.channel());
        removeUserId(ctx);
        ctx.close();
    }

    /**
     * 删除用户与channel的对应关系 * @param ctx
     */
    private void removeUserId(ChannelHandlerContext ctx) {
        AttributeKey<String> key = AttributeKey.valueOf("userId");
        String userId = ctx.channel().attr(key).get();
        NettyConfig.getUserChannelMap().remove(userId);
    }
}