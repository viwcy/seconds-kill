package com.fuqiang.secondskill;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;


@SpringBootApplication
@MapperScan(basePackages = {"com.fuqiang.*.mapper"})
@ComponentScan(basePackages = {"com.fuqiang.*"})
public class SecondsKillApplication {

    public static void main(String[] args) {
        SpringApplication.run(SecondsKillApplication.class, args);
    }

}
