package com.mg.common.user.service;

import com.alibaba.fastjson.JSONObject;
import com.mg.common.entity.*;
import com.mg.common.metadata.dao.MirrorPropertyDao;
import com.mg.framework.entity.metadata.MExpressGroupEntity;
import com.mg.framework.entity.metadata.MObjectEntity;
import com.mg.framework.entity.metadata.MirrorPropertyEntity;
import com.mysema.query.jpa.impl.JPAQuery;
import com.mysema.query.types.expr.BooleanExpression;
import com.mg.common.entity.vo.MenuTypeEnum;
import com.mg.common.entity.vo.PermissionActionEnum;
import com.mg.common.metadata.service.MetaDataExpressService;
import com.mg.common.metadata.service.MetaDataQueryService;
import com.mg.common.metadata.service.MetaDataService;
import com.mg.common.metadata.util.MetaDataUtils;
import com.mg.common.shiro.service.RoleCacheService;
import com.mg.common.user.dao.PermissionDao;
import com.mg.common.user.dao.RoleDao;
import com.mg.common.user.dao.RoleDataScopeDao;
import com.mg.common.user.dao.UserDao;
import com.mg.common.utils.LazyLoadUtil;
import com.mg.common.utils.MD5;
import com.mg.framework.entity.metadata.MInVisibleTypeEnum;
import com.mg.framework.entity.vo.PageTableVO;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by liukefu on 2016/1/9.
 */
@Service
public class RoleServiceImpl implements RoleService {

    public Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private RoleDao roleDao;
    @Autowired
    private UserDao userDao;
    @Autowired
    private PermissionDao permissionDao;
    @Autowired
    private MirrorPropertyDao mirrorPropertyDao;
    @Autowired
    private RoleDataScopeDao roleDataScopeDao;
    @PersistenceContext
    private EntityManager entityManager;
    @Autowired
    MetaDataService metaDataService;
    @Autowired
    MetaDataQueryService metaDataQueryService;
    @Autowired
    UserRuleService userRuleService;
    @Autowired
    UserService userService;
    @Autowired
    MetaDataExpressService metaDataExpressService;

    @Transactional(readOnly = true)
    public RoleEntity get(String id) {
        RoleEntity roleEntity = roleDao.findOne(id);
        Hibernate.initialize(roleEntity.getDataScopeList());
        LazyLoadUtil.fullLoad(roleEntity.getMembersScope());
        return roleEntity;
    }

    public RoleEntity getByName(String name) {
        List<RoleEntity> list = roleDao.findByName(name);
        if (list.size() > 0) {
            return list.get(0);
        }
        return null;
    }

    @Transactional
    public RoleEntity save(RoleEntity roleEntity) {
        if (roleEntity.getMembersScope() != null) {
            MObjectEntity empObject = metaDataQueryService.findEmployeeMObject();
            MExpressGroupEntity expressGroupEntity = metaDataExpressService.initExpressBeforeSave(roleEntity.getMembersScope());
            expressGroupEntity.setMetaObject(empObject);
            roleEntity.setMembersScope(expressGroupEntity);
        }
        roleDao.saveAndFlush(roleEntity);
        //更新角色缓存
        if (RoleCacheService.cacheProvider.get(roleEntity.getName()) == null) {
            RoleCacheService.cacheProvider.put(roleEntity.getName(), roleEntity.getId());
        }
        return roleEntity;
    }

    @Transactional
    public void remove(String id) {
        RoleEntity roleEntity = get(id);

        roleEntity.getMembers().clear();
        roleDao.delete(roleEntity);

    }

    @Transactional
    public void addUser(String id, String userId) {
        RoleEntity roleEntity = get(id);

        UserEntity userEntity = userDao.findOne(userId);
        if (!roleEntity.getMembers().contains(userEntity)) {
            roleEntity.getMembers().add(userEntity);
            roleDao.saveAndFlush(roleEntity);
        }
    }

    @Transactional
    public void removeUser(String id, String userId) {
        RoleEntity roleEntity = get(id);

        int index = -1;
        int i = 0;
        for (UserEntity user : roleEntity.getMembers()) {
            if (userId.equals(user.getId())) {
                index = i;
            }
            i++;
        }
        if (index >= 0) {
            roleEntity.getMembers().remove(index);
            roleDao.saveAndFlush(roleEntity);
        }

    }

    @Transactional
    public void addUserByEmp(String id, String empId) {
        RoleEntity roleEntity = get(id);

        MObjectEntity empObject = metaDataQueryService.findEmployeeMObject();
        Map<String, Object> empMap = metaDataService.queryById(empObject.getId(), empId);
        UserRuleEntity userRuleEntity = userRuleService.get();
        String loginName = userRuleEntity.getLoginName();
        MirrorPropertyEntity mirrorPropertyEntity = metaDataQueryService.findMPropertyByBelongMObjectAndName(empObject, loginName);
        if (mirrorPropertyEntity != null && empMap.get(mirrorPropertyEntity.getPropertyPath()) != null) {
            loginName = String.valueOf(empMap.get(mirrorPropertyEntity.getPropertyPath()));
            UserEntity userEntity = userService.getUser(loginName);
            if (userEntity == null) {
                String password = MD5.GetMD5Code(userRuleEntity.getDefaultPassword());
                userEntity = new UserEntity();
                userEntity.setLoginName(loginName);
                userEntity.setName((String) empMap.get(MetaDataUtils.META_FIELD_NAME));
                userEntity.setPassword(password);
                userService.updateUser(userEntity);
            }
            if (!roleEntity.getMembers().contains(userEntity)) {
                roleEntity.getMembers().add(userEntity);
                roleDao.saveAndFlush(roleEntity);
            }
        }
    }

    @Transactional(readOnly = true)
    public List<UserEntity> findUsersById(String id) {
        RoleEntity roleEntity = roleDao.findOne(id);

        List<UserEntity> userEntities = roleEntity.getMembers();
        List<UserEntity> list = new ArrayList<>();
        list.addAll(userEntities);
        return list;
    }

    public Long findCount(PageTableVO pageTableVO) {
        QRoleEntity entity = QRoleEntity.roleEntity;

        JSONObject paramObject = (JSONObject) pageTableVO.getExtendData();
        RoleEntity roleEntity = JSONObject.toJavaObject(paramObject, RoleEntity.class);

        BooleanExpression ex = entity.name.isNotNull();
        if (StringUtils.isNotBlank(roleEntity.getName())) {
            ex = ex.and(entity.name.like("%" + roleEntity.getName() + "%"));
        }

        JPAQuery query = new JPAQuery(entityManager);
        Long totalNum = query.from(entity).where(
                ex
        ).count();

        return totalNum;
    }

    public PageTableVO findPageList(PageTableVO pageTableVO) {
        QRoleEntity entity = QRoleEntity.roleEntity;
        Integer limit = pageTableVO.getPageSize();
        Integer offset = pageTableVO.getOffset();
        if (limit == null || limit <= 0) {
            limit = 15;
        }
        JSONObject paramObject = (JSONObject) pageTableVO.getExtendData();
        RoleEntity roleEntity = JSONObject.toJavaObject(paramObject, RoleEntity.class);

        BooleanExpression ex = entity.name.isNotNull();
        if (StringUtils.isNotBlank(roleEntity.getName())) {
            ex = ex.and(entity.name.like("%" + roleEntity.getName() + "%"));
        }

        JPAQuery query = new JPAQuery(entityManager);
        List<RoleEntity> list = query.from(entity)
                .where(
                        ex
                ).offset(offset).limit(limit)
                .list(entity);
        Long totalCount = findCount(pageTableVO);
        PageTableVO vo = new PageTableVO();
        vo.setRowData(list);
        vo.setTotalCount(totalCount);
        vo.setPageNo(offset);
        vo.setPageSize(limit);
        return vo;

    }

    public List<RoleEntity> findList(UserEntity userEntity) {
        QRoleEntity entity = QRoleEntity.roleEntity;

        BooleanExpression ex = entity.members.any().id.eq(userEntity.getId());

        JPAQuery query = new JPAQuery(entityManager);
        List<RoleEntity> list = query.from(entity)
                .where(
                        ex
                )
                .list(entity);
        return list;

    }

    @Transactional
    public void addMenu(RoleEntity roleEntity, MenuTypeEnum type) {

        if (roleEntity.getUrlResources() != null) {
            RoleEntity dbRole = roleDao.findOne(roleEntity.getId());
            List<UrlResourcesEntity> urlList = new ArrayList<>();
            for (UrlResourcesEntity urlResourcesEntity : dbRole.getUrlResources()) {
                if (urlResourcesEntity.getMenu().getType() != type) {
                    urlList.add(urlResourcesEntity);
                } else {
                    entityManager.remove(urlResourcesEntity);
                }
            }
            dbRole.setUrlResources(new ArrayList<UrlResourcesEntity>());

            urlList.addAll(roleEntity.getUrlResources());
            for (UrlResourcesEntity urlResourcesEntity : roleEntity.getUrlResources()) {
                urlResourcesEntity.setBelongRole(dbRole);
            }
            dbRole.setUrlResources(urlList);
            roleDao.saveAndFlush(dbRole);
        }
    }

    @Transactional
    public void addPermission(RoleEntity roleEntity) {
        if (roleEntity.getPermissionList() != null) {
            MirrorPropertyEntity propertyEntity = mirrorPropertyDao.findOne(roleEntity.getPermissionList().get(0).getProperty().getId());
            MObjectEntity objectEntity = propertyEntity.getBelongMObject();
            RoleEntity dbRole = roleDao.findOne(roleEntity.getId());
            for (PermissionEntity permissionEntity : dbRole.getPermissionList()) {
                if (StringUtils.equals(permissionEntity.getBelongMObject().getId(), objectEntity.getId())) {
                    entityManager.remove(permissionEntity);
                }
            }

            for (PermissionEntity permissionEntity : roleEntity.getPermissionList()) {
                permissionEntity.setBelongMObject(objectEntity);
                permissionEntity.setBelongRole(dbRole);
                permissionDao.save(permissionEntity);

                dbRole.getPermissionList().add(permissionEntity);
            }

            roleDao.saveAndFlush(dbRole);
        }
    }

    @Transactional
    public void addDataScope(RoleDataScopeEntity dataScopeEntity, String roleId) {
        RoleEntity roleEntity = roleDao.findOne(roleId);

        MExpressGroupEntity expressGroupEntity = metaDataExpressService.initExpressBeforeSave(dataScopeEntity.getDataScope());
        dataScopeEntity.setDataScope(expressGroupEntity);
        dataScopeEntity.setBelongRole(roleEntity);
        if(dataScopeEntity.getId()==null){
            roleDataScopeDao.saveAndFlush(dataScopeEntity);
            roleEntity.getDataScopeList().add(dataScopeEntity);
            roleDao.saveAndFlush(roleEntity);
        }else {
            roleDataScopeDao.saveAndFlush(dataScopeEntity);
        }
    }

    @Transactional(readOnly = true)
    public List<PermissionEntity> findALLPermission(String roleId, String objId) {
        QPermissionEntity entity = QPermissionEntity.permissionEntity;

        BooleanExpression ex = entity.belongRole.id.eq(roleId)
                .and(entity.property.belongMObject.id.eq(objId));

        JPAQuery query = new JPAQuery(entityManager);
        List<PermissionEntity> list = query.from(entity)
                .where(
                        ex
                )
                .list(entity);
        return list;
    }

    @Transactional(readOnly = true)
    public List<PermissionEntity> findPermissionList(String roleId){
        RoleEntity roleEntity = roleDao.findOne(roleId);

        Hibernate.initialize(roleEntity.getPermissionList());

        return roleEntity.getPermissionList();
    }

    @Transactional(readOnly = true)
    public RoleDataScopeEntity findDataScope(String roleId, String objId) {
        QRoleDataScopeEntity entity = QRoleDataScopeEntity.roleDataScopeEntity;

        BooleanExpression ex = entity.belongRole.id.eq(roleId)
                .and(entity.belongMObject.id.eq(objId));

        JPAQuery query = new JPAQuery(entityManager);
        List<RoleDataScopeEntity> list = query.from(entity)
                .where(
                        ex
                )
                .list(entity);

        if (list.size() > 0) {
            RoleDataScopeEntity dataScopeEntity = list.get(0);
            LazyLoadUtil.fullLoad(dataScopeEntity.getDataScope());
            return dataScopeEntity;
        }

        return null;
    }

    @Transactional(readOnly = true)
    public List<MObjectEntity> findPermissionObjects(String roleId) {
        RoleEntity roleEntity = roleDao.findOne(roleId);
        List<PermissionEntity> list = roleEntity.getPermissionList();
        Map<String, Object> map = new HashMap<>();
        List<MObjectEntity> listObj = new ArrayList<>();
        for (PermissionEntity permissionEntity : list) {
            MObjectEntity mObjectEntity = permissionEntity.getProperty().getBelongMObject();
            if (map.get(mObjectEntity.getId()) == null) {
                listObj.add(mObjectEntity);
                map.put(mObjectEntity.getId(), "");
            }
        }
        return listObj;
    }

    @Transactional(readOnly = true)
    public List<MObjectEntity> findDataScopeObjects(String roleId) {
        RoleEntity roleEntity = roleDao.findOne(roleId);
        List<RoleDataScopeEntity> list = roleEntity.getDataScopeList();
        Map<String, Object> map = new HashMap<>();
        List<MObjectEntity> listObj = new ArrayList<>();
        for (RoleDataScopeEntity dataScopeEntity : list) {
            MObjectEntity mObjectEntity = dataScopeEntity.getBelongMObject();
            if (map.get(mObjectEntity.getId()) == null) {
                listObj.add(mObjectEntity);
                map.put(mObjectEntity.getId(), "");
            }
        }
        return listObj;
    }

    @Transactional(readOnly = true)
    public List<PermissionEntity> findObjectPermission(String roleId, String objId) {
        RoleEntity roleEntity = roleDao.findOne(roleId);
        List<PermissionEntity> list = findALLPermission(roleId, objId);
        Map<String, Object> map = new HashMap<>();
        List<PermissionEntity> listRt = new ArrayList<>();
        for (PermissionEntity permissionEntity : list) {
            MirrorPropertyEntity propertyEntity = permissionEntity.getProperty();
            if (map.get(propertyEntity.getId()) == null) {
                map.put(propertyEntity.getId(), "");
                listRt.add(permissionEntity);
            }
        }

        MObjectEntity objectEntity = metaDataQueryService.findMObjectById(objId);
        List<MirrorPropertyEntity> propertyEntities = metaDataQueryService.findMPropertyByBelongMObject(objectEntity);
        for (MirrorPropertyEntity property : propertyEntities) {
            if (map.get(property.getId()) == null
                    && !MetaDataUtils.isSystemFields(property.getFieldName())
                    && property.getInVisibleType() != MInVisibleTypeEnum.invisibleAll) {
                //增加默认无权的权限
                PermissionEntity permissionEntity = new PermissionEntity();
                permissionEntity.setBelongRole(roleEntity);
                permissionEntity.setProperty(property);
                permissionEntity.setAction(PermissionActionEnum.action_none);

                listRt.add(permissionEntity);
                map.put(property.getId(), "");
            }
        }

        return listRt;
    }

    @Transactional(readOnly = true)
    public List<String> getUrlPermission(List<String> roleIdList) {

        QUrlResourcesEntity entity = QUrlResourcesEntity.urlResourcesEntity;

        BooleanExpression ex = entity.belongRole.id.in(roleIdList);

        JPAQuery query = new JPAQuery(entityManager);
        List<UrlResourcesEntity> list = query.from(entity)
                .where(
                        ex
                )
                .list(entity);

        List<String> listRt = new ArrayList<>();
        for (UrlResourcesEntity urlResourcesEntity:list){
            listRt.add(urlResourcesEntity.getMenu().getPath());
        }
        return listRt;
    }
}
