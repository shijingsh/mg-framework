package com.mg.common.upload.service;

import com.mg.common.upload.vo.UploadBean;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.util.List;
import java.util.Map;

/**
 * 图片上传公用类
 */
public interface UploadService {

    List<UploadBean> upload(MultipartHttpServletRequest mulRequest, String userPath);

    Map<String,UploadBean> uploadForMap(MultipartHttpServletRequest mulRequest, String userPath);

    boolean removeFile(String path);
}
