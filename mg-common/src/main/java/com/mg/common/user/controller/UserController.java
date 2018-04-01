package com.mg.common.user.controller;

import com.alibaba.fastjson.JSON;
import com.mg.common.components.SmsService;
import com.mg.common.entity.RoleEntity;
import com.mg.common.entity.UserEntity;
import com.mg.common.metadata.service.MetaDataExpressService;
import com.mg.common.metadata.service.MetaDataQueryService;
import com.mg.common.shiro.service.RoleCacheService;
import com.mg.common.upload.service.UploadService;
import com.mg.common.upload.vo.UploadBean;
import com.mg.common.user.service.UserService;
import com.mg.framework.entity.metadata.MExpressGroupEntity;
import com.mg.framework.entity.metadata.MObjectEntity;
import com.mg.framework.log.Constants;
import com.mg.framework.utils.WebUtil;
import com.mg.common.utils.MD5;
import com.mg.framework.entity.vo.PageTableVO;
import com.mg.framework.utils.JsonResponse;
import com.mg.framework.utils.UserHolder;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
@RequestMapping(value = "/user",
        produces = "application/json; charset=UTF-8")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private HttpServletRequest req;
    @Autowired
    MetaDataQueryService metaDataQueryService;
    @Autowired
    MetaDataExpressService metaDataExpressService;
    @Autowired
    RoleCacheService roleCacheService;
    @Autowired
    private UploadService uploadService;
    @Autowired
    private SmsService smsService;

    @ResponseBody
    @RequestMapping("/modify")
    public String modify() {

        String jsonString = WebUtil.getJsonBody(req);

        UserEntity user = JSON.parseObject(jsonString, UserEntity.class);
        try {
            userService.updateUser(user);
        } catch (Exception e) {
            return JsonResponse.error(10000, "修改员工信息出现异常");
        }

        return JsonResponse.success(user);
    }

    @ResponseBody
    @RequestMapping("/modifyPass")
    public String modifyPass() {

        String pswOld = req.getParameter("oldPassword");
        String psw = req.getParameter("newPassword");
        String pswConfirm = req.getParameter("confirmPassword");
        if (StringUtils.isBlank(psw)) {
            return JsonResponse.error(1, "请输入登录密码");
        }
        if (psw.length() < 6) {
            return JsonResponse.error(1, "密码长度不能少于6位！");
        }
        String userId = UserHolder.getLoginUserId();
        if (StringUtils.isBlank(userId)) {
            return JsonResponse.error(1, "登录超时，请重新登录！");
        }
        UserEntity userEntity = userService.getUserById(userId);
        //String oldPassMd5 = MD5.GetMD5Code(pswOld);
        if (!StringUtils.equals(pswOld, userEntity.getPassword())) {
            return JsonResponse.error(1, "原密码不正确，请重新输入");
        }
        if (!StringUtils.equals(psw, pswConfirm)) {
            return JsonResponse.error(1, "两次密码输入不一致，请重新输入");
        }
        userEntity.setPassword(psw);
        try {
            userService.updateUser(userEntity);
        } catch (Exception e) {
            return JsonResponse.error(10000, "修改密码失败！");
        }
        Session session = SecurityUtils.getSubject().getSession();
        session.setAttribute(Constants.CURRENT_USER, userEntity);
        return JsonResponse.success(userEntity);
    }

    /**
     * 首次登陆修改密码
     *
     * @return
     */
    @ResponseBody
    @RequestMapping("/modifyPassFirst")
    public String modifyPassFirst() {

        String psw = req.getParameter("newPassword");
        String pswConfirm = req.getParameter("confirmPassword");
        UserEntity userSession = UserHolder.getLoginUser();
        if (userSession.getLastLoginDate() != null) {
            return JsonResponse.error(1, "非法访问");
        }
        if (StringUtils.isBlank(psw)) {
            return JsonResponse.error(1, "请输入登录密码");
        }

        if (psw.length() < 6) {
            return JsonResponse.error(1, "密码长度不能少于6位！");
        }
        String userId = UserHolder.getLoginUserId();
        if (StringUtils.isBlank(userId)) {
            return JsonResponse.error(1, "登录超时，请重新登录！");
        }
        UserEntity userEntity = userService.getUserById(userId);

        if (!StringUtils.equals(psw, pswConfirm)) {
            return JsonResponse.error(1, "两次密码输入不一致，请重新输入");
        }
        userEntity.setPassword(MD5.GetMD5Code(psw));
        try {
            userService.updateUser(userEntity);
        } catch (Exception e) {
            return JsonResponse.error(10000, "修改密码失败！");
        }
        Session session = SecurityUtils.getSubject().getSession();
        session.setAttribute(Constants.CURRENT_USER, userEntity);
        return JsonResponse.success();
    }

    @ResponseBody
    @RequestMapping("/resetPassword")
    public String resetPassword() {

        String jsonString = WebUtil.getJsonBody(req);
        UserEntity userEntity = JSON.parseObject(jsonString, UserEntity.class);
        if (StringUtils.isBlank(userEntity.getLoginName()) || StringUtils.isBlank(userEntity.getPassword())) {
            return JsonResponse.error(100000, "用户名,密码不能为空。");
        }

        UserEntity user = userService.getUser(userEntity.getLoginName());
        if (user == null) {
            return JsonResponse.error(100000, "用户尚未注册");
        }
        if(StringUtils.isBlank(user.getMobile())){
            user.setMobile(user.getLoginName());
        }
        String code = req.getParameter("code").trim();
        if(smsService.validateCode(user.getMobile(),code)){
            user.setPassword(userEntity.getPassword());
            userService.updateUser(user);
        }else{
            return JsonResponse.error(100000, "验证码输入错误");
        }
        return JsonResponse.success(user);
    }
    /**
     * 获取某一个人的是否拥有某个角色
     */
    @ResponseBody
    @RequestMapping("/hasRole")
    public String hasRole() {
        String jsonString = WebUtil.getJsonBody(req);

        RoleEntity roleEntity = JSON.parseObject(jsonString, RoleEntity.class);

        boolean hasRole = roleCacheService.hasAnyRole(SecurityUtils.getSubject(),roleEntity.getName());
        return JsonResponse.success(hasRole, null);
    }

    /**
     * 分页查询用户登录帐号
     *
     * @return
     */
    @ResponseBody
    @RequestMapping("/pageList")
    public String getPageList() {
        String jsonString = WebUtil.getJsonBody(req);
        PageTableVO param = JSON.parseObject(jsonString, PageTableVO.class);

        PageTableVO vo = userService.findPageList(param);

        return JsonResponse.success(vo, null);
    }

    /**
     * 初始化用户登录帐号的密码
     *
     * @return
     */
    @ResponseBody
    @RequestMapping("/initPassWord")
    public String initPassWord(String id) {

        userService.saveInitUserPassWord(id);

        return JsonResponse.success(null, null);
    }

    /**
     * 删除用户
     * @param id
     * @return
     */
    @ResponseBody
    @RequestMapping("/delete")
    public String delete(String id) {

        userService.delete(id);

        return JsonResponse.success(null, null);
    }

    @RequestMapping(value = "/createUser", method = RequestMethod.POST)
    @ResponseBody
    public String createUser(String objId) {
        String jsonString = WebUtil.getJsonBody(req);
        MExpressGroupEntity express = JSON.parseObject(jsonString, MExpressGroupEntity.class);

        MObjectEntity metaObject = metaDataQueryService.findMObjectById(objId);
        if (express == null || express.getMatched() == null
                || express.getMatched().getExpressions() == null
                || express.getMatched().getExpressions().size() == 0) {
            express = metaDataExpressService.createBlankExpressGroup(express);

        }

        Integer num = userService.createUser(metaObject, express);

        return JsonResponse.successWithDate(num, "yyyy-MM-dd");
    }

    @ResponseBody
    @RequestMapping("/headPortrait")
    public String headPortrait(HttpServletRequest request,String userPath) {

        MultipartHttpServletRequest mulRequest = (MultipartHttpServletRequest) (request);

        List<UploadBean> list = uploadService.upload(mulRequest,userPath);
        UploadBean bean = list!=null&&list.size()>0?list.get(0):new UploadBean();
        UserEntity userEntity = userService.getUserByRequest(request);

        if(userEntity!=null){
            if(StringUtils.isNotBlank(userEntity.getHeadPortrait())){
                uploadService.removeFile(userEntity.getHeadPortrait());
            }
            userEntity.setHeadPortrait(bean.getRelativePath());
            userService.updateUser(userEntity);
        }

        return JsonResponse.success(userEntity, null);
    }
}
