package com.mg.common.metadata.controller;

import com.alibaba.fastjson.JSONObject;
import com.mg.common.metadata.service.MEnumService;
import com.mg.common.metadata.service.MetaDataManageService;
import com.mg.common.metadata.service.MetaDataQueryService;
import com.mg.framework.entity.metadata.MEnumEntity;
import com.mg.framework.utils.WebUtil;
import com.mg.framework.utils.JsonResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 元数据模板设置
 * @author liukefu
 *
 */
@Controller
@RequestMapping(value = "/metadata",produces = "application/json; charset=UTF-8")
public class MEnumController {
    Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    public MetaDataManageService metaDataManageService;
    @Autowired
    public MetaDataQueryService metaDataQueryService;
    @Autowired
    public MEnumService mEnumService;

    /**
     * 枚举类型列表
     * @param req   请求
     * @return          list
     */
    @ResponseBody
    @RequestMapping("/enum/list")
    public String listEnum(HttpServletRequest req) {

        List<MEnumEntity> list = mEnumService.findAllEnum();
        return JsonResponse.success(list, null);
    }
    /**
     * 根据枚举名称查询枚举类型
     * @param req   请求
     * @return     list
     */
    @ResponseBody
    @RequestMapping("/enum/listByName")
    public String listEnumByName(HttpServletRequest req) {
        String jsonString = WebUtil.getJsonBody(req);
        JSONObject param = JSONObject.parseObject(jsonString, JSONObject.class);
        List<MEnumEntity> list = mEnumService.findByEnumName(param.getString("name"));
        return JsonResponse.success(list, null);
    }
}
