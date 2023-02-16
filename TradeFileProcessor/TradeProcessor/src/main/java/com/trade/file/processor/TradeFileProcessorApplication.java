package com.trade.file.processor;

import com.trade.file.process.TradeCSVProcessor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;


@SpringBootApplication
@Slf4j
@EnableCaching
@ComponentScan(basePackages = "com.trade.file")
public class TradeFileProcessorApplication
        implements CommandLineRunner {

    @Autowired
    TradeCSVProcessor tradeCSVProcessor;
    private static Logger LOG = LoggerFactory
            .getLogger(TradeFileProcessorApplication.class);

    public static void main(String[] args) {
        LOG.info("STARTING TradeFileProcessApplication....................");
        SpringApplication.run(TradeFileProcessorApplication.class, args);

    }

    @Override
    public void run(String... args) {
        try {
            log.info("EXECUTING : command line runner");
            String filePath = System.getProperty("java.io.tmpdir");
            tradeCSVProcessor.processTradeData(filePath);
        } catch (Exception exception) {
            log.error("Spring CommandLine Exception {}", exception.getMessage());
        }

    }
}