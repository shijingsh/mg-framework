package com.mg.common.user.vo;

import javax.persistence.Transient;

/**
 * 第三方登录用户
 * Created by liukefu on 2018/8/11.
 */
public class ThirdUserVo implements java.io.Serializable{
    private String userId;
    private String userName;
    private String accessToken;
    private String userAvatar;
    private String userGender;

    /**
     * 多实例用户token
     */
    private String userToken;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getUserAvatar() {
        return userAvatar;
    }

    public void setUserAvatar(String userAvatar) {
        this.userAvatar = userAvatar;
    }

    public String getUserGender() {
        return userGender;
    }

    public void setUserGender(String userGender) {
        this.userGender = userGender;
    }

    public String getUserToken() {
        return userToken;
    }

    public void setUserToken(String userToken) {
        this.userToken = userToken;
    }
}
