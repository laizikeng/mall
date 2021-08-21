package com.scut.mall.member.vo;

import lombok.Data;

@Data
public class SocialUser {

    private String accessToken;
    private String tokenType;
    private String expiresIn;
    private String refreshToken;
    private String scope;
    private Long createdAt;
}