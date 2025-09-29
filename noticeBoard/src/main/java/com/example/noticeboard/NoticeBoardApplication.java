package com.example.noticeboard;

import io.github.cdimascio.dotenv.Dotenv;
import io.github.cdimascio.dotenv.DotenvException;
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
        // .env 파일 로드
        loadEnvironmentVariables();

        try {
            log.info("********************************************************");
            String localIp = InetAddress.getLocalHost().getHostAddress();
            log.info("localIp=" + localIp);
            System.setProperty("local.ip", localIp); // local.ip 설정

            log.info("********************************************************");
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        SpringApplication.run(NoticeBoardApplication.class, args);
    }

    /**
     * 프로젝트 루트의 .env 파일을 읽어 필요한 변수를 시스템 속성으로 설정
     */
    private static void loadEnvironmentVariables() {
        try {
            // 프로젝트 루트에서 .env 파일을 찾아 로드합니다.
            Dotenv dotenv = Dotenv.load();

            // 2. 핵심 변수들을 System Property로 설정
            // System Property는 Spring Configuration보다 높은 우선순위를 가집니다.

            // Redis 변수 설정
            setSystemPropertyIfPresent(dotenv, "REDIS_PASSWORD");

            // 데이터베이스 변수 설정
            setSystemPropertyIfPresent(dotenv, "DB_PASSWORD");

            // AWS 변수 설정
            setSystemPropertyIfPresent(dotenv, "AWS_ACCESS_KEY");
            setSystemPropertyIfPresent(dotenv, "AWS_SECRET_KEY");

            // 메일 변수 설정
            setSystemPropertyIfPresent(dotenv, "MAIL_USERNAME");
            setSystemPropertyIfPresent(dotenv, "MAIL_PASSWORD");

            // OAuth2 변수 설정
            setSystemPropertyIfPresent(dotenv, "NAVER_CLIENT_ID");
            setSystemPropertyIfPresent(dotenv, "NAVER_CLIENT_SECRET");
            setSystemPropertyIfPresent(dotenv, "GOOGLE_CLIENT_ID");
            setSystemPropertyIfPresent(dotenv, "GOOGLE_CLIENT_SECRET");

            log.info("Loaded necessary variables from .env file into System Properties.");

        } catch (DotenvException e) {
            log.warn("Could not find or load .env file. Falling back to default configuration.");
            // .env 파일이 없는 경우 (예: 서버 배포 환경) 무시하고 진행
        }
    }

    /**
     * Dotenv에서 값을 가져와 System Property로 설정하는 헬퍼 메서드
     */
    private static void setSystemPropertyIfPresent(Dotenv dotenv, String key) {
        String value = dotenv.get(key);
        if (value != null) {
            System.setProperty(key, value);
        }
    }
}