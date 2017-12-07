package com.mg.common.upload.service;

import com.mg.common.upload.vo.UploadBean;
import com.mg.framework.sys.PropertyConfigurer;
import com.mg.framework.utils.UserHolder;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.io.File;
import java.util.*;

/**
 * 图片上传公用类
 * Created by liukefu on 2016/12/17.
 */
@Service
public class UploadServiceImpl implements UploadService {
    private static Logger logger = LoggerFactory.getLogger(UploadServiceImpl.class);
    private static final char separator = '/';

    public List<UploadBean> upload(MultipartHttpServletRequest mulRequest, String userPath) {

        List<UploadBean> list = new ArrayList<>();
        Map<String, MultipartFile> fileMap = mulRequest.getFileMap();
        Iterator<String> it = fileMap.keySet().iterator();
        while (it.hasNext()) {
            UploadBean uploadBean = new UploadBean();
            uploadBean.setUserPath(userPath);

            //保存文件到服务器
            File file = getFileSavePath(uploadBean);
            String key = it.next();
            uploadBean.setKey(key);
            MultipartFile multipartFile = fileMap.get(key);
            if (!multipartFile.isEmpty()) {
                File f = new File(getNewFileName(file, multipartFile));
                logger.info("设置上传文件权限");
                f.setReadable(true,false);
                f.setWritable(true,false);
                f.setExecutable(true,false);
                try {
                    multipartFile.transferTo(f);


                    uploadBean.setFileName(f.getName());
                    uploadBean.setPath(f.getPath());
                    logger.info("file path : {}", file.getPath());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            //返回文件路径
            uploadBean.setRelativePath(uploadBean.getRelativePath() + uploadBean.getFileName());

            list.add(uploadBean);
        }

        return list;
    }

    public Map<String,UploadBean> uploadForMap(MultipartHttpServletRequest mulRequest, String userPath) {

        Map<String,UploadBean> map = new HashMap<>();
        Map<String, MultipartFile> fileMap = mulRequest.getFileMap();
        Iterator<String> it = fileMap.keySet().iterator();
        while (it.hasNext()) {
            UploadBean uploadBean = new UploadBean();
            uploadBean.setUserPath(userPath);

            //保存文件到服务器
            File file = getFileSavePath(uploadBean);
            String key = it.next();
            uploadBean.setKey(key);
            MultipartFile multipartFile = fileMap.get(key);
            if (!multipartFile.isEmpty()) {
                File f = new File(getNewFileName(file, multipartFile));
                try {
                    multipartFile.transferTo(f);
                    logger.info("uploadForMap 设置上传文件权限");
                    f.setReadable(true,false);
                    f.setWritable(true,false);
                    //f.setExecutable(true,false);

                    uploadBean.setFileName(f.getName());
                    uploadBean.setPath(f.getPath());
                    logger.info("file path : {}", file.getPath());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            //返回文件路径
            uploadBean.setRelativePath(uploadBean.getRelativePath() + uploadBean.getFileName());

            map.put(key,uploadBean);
        }

        return map;
    }

    private String getNewFileName(File file, MultipartFile item) {
        StringBuffer sb = new StringBuffer(file.getPath()).append(separator);
        String str = String.valueOf(Math.round(Math.random() * 1000000));
        sb.append("mg").append(new Date().getTime()).append(str);
        sb.append(item.getOriginalFilename().substring(item.getOriginalFilename().lastIndexOf(".")));
        return sb.toString();
    }

    public File getFileSavePath(UploadBean uploadBean) {
        String instanceId = UserHolder.getLoginUserTenantId();
        String rootPath = "mg-static";
        if (StringUtils.isNotBlank(instanceId)) {
            rootPath = instanceId;
        }
        String savePath = separator + rootPath + separator;

        if (StringUtils.isNotBlank(uploadBean.getUserPath())) {
            String today = DateFormatUtils.format(new Date(), "yyyyMMdd");
            savePath = savePath + uploadBean.getUserPath() + separator + today + separator;
        }
        uploadBean.setRelativePath(savePath);
        savePath = PropertyConfigurer.getContextProperty("temppath") + savePath;
        File file = new File(savePath);
        file.mkdirs();

        return file;
    }

    public boolean removeFile(String path) {

        String home = (String) PropertyConfigurer.getContextProperty("temppath");
        File file = new File(home + path);
        file.deleteOnExit();
        return true;
    }
}
