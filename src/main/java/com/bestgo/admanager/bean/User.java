package com.bestgo.admanager.bean;

import java.io.Serializable;

/**
 * @Author: mengjun
 * @Date: 2018/5/14 15:07
 * @Desc: 用户，对应web_ad_login_user表
 */
public class User implements Serializable {
    private Integer id;
    private String username;
    private String password;
    private String nickname;
    private int userType;
    private int userLevel;

    public User() {
    }

    public User(Integer id, String username, String password, String nickname, int userType, int userLevel) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.nickname = nickname;
        this.userType = userType;
        this.userLevel = userLevel;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public int getUserType() {
        return userType;
    }

    public void setUserType(int userType) {
        this.userType = userType;
    }

    public int getUserLevel() {
        return userLevel;
    }

    public void setUserLevel(int userLevel) {
        this.userLevel = userLevel;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", nickname='" + nickname + '\'' +
                ", userType=" + userType +
                ", userLevel=" + userLevel +
                '}';
    }
}
