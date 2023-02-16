package com.trade.adaptor.controller;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

@RestController
@RequestMapping("/tradeupload")
@EnableWebMvc
@RequiredArgsConstructor
public class TradeAdaptorController {


    @PostMapping(consumes = {MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<String> processTradeCSV(@RequestParam("file") MultipartFile file) throws IOException {

        ExecutorService executor = Executors.newFixedThreadPool(10);
        executor.execute(() -> {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd-HHmmss");
            String timestamp = dateFormat.format(new Date());
            String filePath = System.getProperty("java.io.tmpdir") + "trade_inbound" + "-" + timestamp;
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
                String line;
                BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()));
                while ((line = reader.readLine()) != null) {
                    writer.write(line);
                    writer.newLine();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        });
        return ResponseEntity.ok("Trade Inbound Files Received successfully");
    }


}
