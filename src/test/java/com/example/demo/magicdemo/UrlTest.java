package com.example.demo.magicdemo;

import com.example.demo.magicdemo.spider.MatchLinksProcessor;
import com.example.demo.magicdemo.spider.SaveFilePipe;
import org.springframework.util.StringUtils;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.example.BaiduBaikePageProcessor;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>Create Time: 2019年08月08日 20:19          </p>
 * <p>@author Chen.Chao                </p>
 * <p>@Todo                   </p>
 **/

public class UrlTest {

    public static void main(String[] args) throws MalformedURLException {

        Spider.create(new MatchLinksProcessor("steamaker.cn")).addPipeline(new SaveFilePipe(new File("D:\\main_single\\")))
                .addUrl("http://www.steamaker.cn/competition",
                        "http://www.steamaker.cn/noc",
                        "http://www.steamaker.cn/nocsubmit"
                        , "http://www.steamaker.cn/student/view/myCourse",
                        "http://www.steamaker.cn/student/view/userdatum"
                        , "http://www.steamaker.cn/student/view/userworks/userworks"
                        , "http://www.steamaker.cn/student/view/userworks/usercollect"
                ).thread(4).start();

    }


}














