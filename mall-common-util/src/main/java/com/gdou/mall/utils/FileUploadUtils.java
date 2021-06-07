package com.gdou.mall.utils;

import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public class FileUploadUtils {
    public static String uploadImg(MultipartFile multipartFile) {
        //linux IP地址
        String imgUrl = ProjectPara.LINUXIP;
        String path = FileUploadUtils.class.getResource("/tracker.conf").getPath();
        try {
            ClientGlobal.init(path);
        } catch (Exception e) {
            e.printStackTrace();
        }
        TrackerClient trackerClient = new TrackerClient();
        TrackerServer trackerServer = null;
        try {
            trackerServer = trackerClient.getConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //获取StorageClient
        StorageClient storageClient = new StorageClient(trackerServer, null);

        //上传文件
        try {
            byte[] imgByte = multipartFile.getBytes();
            String extName = multipartFile.getOriginalFilename().substring(multipartFile.getOriginalFilename().lastIndexOf(".") + 1);
            String[] uploadInfos = storageClient.upload_file(imgByte,extName,null);

            for (String uploadInfo : uploadInfos) {
                imgUrl += "/" + uploadInfo;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return imgUrl;
    }
}
