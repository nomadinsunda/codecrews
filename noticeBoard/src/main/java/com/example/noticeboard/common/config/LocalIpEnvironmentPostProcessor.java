package com.example.noticeboard.common.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Slf4j
public class LocalIpEnvironmentPostProcessor implements EnvironmentPostProcessor {

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        try {
            log.info("********************************************************");
            String localIp = InetAddress.getLocalHost().getHostAddress();
            log.info("localIp=" + localIp);
            environment.getSystemProperties().put("local.ip", localIp);
            log.info("********************************************************");
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }
}