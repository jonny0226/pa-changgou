package com.changgou.file.util;

import com.changgou.file.pojo.FastDFSFile;
import org.csource.common.MyException;
import org.csource.common.NameValuePair;
import org.csource.fastdfs.*;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;

/**
 * 创建工具类 在该类中实现FastDFS信息获取以及文件的相关操作
 */
public class FastDFSClient {

    /*
     *初始化tracker信息
     */
    static {
        try {
            //获取tracker的配置文件的位置
            String filePath = new ClassPathResource("fdfs_client.conf").getPath();
            //加载tracker的配置文件
            ClientGlobal.init(filePath);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (MyException e) {
            e.printStackTrace();
        }
    }

    /**
     * 文件上传
     *
     * @param file 要上传的文件信息封装 成FastDFSFile
     * @return string[] 1 文件上传所存储的组名
     * 2 文件存储路径
     */
    public static String[] upload(FastDFSFile file) throws Exception {
        //1创建一个配置文件 配置服务器的ip和端口 已经生成
        //2加载配置文件     已经抽取成静态代码块
        //3创建一个trackerclient对象
        TrackerClient trackerClient = new TrackerClient();
        //4 获取到trackerserver对象
        TrackerServer trackerServer = trackerClient.getConnection();
        //5 创建一个storageserver
        StorageServer storageServer = null;
        //6 创建一个storageclient对象（有了很多操作图片的API）
        StorageClient storageClient = new StorageClient(trackerServer, storageServer);
        //7 上传图片
        //参数1 字节数组
        //参数2 指定图片的扩展民 不需要点
        //参数3 指定的就是图片的元数据（该图片的 拍摄日期，像素，拍摄的地点，作者。。。）

        //获取文件作者
        NameValuePair[] meta_list = new NameValuePair[]{
                new NameValuePair(file.getName()),//文件名
                new NameValuePair(file.getAuthor())//文件作者
        };

        String[] jpgs = storageClient.upload_file(file.getContent(), file.getExt(), meta_list);
        for (String jpg : jpgs) {
            System.out.println(jpg);
        }
        return jpgs;
    }

    //图片下载
    public static byte[] downFile(String groupName, String remoteFileName) throws Exception {
        //1创建一个配置文件 配置服务器的ip和端口 已经生成
        //2加载配置文件     已经抽取成静态代码块
        //3创建一个trackerclient对象
        TrackerClient trackerClient = new TrackerClient();
        //4 获取到trackerserver对象
        TrackerServer trackerserver = trackerClient.getConnection();
        //5 创建一个storageserver
        StorageServer storageServer = null;
        //6 创建一个storageclient对象（有了很多操作图片的API）
        StorageClient storageClient = new StorageClient(trackerserver, storageServer);
        //7 下载图片
        //参数1 指定的组名
        //参数2 指定的文件名
        byte[] bytes = storageClient.download_file(groupName, remoteFileName);

        return bytes;
    }


    //图片删除

    public static boolean deleteFile(String groupName, String remoteFileName) throws Exception {
        //1创建一个配置文件 配置服务器的ip和端口 已经生成
        //2加载配置文件     已经抽取成静态代码块
        //3创建一个trackerclient对象
        TrackerClient trackerClient = new TrackerClient();
        //4 获取到trackerserver对象
        TrackerServer trackerserver = trackerClient.getConnection();
        //5 创建一个storageserver
        StorageServer storageServer = null;
        //6 创建一个storageclient对象（有了很多操作图片的API）
        StorageClient storageClient = new StorageClient(trackerserver, storageServer);

        int i = storageClient.delete_file(groupName, remoteFileName);
        if (i == 0) {
            System.out.println("成功");
            return true;
        } else {
            System.out.println("失败");
            return false;
        }
    }

    //获取数组信息
    public static ServerInfo[] getFileGroupInfo(String groupName, String remoteFileName) throws Exception {
        //1创建一个配置文件 配置服务器的ip和端口 已经生成
        //2加载配置文件     已经抽取成静态代码块
        //3创建一个trackerclient对象
        TrackerClient trackerClient = new TrackerClient();
        //4 获取到trackerserver对象
        TrackerServer trackerserver = trackerClient.getConnection();
        //5 创建一个storageserver
        StorageServer storageServer = null;
        //6 创建一个storageclient对象（有了很多操作图片的API）
        StorageClient storageClient = new StorageClient(trackerserver, storageServer);
        //获取数组信息
        ServerInfo[] groups = trackerClient.getFetchStorages(trackerserver, groupName, remoteFileName);
        return groups;
    }

    /**
     * 动态获取ip 端口
     * @return
     */
    public static String getTrackerUrl(){
        try {
            TrackerClient trackerClient = new TrackerClient();

            TrackerServer trackerserver = trackerClient.getConnection();

            return "http://"+trackerserver.getInetSocketAddress().getHostString()+":"+ClientGlobal.getG_tracker_http_port();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
