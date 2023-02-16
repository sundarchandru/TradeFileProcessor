package com.trade.file.util;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.opencsv.CSVWriter;
import com.trade.file.dto.TradeWithProduct;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CsvWriter {

    private static final int THREAD_POOL_SIZE = 10;

    public static void writeDataToCsvFile(String csvFilePathLoc, List<TradeWithProduct> myDataList) throws IOException {
        log.info("Entered into writeDataToCsvFile method with file path {}", csvFilePathLoc);
        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
        CSVWriter csvWriter = new CSVWriter(new FileWriter(appendTimestamp(csvFilePathLoc)), CSVWriter.DEFAULT_SEPARATOR,
                CSVWriter.NO_QUOTE_CHARACTER, CSVWriter.DEFAULT_ESCAPE_CHARACTER, CSVWriter.DEFAULT_LINE_END);

        // write header row
        csvWriter.writeNext(new String[]{"date", "product_name", "currency", "price"});

        for (TradeWithProduct myData : myDataList) {
            executorService.execute(() -> {
                csvWriter.writeNext(new String[]{myData.getDate(), myData.getProduct_name(), myData.getCurrency(), myData.getPrice()});
            });
        }

        executorService.shutdown();
        while (!executorService.isTerminated()) {
            // wait until all tasks are finished
        }
        csvWriter.close();
    }


    public static String appendTimestamp(String fileName) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
        LocalDateTime now = LocalDateTime.now();
        String timestamp = dtf.format(now);
        int dotIndex = fileName.lastIndexOf(".");
        if (dotIndex == -1) {
            return fileName + "_" + timestamp;
        } else {
            String name = fileName.substring(0, dotIndex);
            String extension = fileName.substring(dotIndex);
            return name + "_" + timestamp + extension;
        }
    }
}

