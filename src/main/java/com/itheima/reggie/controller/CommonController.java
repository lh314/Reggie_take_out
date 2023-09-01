package com.itheima.reggie.controller;

import com.itheima.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.UUID;

/**
 * @author lh
 * @Date 2023/8/3 13:58
 * @ 意图：
 */
@RestController
@Slf4j
@RequestMapping("/common")
public class CommonController {

   @Value("${reggie.path}")
    private String basePath;
    @PostMapping("/upload")
    public R<String> upload(MultipartFile file) {
        log.info("获取文件: {}",file.toString());
        File dir = new File(basePath);
        if(!dir.exists()){
            dir.mkdirs();
        }
//        获取原文件名
        String originalFilename = file.getOriginalFilename();
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
//       使用uuid重名
        String filename = UUID.randomUUID() + suffix;
        try {
            file.transferTo(new File(basePath+filename) {
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return R.success(filename);
    }
    @GetMapping("/download")
    public void download(String name, HttpServletResponse response){
// 创建一个文件输入流 fis，用于读取指定路径的文件内容。
        FileInputStream fis = null;
//获取当前请求的响应流 os，用于将数据写入其中。
        ServletOutputStream os = null;
        try {
            fis=new FileInputStream(basePath+name);
            os = response.getOutputStream();
            response.setContentType("image/jpeg");
            //创建一个长度为 1024 的字节数组 buffer 作为缓冲区，用于从文件输入流中读取数据。
            byte[] buffer = new byte[1024];
            Integer len;
//当读取到文件末尾时，fis.read(buffer) 方法会返回 -1，循环终止。
            while ((len=fis.read(buffer))!=-1) {
                //使用循环从文件输入流中读取数据，每次最多读取 1024 字节，并将读取的数据写入响应流中，实现文件下载的功能。
                os.write(buffer,0,len);
                os.flush();
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }finally {
            if(fis!=null){
                try {
                    //关闭文件输入流 fis，释放相关的资源。
                    fis.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            if(os!=null){
                try {
                    //关闭响应流 os，完成文件下载操作，确保数据被正确发送到客户端。
                    os.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        }









}
