package com.changgou.file.controller;

import com.changgou.file.pojo.FastDFSFile;
import com.changgou.file.util.FastDFSClient;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件上传的controller 实现文件上传操作
 */
@RestController
public class FileController {
    /**
     * 前端 点击上传后 携带 图片到后台
     * 后台接收数据 再把数据上传到fastDFS
     * 返回一个路径 并且在前端 回显数据
     */

    @RequestMapping("/upload")
    public String upload(MultipartFile file){
        if (!file.isEmpty()){//判断文件是否为空
            try {
                //创建封装的文件对象
                //设置 字节数组
                byte[] bytes = file.getBytes();
                //设置 扩展名 不带点的
                String extname = StringUtils.getFilenameExtension(file.getOriginalFilename());
                //设置 文件的文件名
                String originalFilename = file.getOriginalFilename();
                FastDFSFile fastDFSFile = new FastDFSFile(originalFilename, bytes, extname);
                //上传图片到 fastdfs上
                //jpgs[0]=group1
                //jpgs[1]=M00/00/00/wKjThF4SsmKACA--AACAThdn_1U136.jpg
                String[] upload = FastDFSClient.upload(fastDFSFile);
                String realpath = FastDFSClient.getTrackerUrl()+"/"+upload[0]+"/"+upload[1];
                return realpath;
            } catch (Exception e) {
                e.printStackTrace();
                return null;//异常返回null
            }
        }
        return null;//文件为空就返回null
    }
}
