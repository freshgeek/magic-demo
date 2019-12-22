package com.example.demo.magicdemo.spider;

import lombok.Builder;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.PageModelPipeline;
import us.codecraft.webmagic.pipeline.Pipeline;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;

/**
 * @author chen.chao
 * @version 1.0
 * @date 2019/12/19 20:05
 * @description
 */

@Slf4j
@Data
public class SaveFilePipe implements Pipeline {
    private static File dir;

    public SaveFilePipe(File dir) {
        SaveFilePipe.dir = dir;
    }

    @SneakyThrows
    @Override
    public void process(ResultItems resultItems, Task task) {
        Resources body = resultItems.get("body");
        if (body == null) {
            log.debug(" if (body == null) {");
            return;
        }
        log.info("链接:[{}]", body.getUrl());
        String replaceAll = UrlUtil.trimDomain(body.getUrl());
        log.info("去除域名后:[{}]", replaceAll);
        String pathAndName = replaceAll.split("[?]")[0];
        log.info("去除参数后:[{}]", pathAndName);
        if (StringUtils.isEmpty(pathAndName)||"/".equals(pathAndName)){
            pathAndName = "index.html";
        }
        File local = new File(dir, pathAndName);
        if (!local.getParentFile().exists() || !local.getParentFile().isDirectory()) {
            local.getParentFile().mkdirs();
        }
        if (local.exists()) {
            log.error("文件已经存在,路径[{}]", pathAndName);
            return ;
        } else {
            local.createNewFile();
        }

        try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(local))) {
            if (body.getContent() != null) {
                bos.write(body.getContent().getBytes());
            } else {
                bos.write(body.getBuffer());
            }
            bos.flush();
        }

    }
}
