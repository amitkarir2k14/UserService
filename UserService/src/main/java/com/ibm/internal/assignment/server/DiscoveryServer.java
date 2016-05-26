package com.ibm.internal.assignment.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableEurekaServer
@ComponentScan("com.ibm.internal.assignment")
public class DiscoveryServer {

	public static void main(String[] args) {
		System.setProperty("spring.config.name", "userServiceregistration");
		SpringApplication.run(DiscoveryServer.class, args);

	}
}
