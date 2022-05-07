package com.beim.ruiji.controller;

import com.beim.ruiji.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.UUID;

/**
 * 文件上传与下载
 */

@RestController
@RequestMapping(value = "/common")
@Slf4j
public class CommonController {

    @Value("${ruiji.path}")
    private String basePath;


    /**
     * 文件的上传
     * @param file
     * @return
     */
    @PostMapping("/upload")
    public R<String> upload(MultipartFile file){
        // file为临时文件，需要转存到指定目录，否则本次请求完成后临时文件会删除
        log.info(file.toString());

        // 获取原始文件名称
        String originalFilename = file.getOriginalFilename();

        // 截取文件后缀名
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));

        // 拼接文件名
        String fileName = UUID.randomUUID().toString() + suffix;

        // 判断转存目录是否存在,如果不存在则创建目录，如果存在则不创建
        File dir = new File(basePath);
        if (!dir.exists()){
            dir.mkdirs();
        }


        // 转存到指定目录
        try {
            file.transferTo(new File(basePath + fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return R.success(fileName);
    }


    /**
     * 文件的下载
     * @param name
     * @param response
     */
    public void download(String name, HttpServletResponse response){

        // 通过输入流读取文件内容
//        FileInputStream inputStream = new FileInputStream()

    }



}
