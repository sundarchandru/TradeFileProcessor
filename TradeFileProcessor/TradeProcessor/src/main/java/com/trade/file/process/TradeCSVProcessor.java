package com.trade.file.process;

import com.opencsv.bean.CsvToBeanBuilder;
import com.trade.file.config.ApplicationPropertyLoader;
import com.trade.file.dto.Product;
import com.trade.file.dto.Trade;
import com.trade.file.dto.TradeWithProduct;
import com.trade.file.util.CsvWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.util.ResourceUtils;

import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

import java.time.temporal.ChronoField;
import java.time.format.DateTimeFormatterBuilder;

@Service
@Slf4j
@RequiredArgsConstructor
@EnableAutoConfiguration
@PropertySource("classpath:application.properties")
public class TradeCSVProcessor {


    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private ApplicationPropertyLoader applicationPropertyLoader;
    private static final String PATTERN = ".*"; // Change this to your desired file name pattern (regex)
    public static final String MISSING_PRODUCT_NAME = "Missing Product Name";
    public static final String FILE_PREFIX = "trade_inbound";
    public static final String PROCESSED_FILE = "_processed";

    private static final String DATE_FORMAT = "yyyyMMdd";
    private static final DateTimeFormatter FORMATTER = new DateTimeFormatterBuilder()
            .appendPattern(DATE_FORMAT)
            .parseDefaulting(ChronoField.CLOCK_HOUR_OF_DAY, 0)
            .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)
            .parseDefaulting(ChronoField.SECOND_OF_MINUTE, 0)
            .toFormatter()
            .withChronology(java.time.chrono.IsoChronology.INSTANCE)
            .withResolverStyle(java.time.format.ResolverStyle.STRICT);

    public List<TradeWithProduct> processTradeData(String filePath) throws IOException {
        log.info("Enter processTradeData called from controller");
        List<Trade> tradeList;
        List<TradeWithProduct> tradeProcessed = new ArrayList<>();
        File folder = new File(filePath);
        File[] files = folder.listFiles((dir, name) -> name.startsWith(FILE_PREFIX));
        if (files == null || files.length == 0) {
            System.out.println("No files found");
            return null;
        }
        for (File file : files) {
            if (Pattern.matches(PATTERN, file.getName()) &&
                    Pattern.matches(PATTERN, file.getName())) {
                try {
                    tradeList = new CsvToBeanBuilder(new FileReader(filePath + file.getName()))
                            .withType(Trade.class)
                            .withSkipLines(1)
                            .build()
                            .parse();
                    tradeProcessed = transformTradeList_latest(tradeList);
                    CsvWriter.writeDataToCsvFile(file.getName() + PROCESSED_FILE, tradeProcessed);
                    log.info("tradeProcessed with following values {}", tradeProcessed);
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

        }
        return tradeProcessed;
    }

    @Cacheable
    public List<Product> loadProductList() throws IOException {
        File file = ResourceUtils.getFile("classpath:product.csv");
        List<Product> productList;
        try {
            productList = new CsvToBeanBuilder(new FileReader(file))
                    .withType(Product.class)
                    .build()
                    .parse();

            cacheManager.getCache("products").put("products", productList);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        return productList;
    }

    public List<TradeWithProduct> transformTradeList_latest(List<Trade> tradeList) throws IOException {
        log.info("Enter transformTradeList");
        List<Product> productList = loadProductList();
        Cache cache = cacheManager.getCache("products");

        if (cache.get("products") != null) {
            productList = (List<Product>) cache.get("products").get();
        } else {
            productList = loadProductList();
        }
        Map<String, String> productMap =
                productListMap(productList);
        List<TradeWithProduct> tradeWithProductList = tradeList.stream().
                map(trade -> productMap.get(trade.getProduct_id()) == null ? missingProductTracker(trade) : new TradeWithProduct(trade.getDate(), productMap.get(trade.getProduct_id()),
                        trade.getCurrency(), trade.getPrice().toString())).filter(
                        trade -> {
                            return isDateFormatValid(trade.getDate());
                        }
                )
                .collect(toList());
        return tradeWithProductList;
    }


    private TradeWithProduct missingProductTracker(Trade trade) {
        log.info("Product name is missing for a given product id {}", trade.getProduct_id());
        System.out.println("Product name is missing for a given product id" + trade.getProduct_id());
        return new TradeWithProduct(trade.getDate(), MISSING_PRODUCT_NAME,
                trade.getCurrency(), trade.getPrice().toString());
    }

    private Map<String, String> productListMap(List<Product> productList) {
        Map<String, String> productListMap = productList.stream().collect(
                Collectors.toMap(product -> product.getProduct_id(), product -> product.getProduct_name()));
        return productListMap;
    }

    private static final ThreadLocal<DateFormat> dateFormatThreadLocal = new ThreadLocal<DateFormat>() {
        @Override
        protected DateFormat initialValue() {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
            dateFormat.setLenient(false);
            return dateFormat;
        }
    };

    public static boolean isDateFormatValid(String dateString) {
        try {
            DateFormat dateFormat = dateFormatThreadLocal.get();
            synchronized (dateFormat) {
                return dateString.equals(dateFormat.format(dateFormat.parse(dateString)));
            }
        } catch (ParseException e) {
            log.info("Given date format from trade.csv file is invalid {}", dateString);
            return false;
        }
    }
}
