package com.s3.demo;

import com.s3.demo.config.AWSConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
public class AWSConfigTest {
    @Autowired
    AWSConfig awsConfig;

    @Test
    public void contextLoads(){

        System.out.println(awsConfig);

    }

}
