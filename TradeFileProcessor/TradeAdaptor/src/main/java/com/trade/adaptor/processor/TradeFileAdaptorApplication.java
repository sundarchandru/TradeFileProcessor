package com.trade.adaptor.processor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
@ComponentScan(basePackages="com.trade.adaptor")
public class TradeFileAdaptorApplication {

	public static void main(String[] args) {
		//SpringApplication.run(TradeFileAdaptorApplication.class, args);
		SpringApplication application = new SpringApplication(TradeFileAdaptorApplication.class);
		Map<String, Object> properties = new HashMap<>();
		properties.put("server.port", 9091);
		application.setDefaultProperties(properties);
		application.run(args);
	}

}
