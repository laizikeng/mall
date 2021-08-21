package com.scut.mall.auth.vo;

import lombok.Data;

@Data
public class SocialUser {

    private String accessToken;
    private String tokenType;
    private long expiresIn;
    private String refreshToken;
    private String scope;
    private long createdAt;
}