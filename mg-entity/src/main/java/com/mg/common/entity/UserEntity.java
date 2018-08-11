package com.mg.common.entity;

import com.mg.framework.utils.StatusEnum;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

@Entity
@Table(name="sys_user")
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class UserEntity extends ExpandEntity {

    public static final String ADMIN_USER_LOGNAME = "admin";
    /**默认密码*/
    public static String DEFAULT_PASSWORD = "96e79218965eb72c92a549dd5a330112";
    /**登录名 */
    private String loginName;
    /**姓名 */
    private String name;
    /**手机号 */
    private String mobile;
    /**密码 */
    private String password;
    /**邮箱 */
    private String email;
    /**QQ */
    private String qq;
    /**微信 */
    private String weixin;
    /**支付宝 */
    private String alipay;
    /**微博 */
    private String weibo;
    /**
     * 状态
     */
    private int status = StatusEnum.STATUS_VALID;
    /**
     * 最后登录时间
     */
    protected Date lastLoginDate;
    /**
     * 头像
     */
    @Lob
    @Basic(fetch = FetchType.EAGER)
    private String headPortrait;
    /**
     * 所属员工
     */
    private String employeeId;

    /**
     * 纬度
     */
    @Column(name = "latitude",precision = 20,scale = 8)
    private BigDecimal latitude;
    /**
     * 经度
     */
    @Column(name = "longitude",precision = 20,scale = 8)
    private BigDecimal longitude;
    /**
     * 员工拥有的角色
     */
    @ManyToMany(mappedBy="members",fetch = FetchType.LAZY)
    private List<RoleEntity> roles = new LinkedList<RoleEntity>();

    /**
     * 第三方登录token
     */
    private String accessToken;
    /**
     * 用户的公司实例标识
     */
    @Transient
    private String userToken;

    @Transient
    private String companyName;

    public UserEntity(){}

    public UserEntity(String _name, String _password) {
        this.name = _name;
        this.password = _password;
    }

    public UserEntity(String _name) {
        this.name = _name;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public List<RoleEntity> getRoles() {
        return roles;
    }

    public void setRoles(List<RoleEntity> roles) {
        this.roles = roles;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getQq() {
        return qq;
    }

    public void setQq(String qq) {
        this.qq = qq;
    }

    public String getWeixin() {
        return weixin;
    }

    public void setWeixin(String weixin) {
        this.weixin = weixin;
    }

    public String getAlipay() {
        return alipay;
    }

    public void setAlipay(String alipay) {
        this.alipay = alipay;
    }

    public String getWeibo() {
        return weibo;
    }

    public void setWeibo(String weibo) {
        this.weibo = weibo;
    }

    public String getUserToken() {
        return userToken;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public void setUserToken(String userToken) {
        this.userToken = userToken;
    }

    public Date getLastLoginDate() {
        return lastLoginDate;
    }

    public void setLastLoginDate(Date lastLoginDate) {
        this.lastLoginDate = lastLoginDate;
    }

    public String getHeadPortrait() {
        return headPortrait;
    }

    public void setHeadPortrait(String headPortrait) {
        this.headPortrait = headPortrait;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public  Boolean isAdmin(){
        if(StringUtils.isBlank(loginName)){
            return false;
        }
        return loginName.equals(ADMIN_USER_LOGNAME);
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public BigDecimal getLatitude() {
        return latitude;
    }

    public void setLatitude(BigDecimal latitude) {
        this.latitude = latitude;
    }

    public BigDecimal getLongitude() {
        return longitude;
    }

    public void setLongitude(BigDecimal longitude) {
        this.longitude = longitude;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}
