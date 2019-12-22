package com.example.demo.magicdemo.spider;

import org.springframework.util.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author chen.chao
 * @version 1.0
 * @date 2019/12/20 10:30
 * @description
 */

public class UrlUtil {

    public static final String DOMAIN_MATCH = "^(http|https)://[^/]+";
    public static final Pattern DOMAIN_MATCH_PATTERN = Pattern.compile(DOMAIN_MATCH);

    public static String trimDomain(String url){
        String s = url.replaceAll(DOMAIN_MATCH, "");
        if (StringUtils.isEmpty(s)){
            s = "index";
        }
        String[] split = s.split("[?]");
        if (split.length>1){
            return split[0].indexOf(".")<0?s+".html":s;
        }else{
            return s.indexOf(".")<0?s+".html":s;
        }
    }

    public static String getDomainMatch(String url){
        Matcher matcher = DOMAIN_MATCH_PATTERN.matcher(url);
        return matcher.matches()?matcher.group():url;
    }
}
