package com.example.demo.magicdemo.spider;

import lombok.Builder;
import lombok.Data;

/**
 * @author chen.chao
 * @version 1.0
 * @date 2019/12/19 20:24
 * @description
 */
@Data
@Builder
public class Resources {

    private String url;
    private String type;
    private String content;
    private byte[] buffer;

}
