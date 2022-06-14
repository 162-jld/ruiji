package com.beim.ruiji.controller;

import com.beim.ruiji.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
    @GetMapping("/download")
    public void download(String name, HttpServletResponse response){
        try {
            // 通过输入流读取文件内容
            FileInputStream fileInputStream = new FileInputStream(basePath + name);
            // 设置响应数据的格式
            response.setContentType("image/jpeg");
            // 通过输出流输出文件，在浏览器中展示文件
            ServletOutputStream outputStream = response.getOutputStream();

            int len = 0;
            byte[] bytes = new byte[1024];
            // 每次读取bytes个长度，当等于-1的时候表示读取完
            while ((len =  fileInputStream.read(bytes)) != -1){
                // 代表已经读取完毕
                outputStream.write(bytes,0,len);
                // 刷新
                outputStream.flush();
            }
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
