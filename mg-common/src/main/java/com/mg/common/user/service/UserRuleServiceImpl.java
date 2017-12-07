package com.mg.common.user.service;

import com.mg.common.entity.UserEntity;
import com.mg.common.entity.UserRuleEntity;
import com.mg.common.metadata.service.MetaDataService;
import com.mg.common.utils.MD5;
import com.mg.common.metadata.service.MetaDataQueryService;
import com.mg.common.user.dao.UserDao;
import com.mg.common.user.dao.UserRuleDao;
import com.mg.framework.entity.metadata.MObjectEntity;
import com.mg.framework.entity.metadata.MirrorPropertyEntity;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * Created by liukefu on 2015/12/7.
 */
@Service
public class UserRuleServiceImpl implements UserRuleService {
    @Autowired
    private UserDao userDao;
    @Autowired
    private UserRuleDao userRuleDao;
    @Autowired
    private MetaDataService metaDataService;
    @Autowired
    private MetaDataQueryService metaDataQueryService;
    /**
     * 获取系统帐户规则
     *
     * @return
     */
    public UserRuleEntity get() {
        List<UserRuleEntity> list = userRuleDao.findAll();
        UserRuleEntity userRuleEntity = null;
        if (list.size() > 0) {
            userRuleEntity = list.get(0);
        }
        if (userRuleEntity == null) {
            userRuleEntity = new UserRuleEntity();
            //saveRule(userRuleEntity);
        }

        return userRuleEntity;
    }

    @Transactional
    public UserRuleEntity saveRule(UserRuleEntity userRuleEntity){

        boolean changeRule = false;
        if(StringUtils.isNotBlank(userRuleEntity.getId())){
            UserRuleEntity oldRuleEntity = userRuleDao.findOne(userRuleEntity.getId());
            if(!StringUtils.equals(oldRuleEntity.getLoginName(),userRuleEntity.getLoginName())){
                changeRule = true;
            }
        }
        userRuleDao.save(userRuleEntity);

        if(changeRule){
            List<UserEntity> list = userDao.findAll();
            for(UserEntity userEntity:list){
                initUser(userEntity,false);
            }
            userDao.save(list);
        }

        return userRuleEntity;
    }

    /**
     * 初始化登录用户信息
     * @param userEntity
     * @param initPass 是否重置密码
     * @return
     */
    public UserEntity initUser(UserEntity userEntity,boolean initPass) {

        UserRuleEntity userRuleEntity = get();
        if(initPass){
            String password = MD5.GetMD5Code(userRuleEntity.getDefaultPassword());
            userEntity.setPassword(password);
        }

        MObjectEntity mObjectEntity = metaDataQueryService.findEmployeeMObject();
        if(mObjectEntity!=null){
            MirrorPropertyEntity propertyEntity = metaDataQueryService.findMPropertyByBelongMObjectAndName(mObjectEntity, userRuleEntity.getLoginName());
            if(propertyEntity!=null){
                Map<String,Object> empMap = metaDataService.queryByName(mObjectEntity.getId(), userEntity.getName());
                Object loginName = empMap.get(propertyEntity.getPropertyPath());
                if(loginName!=null){
                    userEntity.setLoginName(String.valueOf(loginName));
                }else if(StringUtils.isBlank(userEntity.getLoginName())){
                    userEntity.setLoginName(userEntity.getName());
                }
            }
        }
        return userEntity;
    }
}
