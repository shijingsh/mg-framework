package com.mg.common.upload.controller;

import com.mg.common.upload.service.UploadService;
import com.mg.common.upload.vo.UploadBean;
import com.mg.framework.utils.JsonResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.util.List;


/**
 * 上传文件
 * @author liukefu
 */
@Controller
@RequestMapping(value = "/",
        produces = "application/json; charset=UTF-8")
public class UploadController{
    @Autowired
    private UploadService uploadService;
    /**
     * 上传文件
     * @param request
     * @param userPath  在temp 下自定义一个目录
     * @return
     */
    @ResponseBody
    @RequestMapping("/upload")
    public String upload(HttpServletRequest request,String userPath) {

        MultipartHttpServletRequest mulRequest = (MultipartHttpServletRequest) (request);

        List<UploadBean> list = uploadService.upload(mulRequest,userPath);

        return JsonResponse.success(list, null);
    }


}
