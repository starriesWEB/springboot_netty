package com.starry.netty.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author starry
 * @version 1.0
 * @date 2022/1/27 22:04
 * @Description
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result implements Serializable {

    public static final int SUCCESS = 200;
    public static final int ERROR = 500;
    public static final String BLANK_MESSAGE = "";

    private Integer code;
    private String message;
    private String data;


    @Override
    public String toString() {
        return "{" +
                "code: " + code +
                ", message: \"" + message + "\"" +
                ", data: \"" + data + "\"" +
                "}";
    }

}
