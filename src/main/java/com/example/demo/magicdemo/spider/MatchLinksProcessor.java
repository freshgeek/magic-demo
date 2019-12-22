package com.example.demo.magicdemo.spider;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Html;
import us.codecraft.webmagic.selector.Selectable;

import java.net.URL;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author chen.chao
 * @version 1.0
 * @date 2019/12/12 20:05
 * @description
 */
@Slf4j
public class MatchLinksProcessor implements PageProcessor {

    private static final Pattern RESOURCES_PATTERN = Pattern.compile("css|js|txt|ico|png|jpg|gif|bmg|jpeg");

    private static final Pattern CHAR_PATTERN = Pattern.compile("css|js|txt");
    private static final Pattern BYTE_PATTERN = Pattern.compile("ico|png|jpg|gif|bmg|jpeg");

    private static final Pattern IMG_URL = Pattern.compile("<[img|IMG].*?src=['|\"](.*?(?:[.gif|.jpg]))['|\"].*?[/]?>");
    private static final Pattern CSS_URL = Pattern.compile("<[img|IMG].*?src=['|\"](.*?(?:[.gif|.jpg]))['|\"].*?[/]?>");
    private static final Pattern JS_URL = Pattern.compile("<[img|IMG].*?src=['|\"](.*?(?:[.gif|.jpg]))['|\"].*?[/]?>");

    private static final Pattern INNER_URL_PATTERN = Pattern.compile("(?<==\")((..)|(/)).+?(?=\")");
    private static final Pattern FULL_URL_PATTERN = Pattern.compile("(src=\".+\")|(href=\".+\"(( )|(/)|(>)))");

    private static Set<String> TOTAL = new CopyOnWriteArraySet<>();

    private String url ;
    public MatchLinksProcessor(String url) {
        this.url = url;
    }

    @Override
    public void process(Page page) {
        String url = page.getUrl().toString();
        String s1 = UrlUtil.trimDomain(url);
        String type = s1.substring(s1.lastIndexOf(".") + 1);

        Matcher matcher = RESOURCES_PATTERN.matcher(type);

        if (matcher.matches()){
            Matcher matcher1 = CHAR_PATTERN.matcher(type);
            if (matcher1.matches()){
                page.putField("body",Resources.builder()
                        .url(url).type(type).content(page.getRawText()).build());
            }else{
                page.putField("body",Resources.builder()
                        .url(url).type(type).buffer(page.getBytes()).build());
            }
        }else{
            // url
            String domainMatch = UrlUtil.getDomainMatch(url);
            if (domainMatch.indexOf(url)<0){
                return ;
            }
            final StringBuilder html = new StringBuilder(page.getRawText());
            //resources
            Set<String> pageSet = new HashSet<>();
            pageSet.addAll(page.getHtml().css("link", "href").all());
            pageSet.addAll(page.getHtml().css("img", "src").all());
            pageSet.addAll(page.getHtml().css("script", "src").all());

            Set<String> collect = page.getHtml().css("a", "href").all().stream().collect(Collectors.toSet());

            pageSet.remove("");
            pageSet.remove("#");
            collect.remove("");
            collect.remove("#");

            Consumer<String> tConsumer = s -> {
                String replace = UrlUtil.trimDomain(s);
                String newHtml = html.toString().replace(s, replace);
                html.setLength(0);
                html.append(newHtml);
            };

            //替换html中链接
            pageSet.forEach(tConsumer);
            collect.forEach(tConsumer);
            page.putField("body",Resources.builder()
                    .url(url).type("html").content(html.toString()).build());

            page.addTargetRequests(pageSet.stream().filter(v->!TOTAL.contains(v)).collect(Collectors.toList()));
            TOTAL.addAll(pageSet);

            // html页面
/*
            page.addTargetRequests(collect.stream()
                    .filter(v->!TOTAL.contains(UrlUtil.trimDomain(v)))
                    .collect(Collectors.toList()));
*/
            List<String> stringList = collect.stream().collect(Collectors.toList());

            for (int i = 0; i < stringList.size(); i++) {
                stringList.set(i,UrlUtil.trimDomain(stringList.get(i)));
            }
            TOTAL.addAll(stringList);
        }

    }

    @Override
    public Site getSite() {
        String coo = "UM_distinctid=16f13863d852c4-0d3b12fa34a2c2-7711439-144000-16f13863d886f; CNZZDATA1262118875=1936624158-1576579118-null%7C1576979744; JSESSIONID=1ab334e0-fea6-43d1-8bb7-13a74405268f";
        String[] split = coo.split(";");
        Site me = Site.me();
        if (split.length>0){
            Arrays.asList(split).forEach(s -> {
                String[] split1 = s.split("=");
                me.addCookie(split1[0].trim(),split1[1].trim());
            });
        }
        return me;
    }



}
