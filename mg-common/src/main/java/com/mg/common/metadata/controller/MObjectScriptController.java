package com.mg.common.metadata.controller;

import com.alibaba.fastjson.JSON;
import com.mg.common.metadata.service.MObjectScriptService;
import com.mg.common.metadata.service.MetaDataQueryService;
import com.mg.framework.entity.metadata.MObjectScriptEntity;
import com.mg.framework.utils.WebUtil;
import com.mg.framework.entity.metadata.MObjectEntity;
import com.mg.framework.entity.vo.PageTableVO;
import com.mg.framework.utils.JsonResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 元数据批量任务
 * @author liukefu
 */
@Controller
@RequestMapping(value = "/metadata/script",produces = "application/json; charset=UTF-8")
public class MObjectScriptController {
    @Autowired
    MObjectScriptService mObjectScriptService;
    @Autowired
    MetaDataQueryService metaDataQueryService;
    /**
     * 对象的脚本的列表
     * @return          list
     */
    @ResponseBody
    @RequestMapping("/list")
    public String list(String mainObjId) {

        MObjectEntity mObjectEntity = metaDataQueryService.findMObjectById(mainObjId);
        List<MObjectScriptEntity> list = mObjectScriptService.findByBelongMObject(mObjectEntity);
        return JsonResponse.success(list, null);
    }
    /**
     * 对象的脚本的列表
     * @return          listPage
     */
    @ResponseBody
    @RequestMapping("/listPage")
    public String listPage(HttpServletRequest req,String mainObjId) {
        String jsonString = WebUtil.getJsonBody(req);

        PageTableVO pageTableVO = JSON.parseObject(jsonString, PageTableVO.class);

        MObjectEntity mObjectEntity = metaDataQueryService.findMObjectById(mainObjId);
        pageTableVO = mObjectScriptService.findPageList(pageTableVO,mObjectEntity);
        return JsonResponse.success(pageTableVO, null);
    }
    /**
     *  根据ID获取
     * @param id   请求
     * @return
     */
    @ResponseBody
    @RequestMapping("/get")
    public String getScript(String  id) {

        MObjectScriptEntity scriptEntity = mObjectScriptService.findById(id);
        return JsonResponse.success(scriptEntity, null);
    }
    /**
     * 保存
     * @return          list
     */
    @ResponseBody
    @RequestMapping("/post")
    public String postScript(HttpServletRequest req,String objId) {
        String jsonString = WebUtil.getJsonBody(req);

        MObjectScriptEntity scriptEntity = JSON.parseObject(jsonString, MObjectScriptEntity.class);

        MObjectEntity mObjectEntity = metaDataQueryService.findMObjectById(objId);
        scriptEntity.setBelongMObject(mObjectEntity);

        mObjectScriptService.save(scriptEntity);
        return JsonResponse.success(null, null);
    }
}
