package com.airxiechao.j20;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 测试单体程序
 */
@SpringBootApplication
public class TestAllinoneBoot implements CommandLineRunner {

    public static void main(String[] args){
        SpringApplication.run(TestAllinoneBoot.class, args);
    }

    @Override
    public void run(String... args) throws Exception {

    }
}

