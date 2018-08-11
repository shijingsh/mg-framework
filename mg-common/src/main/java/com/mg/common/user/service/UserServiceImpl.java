package com.mg.common.user.service;

import com.alibaba.fastjson.JSONObject;
import com.mg.common.entity.QUserEntity;
import com.mg.common.entity.UserEntity;
import com.mg.common.entity.UserRuleEntity;
import com.mg.common.metadata.service.MetaDataExpressService;
import com.mg.common.metadata.service.MetaDataService;
import com.mg.common.user.vo.ThirdUserVo;
import com.mg.common.utils.MD5;
import com.mg.framework.entity.metadata.MExpressGroupEntity;
import com.mg.framework.entity.metadata.MExpressionEntity;
import com.mg.framework.entity.metadata.MirrorPropertyEntity;
import com.mg.framework.utils.StatusEnum;
import com.mg.framework.utils.UserHolder;
import com.mysema.query.jpa.impl.JPAQuery;
import com.mysema.query.types.expr.BooleanExpression;
import com.mg.common.metadata.service.CustomFormService;
import com.mg.common.metadata.service.MetaDataQueryService;
import com.mg.common.metadata.util.MetaDataUtils;
import com.mg.common.user.dao.UserDao;
import com.mg.framework.entity.metadata.MObjectEntity;
import com.mg.framework.entity.vo.PageTableVO;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {


    public Logger logger = LoggerFactory.getLogger(this.getClass());

    @PersistenceContext
    private EntityManager entityManager;
    @Autowired
    private UserDao userDao;
    @Autowired
    private UserRuleService userRuleService;
    @Autowired
    MetaDataService metaDataService;
    @Autowired
    MetaDataQueryService metaDataQueryService;
    @Autowired
    CustomFormService customFormService;
    @Autowired
    MetaDataExpressService metaDataExpressionService;
    /**
     * 根据用户名,获取用户信息
     * @param loginName
     * @return
     */
    public UserEntity getUser(String loginName) {

        List<String> names = new ArrayList<>();
        names.add(loginName);
        List<UserEntity> userEntityList= getUsersByNames(names);
        if(userEntityList!=null && userEntityList.size()>0){
            return userEntityList.get(0);
        }
        return null;
    }


    /**
     * 根据用户名和密码,获取用户信息
     * @param loginName
     *        用户名
     * @param password
     *        密码
     * @return
     *        返回验证通过后返回User对象，若无返回null
     */
    public UserEntity getUser(String loginName, String password) {

        JPAQuery query = getQuery();
        QUserEntity qUserEntity = QUserEntity.userEntity;
        query.from(qUserEntity);

        query.where(
                qUserEntity.loginName.eq(loginName),
                qUserEntity.password.eq(password)
        );
        List<UserEntity> users = query.list(qUserEntity);

        if (users == null || users.isEmpty()) {
            return null;
        }
        return users.get(0);
    }

    /**
     * 根据用户id,初始化登录密码
     * @param userId
     * @return
     */
    @Transactional
    public UserEntity saveInitUserPassWord(String userId) {

        UserEntity userEntity = userDao.findOne(userId);
        userRuleService.initUser(userEntity, true);
        userDao.save(userEntity);
        return userEntity;
    }

    @Transactional
    public void delete(String userId) {

        UserEntity userEntity = userDao.findOne(userId);
        userEntity.getRoles().clear();
        userDao.delete(userEntity);
    }
    public Long findCount(PageTableVO pageTableVO) {
        QUserEntity entity = QUserEntity.userEntity;

        JSONObject paramObject = (JSONObject)pageTableVO.getExtendData();
        UserEntity userEntity = JSONObject.toJavaObject(paramObject, UserEntity.class);

        BooleanExpression ex = entity.status.eq(StatusEnum.STATUS_VALID);
        if(StringUtils.isNotBlank(userEntity.getLoginName())){
            ex = ex.and(entity.loginName.like("%" + userEntity.getLoginName() + "%"));
        }else if(StringUtils.isNotBlank(userEntity.getName())){
            ex = ex.and(entity.name.like("%"+userEntity.getName()+"%"));
        }

        JPAQuery query = new JPAQuery(entityManager);
        Long totalNum = query.from(entity).where(
                ex
        ).count();

        return totalNum;
    }

    public PageTableVO findPageList(PageTableVO pageTableVO) {
        QUserEntity entity = QUserEntity.userEntity;
        Integer limit = pageTableVO.getPageSize();
        Integer offset = pageTableVO.getOffset();
        if(limit==null || limit <=0){
            limit = 15;
        }
        JSONObject paramObject = (JSONObject)pageTableVO.getExtendData();
        UserEntity userEntity = JSONObject.toJavaObject(paramObject,UserEntity.class);

        BooleanExpression ex = entity.status.eq(StatusEnum.STATUS_VALID);
        if(StringUtils.isNotBlank(userEntity.getLoginName())){
            ex = ex.and(entity.loginName.like("%" + userEntity.getLoginName() + "%"));
        }else if(StringUtils.isNotBlank(userEntity.getName())){
            ex = ex.and(entity.name.like("%"+userEntity.getName()+"%"));
        }

        JPAQuery query = new JPAQuery(entityManager);
        List<UserEntity> list = query.from(entity)
                .where(
                        ex
                ).offset(offset).limit(limit)
                .list(entity);
        Long totalCount = findCount(pageTableVO);
        PageTableVO vo = new PageTableVO();
        vo.setRowData(list);
        vo.setTotalCount(totalCount);
        vo.setPageNo(pageTableVO.getPageNo());
        vo.setPageSize(pageTableVO.getPageSize());
        return vo;
    }
    /**
     * 插入用户信息
     * @param userNames
     *        用户名称组，每n个一次性提交
     * @return
     *        n条数据倒入成功
     */
    @Transactional
    public List<UserEntity> insertUsers(List<String> userNames) {

        List<UserEntity> users = new ArrayList<>();
        for (String userName : userNames) {

            UserEntity user = getUser(userName);

            if (user != null) { //已经在系统中
                users.add(user);
                continue;
            }
            users.add(insertUser(userName, null));
        }
        return users;
    }


    public List<UserEntity> getUsers(List<String> userNames) {
        QUserEntity user = QUserEntity.userEntity;


        JPAQuery query = new JPAQuery(entityManager);
        return query.from(user)
                .where(
                        user.name.in(userNames)
                ).list(user);

    }

    /**
     * 根据用户主键获取用户对象 by huan
     * @param id
     *        用户ID
     * @return
     *        用户信息 ID＝＝NULL 返回NULL
     */
    public UserEntity getUserById(String id) {
        if (StringUtils.isBlank(id)) {
            return null;
        }
        return userDao.findOne(id);
     }

    /**
     * 插入用户信息
     * @param userName
     *        单个用户名称
     * @return
     *        插入user实体类
     */
    @Transactional
    public UserEntity insertUser(String userName, String password) {

        UserEntity user = getUser(userName);

        if (user != null) {
            return user;
        }

        UserEntity userEntity = new UserEntity(userName, StringUtils.isBlank(password) ? UserEntity.DEFAULT_PASSWORD : password);

        entityManager.persist(userEntity);

        return userEntity;
    }

    /**
     * 获取某一用户特殊权限上的别名， 若不存在该权限， 则返回用户名称 by huan
     * @param userId
     *        用户ID
     * @param flag
     *        权限标示
     * @return
     *        用户特殊权限所对应别名
     */
    public String getUserMarkName(String userId, int flag) {
        if(StringUtils.isBlank(userId)){
            return "";
        }
        String[] userIds = userId.split(";");
        List<UserEntity> list = getUsersByIds(Arrays.asList(userIds));
        StringBuilder sb = new StringBuilder();
        for (int i=0;i<list.size();i++){
            UserEntity user = list.get(i);
            sb.append(user.getName());
            if(i!=list.size()-1){
                sb.append(";");
            }
        }
        return sb.toString();
    }

    @Override
    public List<Map<String, Object>> getBussinessTemplate() {
        return null;
    }

    @Override
    public void insertBussinessVariables_(List<Map<String, Object>> datas) {

    }

    /**
     * 修改员工信息
     * @param user
     */
    @Transactional
    public void updateUser(UserEntity user) {
        userDao.save(user);
    }

    /**
     * 模糊检索人员列表by huan
     * @param name
     *        名称，为空是所有人员
     * @return
     *        返回符合条件的人员信息
     */
    public List<UserEntity> getUsers(String name) {
        QUserEntity qUserEntity = QUserEntity.userEntity;
        JPAQuery query = getQuery();

        query.from(qUserEntity);
        if(StringUtils.isNotBlank(name)){
            query.where(
                    qUserEntity.name.like("%" + name + "%")
            );
        }
        return query.list(qUserEntity);
    }


    /**
     * 查询用户名称在集合userNames中的所有用户集
     * @param userNames
     *        用户名称组，
     * @return
     *        满足name in userNames && 状态标示位为有效的用户
     */
    public List<UserEntity> getUsersByNames(List<String> userNames) {
        QUserEntity qUserEntity = QUserEntity.userEntity;
        JPAQuery query = getQuery();

        query.from(qUserEntity);
        query.where(
                BooleanExpression.allOf(
                        qUserEntity.loginName.in(userNames),
                        qUserEntity.status.eq(StatusEnum.STATUS_VALID)
                )
        );
        return query.list(qUserEntity);
    }

    /**
     * 查询用户名称在集合userNames中的所有用户集
     * @param userIds
     *        用户名称组，
     * @return
     *        满足name in userNames && 状态标示位为有效的用户
     */
    public List<UserEntity> getUsersByIds(List<String> userIds) {
        QUserEntity qUserEntity = QUserEntity.userEntity;
        JPAQuery query = getQuery();

        query.from(qUserEntity);
        query.where(
                BooleanExpression.allOf(
                        qUserEntity.id.in(userIds)
                )
        );
        return query.list(qUserEntity);
    }

    protected JPAQuery getQuery() {
        return new JPAQuery(entityManager);
    }

    /**
     * 根据条件组，生成登录帐号
     * @param metaObject
     * @param expressGroupEntity
     */
    @Transactional
    public Integer createUser(MObjectEntity metaObject, MExpressGroupEntity expressGroupEntity) {
        List<MirrorPropertyEntity> mPropertyEntityList  = metaDataQueryService.findMPropertyNormalByBelongMObject(metaObject);
        MirrorPropertyEntity primaryKey = metaDataQueryService.findPrimaryKeyMPropertyByBelongMObject(metaObject);
        mPropertyEntityList.add(primaryKey);
                //设置排序字段
        customFormService.initSort(metaObject,expressGroupEntity);
        //数据列表
        expressGroupEntity.setPageSize(-1);
        List<Map<String,Object>> list = metaDataService.queryByMetaData(metaObject, mPropertyEntityList, expressGroupEntity);

        UserRuleEntity userRuleEntity = userRuleService.get();
        String loginNameRule = userRuleEntity.getLoginName();
        MirrorPropertyEntity loginNameProperty = metaDataQueryService.findMPropertyByBelongMObjectAndName(metaObject, loginNameRule);
        MirrorPropertyEntity nameProperty = metaDataQueryService.findMPropertyByBelongMObjectAndFieldName(metaObject,metaObject, MetaDataUtils.META_FIELD_NAME);
        String password = MD5.GetMD5Code(userRuleEntity.getDefaultPassword());
        if(loginNameProperty==null){
            return 0;
        }
        Integer count = 0;
        for(Map<String,Object> empMap:list){
            String loginName = (String)empMap.get(loginNameProperty.getPropertyPath());
            String name = (String)empMap.get(nameProperty.getPropertyPath());
            String id = (String)empMap.get(MetaDataUtils.META_FIELD_ID);
            if(StringUtils.isNotBlank(loginName)){
                UserEntity userEntity = getUser(loginName);
                if(userEntity==null){
                    //用户不存在的时候，才增加账号
                    userEntity = new UserEntity();
                    userEntity.setLoginName(loginName);
                    userEntity.setName(name);
                    userEntity.setEmployeeId(id);
                    userEntity.setPassword(password);

                    userDao.saveAndFlush(userEntity);

                    count++;
                }else{
                    userEntity.setEmployeeId(id);
                    userDao.saveAndFlush(userEntity);
                }
            }
        }

        return count;
    }

    /**
     * 根据登录用户，查询所对应的员工ID
     * @param userEntity
     * @return
     */
    public String getEmployeeIdByUser(UserEntity userEntity){
        String loginName = userEntity.getLoginName();
        UserRuleEntity userRuleEntity = userRuleService.get();
        MObjectEntity metaObject = metaDataQueryService.findEmployeeMObject();
        MirrorPropertyEntity loginNameProperty = metaDataQueryService.findMPropertyByBelongMObjectAndName(metaObject, userRuleEntity.getLoginName());
        MExpressionEntity expression =  metaDataExpressionService.createSimpleEqExpress(loginNameProperty, loginName);
        MExpressGroupEntity expressGroup = new MExpressGroupEntity(expression);
        List<Map<String, Object>> list = metaDataService.queryByMetaData(metaObject, expressGroup);
        if(list.size()>0){
            return String.valueOf(list.get(0).get(MetaDataUtils.META_FIELD_ID));
        }
        return null;
    }

    /**
     * 根据empId，查询所对应的user
     * @param empId
     * @return
     */
    public UserEntity getUserByEmpId(String empId){
        MObjectEntity empObject = metaDataQueryService.findEmployeeMObject();
        Map<String,Object> empMap = metaDataService.queryById(empObject.getId(), empId);
        UserRuleEntity userRuleEntity = userRuleService.get();
        String loginName = userRuleEntity.getLoginName();
        MirrorPropertyEntity mirrorPropertyEntity = metaDataQueryService.findMPropertyByBelongMObjectAndName(empObject, loginName);
        if(mirrorPropertyEntity!=null && empMap.get(mirrorPropertyEntity.getPropertyPath())!=null){
            loginName = String.valueOf(empMap.get(mirrorPropertyEntity.getPropertyPath()));
            UserEntity userEntity = getUser(loginName);

            return userEntity;
        }

        return null;
    }

    /**
     * 从request中获取当前用户
     * @param request
     * @return
     */
    public UserEntity getUserByRequest(HttpServletRequest request) {

        UserEntity userEntity = UserHolder.getLoginUser();
        if(userEntity != null){
            return userEntity;
        }
        String userId = request.getParameter("userId");
        if(StringUtils.isNotBlank(userId)){
            UserEntity tempUser = getUserById(userId);
            if(tempUser != null){
                return tempUser;
            }
        }
        return null;
    }

    @Transactional
    public UserEntity saveOrGetThirdUser(ThirdUserVo thirdUserVo) {
       UserEntity userEntity =  userDao.findByName(thirdUserVo.getUserId());
       if(userEntity == null){
            //没有这创建帐户
           userEntity = new UserEntity();
           userEntity.setLoginName(thirdUserVo.getUserId());
           userEntity.setName(thirdUserVo.getUserName());
           userEntity.setHeadPortrait(thirdUserVo.getUserAvatar());
           userEntity.setPassword(UserEntity.DEFAULT_PASSWORD);
           userEntity.setAccessToken(thirdUserVo.getAccessToken());

           userDao.save(userEntity);
       }

        return userEntity;
    }
}
