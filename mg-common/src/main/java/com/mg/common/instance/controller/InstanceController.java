package com.mg.common.instance.controller;

import com.alibaba.fastjson.JSON;
import com.mg.common.entity.InstanceEntity;
import com.mg.framework.utils.WebUtil;
import com.mg.common.instance.service.InstanceService;
import com.mg.framework.entity.vo.PageTableVO;
import com.mg.framework.utils.JsonResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 公司实例管理 restful 接口
 * @author liukefu
 */
@Controller
@RequestMapping(value = "/instance",
        produces = "application/json; charset=UTF-8")
public class InstanceController {
    @Autowired
    private InstanceService instanceService;
    /**
     * 获取所有的公司实例
     * @return
     */
    @ResponseBody
    @RequestMapping("/all")
    public String all() {
        List<InstanceEntity> list = instanceService.findInstanceAll();

        return JsonResponse.success(list, null);
    }

    /**
     * 获取单个的公司实例
     * @return
     */
    @ResponseBody
    @RequestMapping("/get")
    public String get(String id) {
        InstanceEntity instanceEntity = instanceService.findInstanceById(id);

        return JsonResponse.success(instanceEntity, null);
    }

    /**
     * 获取单个的公司实例
     * @return
     */
    @ResponseBody
    @RequestMapping("/post")
    public String post(HttpServletRequest req) {
        String jsonString = WebUtil.getJsonBody(req);
        InstanceEntity instanceEntity =  JSON.parseObject(jsonString, InstanceEntity.class);
        instanceService.save(instanceEntity);
        return JsonResponse.success(instanceEntity, null);
    }
    /**
     * 参数列表分页模式
     * @param req   请求
     * @return          PageTableVO
     */
    @ResponseBody
    @RequestMapping("/listPage")
    public String listPage(HttpServletRequest req) {
        String jsonString = WebUtil.getJsonBody(req);
        PageTableVO  param =  JSON.parseObject(jsonString, PageTableVO.class);

        PageTableVO vo = instanceService.findPageList(param.getPageNo(), param.getPageSize());

        return JsonResponse.success(vo, null);
    }



}