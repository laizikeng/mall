package com.suct.mall.thirdparty;

import com.aliyun.oss.OSSClient;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

@RunWith(SpringRunner.class)
@SpringBootTest
class MallThirdPartyApplicationTests {
    @Autowired
    OSSClient ossClient;

    @Test
    void contextLoads() throws FileNotFoundException {
        InputStream inputStream = new FileInputStream("E:\\mall\\README.md");
        ossClient.putObject("lzk-mall","README.md",inputStream);
        ossClient.shutdown();
        System.out.println("上传成功");
    }

}
