package com.mg.common.user.service;

import com.mg.common.entity.UserEntity;
import com.mg.framework.entity.metadata.MExpressGroupEntity;
import com.mg.framework.entity.metadata.MObjectEntity;
import com.mg.framework.entity.vo.PageTableVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * Created by huan on 15/7/22.
 */
public interface UserService {


    /**
     * 根据用户名和密码,获取用户信息 by huan
     * @param loginName
     *        用户名
     * @param password
     *        密码
     * @return
     *        返回验证通过后返回User对象，若无返回null
     */
    UserEntity getUser(String loginName, String password);
    /**
     * 保持用户信息，名称在系统中不存在，就初始化数据； 或者相反 by huan
     * @param userNames
     *        员工名称集合
     */
    List<UserEntity> insertUsers(List<String> userNames);


    /**
     * 插入用户信息
     * @param userName
     *        单个用户名称
     * @return
     *        插入user实体类
     */
    UserEntity insertUser(String userName, String password);


    /**
     * 根据用户名,获取用户信息 by huan
     * @param loginName
     * @return
     */
    public UserEntity getUser(String loginName);

    /**
     * 根据用户主键获取用户对象 by huan
     * @param id
     *        用户ID
     * @return
     *        用户信息 ID＝＝NULL 返回NULL
     */
    UserEntity getUserById(String id);

    /**
     * 获取某一用户特殊权限上的别名， 若不存在该权限， 则返回用户名称 by huan
     * @param userId
     *        用户ID
     * @param flag
     *        权限标示
     * @return
     *        用户特殊权限所对应别名
     */
    String getUserMarkName(String userId, int flag);

    /**
     * 获取业务数据模版  by huan
     * @return
     *        业务数据模版
     */
    public List<Map<String, Object>> getBussinessTemplate();
    /**导入业务数据 －－－版本－－2*/
    void insertBussinessVariables_(List<Map<String,Object>> datas);

    /**
     * 修改员工信息， 比如密码 by huan
     * @param user
     *        需要修改的员工对象
     */
    void updateUser(UserEntity user);

    /**
     * 模糊检索人员列表by huan
     * @param name
     *        名称，为空是所有人员
     * @return
     *        返回符合条件的人员信息
     */
    List<UserEntity> getUsers(String name);

    /**
     * 删除用户
     * @param userId
     */
    public void delete(String userId);
    /**
     * 分页查询用户登录帐号
     * @param pageTableVO
     * @return
     */
    public PageTableVO findPageList(PageTableVO pageTableVO);

    /**
     * 根据用户id,初始化登录密码
     * @param userId
     * @return
     */
    public UserEntity saveInitUserPassWord(String userId);

    /**
     * 根据条件组，生成登录帐号
     * @param metaObject
     * @param expressGroupEntity
     * @return 生成的账号数目
     */
    Integer createUser(MObjectEntity metaObject, MExpressGroupEntity expressGroupEntity);

    /**
     * 查询用户名称在集合userNames中的所有用户集
     * @param userNames
     *        用户名称组，
     * @return
     *        满足name in userNames && 状态标示位为有效的用户
     */
    List<UserEntity> getUsersByNames(List<String> userNames);

    /**
     * 查询用户名称在集合userNames中的所有用户集
     * @param userIds
     *        用户名称组，
     * @return
     *        满足name in userNames && 状态标示位为有效的用户
     */
    public List<UserEntity> getUsersByIds(List<String> userIds);

    /**
     * 根据登录用户，查询所对应的员工ID
     * @param userEntity
     * @return
     */
    public String getEmployeeIdByUser(UserEntity userEntity);


    /**
     * 根据empId，查询所对应的user
     * @param empId
     * @return
     */
    public UserEntity getUserByEmpId(String empId);

    UserEntity getUserByRequest(HttpServletRequest request);
}
