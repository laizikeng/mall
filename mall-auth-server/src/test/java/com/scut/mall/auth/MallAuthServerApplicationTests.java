package com.scut.mall.auth;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;


class MallAuthServerApplicationTests {

    @Test
    void contextLoads() {
        String s="{\"msg\":\"成功\",\"code\":\"0\"}";
        String[] strs = s.split(":");
        System.out.println(strs[strs.length-1].replaceAll("}",""));
        for(String temp:strs) {
            System.out.println(temp);
        }
    }

}
