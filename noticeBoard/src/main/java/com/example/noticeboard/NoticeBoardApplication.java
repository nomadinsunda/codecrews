package com.example.noticeboard;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Slf4j
@EnableJpaAuditing
@EnableScheduling
@SpringBootApplication
public class NoticeBoardApplication {
    public static void main(String[] args) {
        try {

            log.info("********************************************************");

            String localIp = InetAddress.getLocalHost().getHostAddress();
            log.info("localIp=" + localIp);
            System.setProperty("local.ip", localIp);  // local.ip 설정

            log.info("********************************************************");
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        SpringApplication.run(NoticeBoardApplication.class, args);
    }

}
