# TradeFileProcessor
TradeFile processor is responsible to map trade vs products 

Please follow the below specified steps to run the TradeFile Processor Coding Test


Problem Summary :
=================
Two different services has been implemented inorder to achieve the trade vs product mapping Task

1. Trade Adaptor Service - It receive the inbound trade file by POST request and read the incoming trade.csv file and writes in to internal temp cache/directory and returns 200 status to the caller.

2. Trade Processor Service - This service is responsible to read the trade.csv file from internal temp dir and picks up product.csv from local folder and does the mapping
                              * Record which has the invalid date gets rejected and error message get logged in logger
                              * If the matching product name is not available then it still add that record with product name as "Missing product Name" and it                                   logs that record as well
                              * Product data stored in CaffineCache and internally accessed without reading the file or keeping the file open during trade                                       mapping.
                              * Final mapped records written in a file and stored in project home directory 
                                                [ex : trade_inbound-20230216-010625_processed_2023-02-16_01-07-43 ]

Steps to run the App
=====================
1. Checkout the project from github and setup the workspace with JDK 17 version
2. once the mvn clean install execution please setup curl env in your local system
3. start the "Trade Adaptor" service from intelliJ
4. Copy the trade.csv and product.csv from src\main\resource folder from TradeAdaptor app and save under c:\tmp\ location
5. Run following command in your curl to post trade.csv file [I saved trade.csv in my c:\tmp\trade.csv] - make sure your 9091 port is not used by any other processes
If you want to save your trade.csv and product.csv in a different folder location then you have to change the respective paths in curl command and application.properties file


curl -v -F 'file=@trade.csv' http://localhost:9091/tradeupload

6. You will receive following response in your Curl command window

Trade Inbound Files Received successfully*

7. Now Start 'TradeProcessor' Spring application from your intelliJ/Editor
8. go to your "java.io.tmpdir" folder in your local system and check whether the file which is transferred by our Adaptor service [in my system the tempdir is located under
  C:\Users\<user_name>\AppData\Local\Temp] reached or not [File name similar to this format :trade_inbound-20230216-010625]
9. TradeProcessor picks up Trade.csv and load the product.csv from CaffineCache and execute the mapping logic
10. Mapped records written under your user.home location of your workspace with following filename format - [Ex : trade_inbound-20230216-011914_processed_2023-02-16_01-19-38]
=======================================================
Input trade.csv :
=======================================================
date,product_id,currency,price
20210505,1,EUR,10.0
20220505,2,EUR,20.0
20230505,3,EUR,30.0
20240505,11,EUR,35.0  <Missing Product Name Record>
20251505,12,INR,45.0  <invalid Date format>
20263333,12,PAK,99.0  <invalid date format>
===================================================
product.csv
===================================================
product_id,product_name
1,Treasury Bills Domestic
2,Corporate Bonds Domestic
3,REPO Domestic
4,Interest Rate Swap
5,OTC index Option
6,Currency Options
=====================================================
Result after mapping the trade vs product
=====================================================
date,product_name,currency,price
20210505,Treasury Bills Domestic,EUR,10.0
20220505,Corporate Bonds Domestic,EUR,20.0
20230505,REPO Domestic,EUR,30.0
20240505,Missing Product Name,EUR,35.0
=====================================================

Things yet to be implemented
===================================================
* E2E test cases has to be written
* All local folder path has to be moved to property files
* File watcher has to be added in Tradeprocessor inorder to automate the mapping process
* Outboud file has to be moved to output folder




